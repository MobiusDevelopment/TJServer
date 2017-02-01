/*
 * This file is part of TJServer.
 * 
 * TJServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TJServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package tera.gameserver.model.npc.interaction.dialogs;

import tera.Config;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.BuyableItem;
import tera.gameserver.model.SellableItem;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ShopReplyPacket;
import tera.gameserver.network.serverpackets.ShopTradePacket;
import tera.gameserver.templates.ItemTemplate;

import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.array.FuncElement;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public final class ShopDialog extends AbstractDialog
{
	private static final FuncElement<BuyableItem> BUYABLE_ITEM_FOLD = item -> item.fold();
	private static final FuncElement<SellableItem> SELLABLE_ITEM_FOLD = item -> item.fold();
	
	public static ShopDialog newInstance(Npc npc, ItemTemplate[][] sections, Table<IntKey, ItemTemplate> availableItems, Player player, Bank bank, int sectionId, float resultTax)
	{
		final ShopDialog dialog = (ShopDialog) DialogType.SHOP_WINDOW.newInstance();
		dialog.availableItems = availableItems;
		dialog.npc = npc;
		dialog.player = player;
		dialog.bank = bank;
		dialog.sections = sections;
		dialog.sectionId = sectionId;
		dialog.resultTax = resultTax;
		return dialog;
	}
	
	private ItemTemplate[][] sections;
	private Table<IntKey, ItemTemplate> availableItems;
	private final Array<BuyableItem> buyItems;
	private final Array<SellableItem> sellItems;
	private Bank bank;
	private int sectionId;
	private float resultTax;
	
	protected ShopDialog()
	{
		buyItems = Arrays.toArray(BuyableItem.class, 8);
		sellItems = Arrays.toArray(SellableItem.class, 8);
	}
	
	public synchronized boolean addBuyItem(int itemId, long count)
	{
		if (count < 1)
		{
			return false;
		}
		
		if (Arrays.contains(Config.WORLD_DONATE_ITEMS, itemId))
		{
			log.warning(this, "not added donate item for " + itemId);
			return false;
		}
		
		final ItemTemplate template = availableItems.get(itemId);
		
		if (template == null)
		{
			log.warning(this, new Exception("not found template"));
			return false;
		}
		
		final Array<BuyableItem> buyItems = getBuyItems();
		
		if (!template.isStackable() && (buyItems.size() < 8))
		{
			buyItems.add(BuyableItem.newInstance(template, 1));
			return true;
		}
		
		if (template.isStackable())
		{
			final BuyableItem[] array = buyItems.array();
			
			for (int i = 0, length = buyItems.size(); i < length; i++)
			{
				final BuyableItem item = array[i];
				
				if (item.getItemId() == itemId)
				{
					item.addCount(count);
					return true;
				}
			}
			
			if (buyItems.size() < 8)
			{
				buyItems.add(BuyableItem.newInstance(template, count));
				return true;
			}
		}
		
		return false;
	}
	
	public synchronized boolean addSellItem(int itemId, int count, int index)
	{
		if (count < 1)
		{
			return false;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		final Inventory inventory = player.getInventory();
		inventory.lock();
		
		try
		{
			final Cell cell = inventory.getCell(index);
			
			if (cell == null)
			{
				log.warning(this, new Exception("not found cell"));
				return false;
			}
			
			final ItemInstance old = cell.getItem();
			
			if ((old == null) || (old.getItemId() != itemId))
			{
				return false;
			}
			
			if (!old.isSellable())
			{
				player.sendMessage("This item can not be sold.");
				return false;
			}
			
			final Array<SellableItem> sellItems = getSellItems();
			
			if (!old.isStackable())
			{
				if ((sellItems.size() > 7) || sellItems.contains(old))
				{
					return false;
				}
				
				sellItems.add(SellableItem.newInstance(old, inventory, 1));
				return true;
			}
			
			if (count > old.getItemCount())
			{
				return false;
			}
			
			final int i = sellItems.indexOf(old);
			
			if (i < 0)
			{
				if (sellItems.size() > 7)
				{
					return false;
				}
				
				sellItems.add(SellableItem.newInstance(old, inventory, count));
				return true;
			}
			final SellableItem sell = sellItems.get(i);
			
			if ((sell.getCount() + count) > old.getItemCount())
			{
				return false;
			}
			
			sell.addCount(count);
			return true;
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	@Override
	public synchronized boolean apply()
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		final Inventory inventory = player.getInventory();
		final Array<SellableItem> sellItems = getSellItems();
		final Array<BuyableItem> buyItems = getBuyItems();
		final Bank bank = getBank();
		final float tax = getResultTax();
		final GameLogManager gameLogger = GameLogManager.getInstance();
		lock(bank);
		
		try
		{
			inventory.lock();
			
			try
			{
				long availableMoney = inventory.getMoney();
				final SellableItem[] sellArray = sellItems.array();
				final BuyableItem[] buyArray = buyItems.array();
				
				if (!sellItems.isEmpty())
				{
					for (int i = 0, length = sellItems.size(); i < length; i++)
					{
						if (!sellArray[i].check())
						{
							return false;
						}
					}
					
					for (int i = 0, length = sellItems.size(); i < length; i++)
					{
						availableMoney += sellArray[i].getSellPrice();
					}
				}
				
				long neededMoney = 0;
				long result = 0;
				
				if (!buyItems.isEmpty())
				{
					for (int i = 0, length = buyItems.size(); i < length; i++)
					{
						neededMoney += (buyArray[i].getBuyPrice() + tax);
					}
				}
				
				if ((neededMoney > Integer.MAX_VALUE) || (neededMoney > availableMoney))
				{
					return false;
				}
				
				if (!sellItems.isEmpty())
				{
					for (int i = 0, length = sellItems.size(); i < length; i++)
					{
						final SellableItem sell = sellArray[i];
						final long money = sell.getSellPrice();
						final ItemInstance item = sell.getItem();
						final long count = sell.getCount();
						inventory.addMoney(money);
						result += money;
						sell.deleteItem();
						sell.fold();
						gameLogger.writeItemLog(player.getName() + " sell item [id = " + item.getItemId() + ", count = " + count + ", name = " + item.getName() + "] for " + money + " gold");
					}
				}
				
				if (!buyItems.isEmpty())
				{
					for (int i = 0, length = buyItems.size(); i < length; i++)
					{
						final BuyableItem buy = buyArray[i];
						final ItemTemplate item = buy.getItem();
						final long count = buy.getCount();
						final long price = (long) (buy.getBuyPrice() * tax);
						
						if (inventory.addItem(buy.getItemId(), buy.getCount(), "Merchant"))
						{
							inventory.subMoney(price);
							gameLogger.writeItemLog(player.getName() + " buy item [id = " + item.getItemId() + ", count = " + count + ", name = " + item.getName() + "] for " + price + " gold");
						}
						
						if (bank != null)
						{
							bank.addMoney(price - buy.getBuyPrice());
						}
						
						result -= price;
						buy.fold();
					}
				}
				
				buyItems.clear();
				sellItems.clear();
				
				if (result < 0)
				{
					PacketManager.showPaidGold(player, (int) -result);
				}
				else if (result > 0)
				{
					PacketManager.showAddGold(player, (int) result);
				}
				
				PacketManager.showShopDialog(player, this);
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(player);
				return true;
			}
			
			finally
			{
				inventory.unlock();
			}
		}
		
		finally
		{
			unlock(bank);
		}
	}
	
	public Bank getBank()
	{
		return bank;
	}
	
	@Override
	public void finalyze()
	{
		final Array<BuyableItem> buyItems = getBuyItems();
		buyItems.apply(BUYABLE_ITEM_FOLD);
		buyItems.clear();
		final Array<SellableItem> sellItems = getSellItems();
		sellItems.apply(SELLABLE_ITEM_FOLD);
		sellItems.clear();
		super.finalyze();
	}
	
	public Array<BuyableItem> getBuyItems()
	{
		return buyItems;
	}
	
	public synchronized long getBuyPrice()
	{
		long price = 0;
		final BuyableItem[] array = buyItems.array();
		final float tax = getResultTax();
		
		for (int i = 0, length = buyItems.size(); i < length; i++)
		{
			price += (array[i].getBuyPrice() * tax);
		}
		
		return price;
	}
	
	public Array<SellableItem> getSellItems()
	{
		return sellItems;
	}
	
	public synchronized long getSellPrice()
	{
		long price = 0;
		final SellableItem[] array = sellItems.array();
		
		for (int i = 0, length = sellItems.size(); i < length; i++)
		{
			price += array[i].getSellPrice();
		}
		
		return price;
	}
	
	public float getResultTax()
	{
		return resultTax;
	}
	
	@Override
	public DialogType getType()
	{
		return DialogType.SHOP_WINDOW;
	}
	
	@Override
	public synchronized boolean init()
	{
		if (!super.init())
		{
			return false;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		player.sendPacket(ShopReplyPacket.getInstance(sections, player, sectionId), true);
		player.sendPacket(ShopTradePacket.getInstance(this), true);
		return true;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public synchronized boolean subBuyItem(int itemId, int count)
	{
		if (count < 1)
		{
			return false;
		}
		
		final Array<BuyableItem> buyItems = getBuyItems();
		final BuyableItem[] array = buyItems.array();
		
		for (int i = 0, length = buyItems.size(); i < length; i++)
		{
			final BuyableItem item = array[i];
			
			if (item.getItemId() == itemId)
			{
				item.subCount(count);
				
				if (item.getCount() < 1)
				{
					buyItems.fastRemove(i);
					item.fold();
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public synchronized boolean subSellItem(int itemId, int count, int objectId)
	{
		if (count < 1)
		{
			return false;
		}
		
		final Array<SellableItem> sellItems = getSellItems();
		final SellableItem[] array = sellItems.array();
		
		for (int i = 0, length = sellItems.size(); i < length; i++)
		{
			final SellableItem sell = array[i];
			
			if (sell.getObjectId() == objectId)
			{
				sell.subCount(count);
				
				if (sell.getCount() < 1)
				{
					sellItems.fastRemove(i);
					sell.fold();
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public void lock(Bank bank)
	{
		if (bank != null)
		{
			bank.lock();
		}
	}
	
	public void unlock(Bank bank)
	{
		if (bank != null)
		{
			bank.unlock();
		}
	}
}
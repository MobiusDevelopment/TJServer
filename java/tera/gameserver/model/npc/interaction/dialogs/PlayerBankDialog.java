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

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.PlayerBankPanel;

/**
 * @author Ronn
 */
public final class PlayerBankDialog extends AbstractDialog implements BankDialog
{
	/**
	 * Method newInstance.
	 * @param npc Npc
	 * @param player Player
	 * @return PlayerBankDialog
	 */
	public static final PlayerBankDialog newInstance(Npc npc, Player player)
	{
		final PlayerBankDialog dialog = (PlayerBankDialog) DialogType.PLAYER_BANK.newInstance();
		dialog.npc = npc;
		dialog.player = player;
		dialog.startCell = 0;
		return dialog;
	}
	
	private int startCell;
	
	/**
	 * Method addItem.
	 * @param index int
	 * @param itemId int
	 * @param count int
	 * @see tera.gameserver.model.npc.interaction.dialogs.BankDialog#addItem(int, int, int)
	 */
	@Override
	public synchronized void addItem(int index, int itemId, int count)
	{
		if ((index < 0) || (count < 1))
		{
			return;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final Inventory inventory = player.getInventory();
		final Bank bank = player.getBank();
		
		if ((bank == null) || (inventory == null))
		{
			log.warning(this, new Exception("not found bank or inventory"));
			return;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		inventory.lock();
		
		try
		{
			final Cell cell = inventory.getCell(index);
			
			if ((cell == null) || cell.isEmpty())
			{
				return;
			}
			
			if (cell.getItemCount() < count)
			{
				return;
			}
			
			final ItemInstance item = cell.getItem();
			
			if (item == null)
			{
				log.warning(this, new Exception("not found item"));
				return;
			}
			
			if (!item.isBank())
			{
				player.sendMessage(MessageType.THAT_ITEM_CANTT_BE_STORED_IN_THE_BANK);
				return;
			}
			
			bank.lock();
			
			try
			{
				if (!item.isStackable())
				{
					if (bank.putItem(item) && !inventory.removeItem(item))
					{
						bank.removeItem(item);
					}
					
					eventManager.notifyPlayerBankChanged(player, startCell);
					eventManager.notifyInventoryChanged(player);
				}
				else if (bank.addItem(itemId, count))
				{
					if (!inventory.removeItem(itemId, count))
					{
						bank.removeItem(itemId, count);
					}
					
					eventManager.notifyPlayerBankChanged(player, startCell);
					eventManager.notifyInventoryChanged(player);
				}
			}
			
			finally
			{
				bank.unlock();
			}
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	/**
	 * Method addMoney.
	 * @param money int
	 * @see tera.gameserver.model.npc.interaction.dialogs.BankDialog#addMoney(int)
	 */
	@Override
	public synchronized void addMoney(int money)
	{
		if (money < 1)
		{
			return;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final Inventory inventory = player.getInventory();
		final Bank bank = player.getBank();
		
		if ((inventory == null) || (bank == null))
		{
			log.warning(this, new Exception("not found bank or inventory"));
			return;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		inventory.lock();
		
		try
		{
			if (inventory.getMoney() < money)
			{
				return;
			}
			
			inventory.subMoney(money);
			bank.addMoney(money);
			eventManager.notifyPlayerBankChanged(player, startCell);
			eventManager.notifyInventoryChanged(player);
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	/**
	 * Method apply.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#apply()
	 */
	@Override
	public synchronized boolean apply()
	{
		return false;
	}
	
	/**
	 * Method getItem.
	 * @param index int
	 * @param itemId int
	 * @param count int
	 * @see tera.gameserver.model.npc.interaction.dialogs.BankDialog#getItem(int, int, int)
	 */
	@Override
	public synchronized void getItem(int index, int itemId, int count)
	{
		index += startCell;
		
		if ((index < 0) || (count < 1))
		{
			return;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final Inventory inventory = player.getInventory();
		final Bank bank = player.getBank();
		
		if ((bank == null) || (inventory == null))
		{
			log.warning(this, new Exception("not found bank or inventory"));
			return;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		bank.lock();
		
		try
		{
			final Cell cell = bank.getCell(index);
			
			if ((cell == null) || cell.isEmpty())
			{
				return;
			}
			
			if ((cell.getItemId() != itemId) || (cell.getItemCount() < count))
			{
				return;
			}
			
			final ItemInstance item = cell.getItem();
			
			if (item == null)
			{
				log.warning(this, new Exception("not found item"));
				return;
			}
			
			inventory.lock();
			
			try
			{
				if (!item.isStackable())
				{
					if (!inventory.putItem(item))
					{
						player.sendMessage(MessageType.INVENTORY_IS_FULL);
					}
					else
					{
						if (!bank.removeItem(item))
						{
							inventory.removeItem(item);
						}
						
						eventManager.notifyPlayerBankChanged(player, startCell);
						eventManager.notifyInventoryChanged(player);
					}
					
					return;
				}
				
				try
				{
					if (!inventory.addItem(itemId, count, "Bank"))
					{
						player.sendMessage(MessageType.INVENTORY_IS_FULL);
					}
					else
					{
						if (!bank.removeItem(itemId, count))
						{
							inventory.removeItem(itemId, count);
						}
						
						eventManager.notifyPlayerBankChanged(player, startCell);
						eventManager.notifyInventoryChanged(player);
					}
				}
				catch (Exception e)
				{
					log.warning(this, e);
				}
			}
			
			finally
			{
				inventory.unlock();
			}
		}
		
		finally
		{
			bank.unlock();
		}
	}
	
	/**
	 * Method getMoney.
	 * @param money int
	 * @see tera.gameserver.model.npc.interaction.dialogs.BankDialog#getMoney(int)
	 */
	@Override
	public synchronized void getMoney(int money)
	{
		if (money < 1)
		{
			return;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final Inventory inventory = player.getInventory();
		final Bank bank = player.getBank();
		
		if ((inventory == null) || (bank == null))
		{
			log.warning(this, new Exception("not found bank or inventory"));
			return;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		inventory.lock();
		
		try
		{
			if (bank.getMoney() < money)
			{
				return;
			}
			
			bank.subMoney(money);
			inventory.addMoney(money);
			eventManager.notifyPlayerBankChanged(player, startCell);
			eventManager.notifyInventoryChanged(player);
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	/**
	 * Method getType.
	 * @return DialogType
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#getType()
	 */
	@Override
	public DialogType getType()
	{
		return DialogType.PLAYER_BANK;
	}
	
	/**
	 * Method init.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#init()
	 */
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
		
		player.sendPacket(PlayerBankPanel.getInstance(player), true);
		PacketManager.updatePlayerBank(player, startCell);
		return true;
	}
	
	/**
	 * Method movingItem.
	 * @param oldCell int
	 * @param newCell int
	 * @see tera.gameserver.model.npc.interaction.dialogs.BankDialog#movingItem(int, int)
	 */
	@Override
	public void movingItem(int oldCell, int newCell)
	{
		oldCell += startCell;
		newCell += startCell;
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final Bank bank = player.getBank();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		bank.lock();
		
		try
		{
			final Cell start = bank.getCell(oldCell);
			final Cell end = bank.getCell(newCell);
			
			if ((start == null) || (end == null) || (start == end))
			{
				return;
			}
			
			final ItemInstance oldItem = start.getItem();
			start.setItem(end.getItem());
			end.setItem(oldItem);
			dbManager.updateLocationItem(end.getItem());
			dbManager.updateLocationItem(start.getItem());
			eventManager.notifyPlayerBankChanged(player, startCell);
		}
		
		finally
		{
			bank.unlock();
		}
	}
	
	/**
	 * Method setStartCell.
	 * @param startCell int
	 * @see tera.gameserver.model.npc.interaction.dialogs.BankDialog#setStartCell(int)
	 */
	@Override
	public void setStartCell(int startCell)
	{
		this.startCell = startCell;
		PacketManager.updatePlayerBank(player, startCell);
	}
	
	/**
	 * Method sort.
	 * @see tera.gameserver.model.npc.interaction.dialogs.BankDialog#sort()
	 */
	@Override
	public void sort()
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final Bank bank = player.getBank();
		bank.sort();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyPlayerBankChanged(player, startCell);
	}
}
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
import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildRank;
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
public final class GuildBankDialog extends AbstractDialog implements BankDialog
{
	public static GuildBankDialog newInstance(Npc npc, Player player)
	{
		final GuildBankDialog dialog = (GuildBankDialog) DialogType.GUILD_BANK.newInstance();
		dialog.npc = npc;
		dialog.player = player;
		dialog.startCell = 0;
		return dialog;
	}
	
	private int startCell;
	
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
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			player.sendMessage(MessageType.YOU_NOT_IN_GUILD);
			return;
		}
		
		final Inventory inventory = player.getInventory();
		final Bank bank = guild.getBank();
		
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
			
			if (!item.isStackable())
			{
				if (bank.putItem(item))
				{
					inventory.removeItem(item);
				}
				
				eventManager.notifyGuildBankChanged(player, startCell);
				eventManager.notifyInventoryChanged(player);
				return;
			}
			
			if (bank.addItem(itemId, count))
			{
				inventory.removeItem(itemId, count);
				eventManager.notifyGuildBankChanged(player, startCell);
				eventManager.notifyInventoryChanged(player);
			}
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
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
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			player.sendMessage(MessageType.YOU_NOT_IN_GUILD);
			return;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		final Inventory inventory = player.getInventory();
		final Bank bank = guild.getBank();
		
		if ((inventory == null) || (bank == null))
		{
			log.warning(this, new Exception("not found bank or inventory"));
			return;
		}
		
		inventory.lock();
		
		try
		{
			if (inventory.getMoney() < money)
			{
				return;
			}
			
			inventory.subMoney(money);
			bank.addMoney(money);
			eventManager.notifyGuildBankChanged(player, startCell);
			eventManager.notifyInventoryChanged(player);
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	@Override
	public synchronized boolean apply()
	{
		return false;
	}
	
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
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			player.sendMessage(MessageType.YOU_NOT_IN_GUILD);
			return;
		}
		
		final GuildRank rank = player.getGuildRank();
		
		if (!rank.isAccessBank())
		{
			player.sendMessage("You do not have access.");
			return;
		}
		
		final Inventory inventory = player.getInventory();
		final Bank bank = guild.getBank();
		
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
			
			if (!item.isStackable())
			{
				if (!inventory.putItem(item))
				{
					player.sendMessage(MessageType.INVENTORY_IS_FULL);
				}
				else
				{
					bank.removeItem(item);
					eventManager.notifyGuildBankChanged(player, startCell);
					eventManager.notifyInventoryChanged(player);
				}
				
				return;
			}
			
			if (!inventory.addItem(itemId, count, "Bank"))
			{
				player.sendMessage(MessageType.INVENTORY_IS_FULL);
			}
			else
			{
				bank.removeItem(itemId, count);
				eventManager.notifyGuildBankChanged(player, startCell);
				eventManager.notifyInventoryChanged(player);
			}
		}
		
		finally
		{
			bank.unlock();
		}
	}
	
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
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			player.sendMessage(MessageType.YOU_NOT_IN_GUILD);
			return;
		}
		
		final GuildRank rank = player.getGuildRank();
		
		if (!rank.isAccessBank())
		{
			player.sendMessage("You do not have access.");
			return;
		}
		
		final Inventory inventory = player.getInventory();
		final Bank bank = guild.getBank();
		
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
			eventManager.notifyGuildBankChanged(player, startCell);
			eventManager.notifyInventoryChanged(player);
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	@Override
	public DialogType getType()
	{
		return DialogType.GUILD_BANK;
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
		
		player.sendPacket(PlayerBankPanel.getInstance(player), true);
		PacketManager.updateGuildBank(player, startCell);
		return true;
	}
	
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
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			player.sendMessage(MessageType.YOU_NOT_IN_GUILD);
			return;
		}
		
		final GuildRank rank = player.getGuildRank();
		
		if (!rank.isAccessBank())
		{
			player.sendMessage("You do not have access.");
			return;
		}
		
		final Bank bank = guild.getBank();
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
			eventManager.notifyGuildBankChanged(player, startCell);
		}
		
		finally
		{
			bank.unlock();
		}
	}
	
	@Override
	public void setStartCell(int startCell)
	{
		this.startCell = startCell;
		PacketManager.updateGuildBank(player, startCell);
	}
	
	@Override
	public void sort()
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			player.sendMessage(MessageType.YOU_NOT_IN_GUILD);
			return;
		}
		
		final GuildRank rank = player.getGuildRank();
		
		if (!rank.isAccessBank())
		{
			player.sendMessage("You do not have access.");
			return;
		}
		
		final Bank bank = guild.getBank();
		bank.sort();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyGuildBankChanged(player, startCell);
	}
}
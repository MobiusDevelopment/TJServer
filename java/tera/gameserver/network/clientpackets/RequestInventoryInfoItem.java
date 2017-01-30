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
package tera.gameserver.network.clientpackets;

import tera.gameserver.model.Guild;
import tera.gameserver.model.World;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.Slot;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.InventoryItemInfo;

/**
 * @author Ronn
 */
public class RequestInventoryInfoItem extends ClientPacket
{
	private int objectId;
	private String name;
	private Player player;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
		player = null;
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.ReadeablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	public void readImpl()
	{
		player = owner.getOwner();
		readInt();
		readShort();
		objectId = readInt();
		readLong();
		readLong();
		readInt();
		readShort();
		readShort();
		name = readString();
	}
	
	@Override
	public void runImpl()
	{
		if ((player == null) || (name == null))
		{
			return;
		}
		
		Player target = null;
		
		if (name.equals(player.getName()))
		{
			target = player;
		}
		else
		{
			target = World.getAroundByName(Player.class, player, name);
			
			if (target == null)
			{
				target = World.getPlayer(name);
			}
		}
		
		if (target == null)
		{
			return;
		}
		
		final Equipment equipment = target.getEquipment();
		
		if (equipment == null)
		{
			return;
		}
		
		equipment.lock();
		
		try
		{
			final Slot slot = equipment.getSlotForObjectId(objectId);
			
			if (slot != null)
			{
				player.sendPacket(InventoryItemInfo.getInstance(slot.getIndex(), slot.getItem()), true);
				return;
			}
		}
		
		finally
		{
			equipment.unlock();
		}
		final Inventory inventory = target.getInventory();
		
		if (inventory == null)
		{
			return;
		}
		
		inventory.lock();
		
		try
		{
			final Cell cell = inventory.getCellForObjectId(objectId);
			
			if (cell != null)
			{
				player.sendPacket(InventoryItemInfo.getInstance(cell.getIndex(), cell.getItem()), true);
				return;
			}
		}
		
		finally
		{
			inventory.unlock();
		}
		Bank bank = target.getBank();
		
		if (bank == null)
		{
			return;
		}
		
		bank.lock();
		
		try
		{
			final ItemInstance item = bank.getItemForObjectId(objectId);
			
			if (item != null)
			{
				player.sendPacket(InventoryItemInfo.getInstance(0, item), true);
				return;
			}
		}
		
		finally
		{
			bank.unlock();
		}
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			return;
		}
		
		bank = guild.getBank();
		bank.lock();
		
		try
		{
			final ItemInstance item = bank.getItemForObjectId(objectId);
			
			if (item != null)
			{
				player.sendPacket(InventoryItemInfo.getInstance(0, item), true);
				return;
			}
		}
		
		finally
		{
			bank.unlock();
		}
	}
}
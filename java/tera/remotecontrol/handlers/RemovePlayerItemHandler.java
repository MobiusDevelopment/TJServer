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
package tera.remotecontrol.handlers;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.World;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.Slot;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;
import tera.remotecontrol.PacketType;

import rlib.concurrent.Locks;

/**
 * @author Ronn
 * @created 09.04.2012
 */
public class RemovePlayerItemHandler implements PacketHandler
{
	public static final RemovePlayerItemHandler instance = new RemovePlayerItemHandler();
	
	/**
	 * Method processing.
	 * @param packet Packet
	 * @return Packet
	 * @see tera.remotecontrol.PacketHandler#processing(Packet)
	 */
	@Override
	public Packet processing(Packet packet)
	{
		final Player player = World.getPlayer(packet.nextString());
		
		if (player == null)
		{
			return null;
		}
		
		final int objectId = packet.nextInt();
		final Inventory inventory = player.getInventory();
		final Equipment equipment = player.getEquipment();
		
		if ((inventory == null) || (equipment == null))
		{
			return null;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		Locks.lock(inventory, equipment);
		
		try
		{
			final Cell cell = inventory.getCellForObjectId(objectId);
			
			if (cell != null)
			{
				final ItemInstance item = cell.getItem();
				item.setOwnerId(0);
				cell.setItem(null);
				dbManager.updateLocationItem(item);
				eventManager.notifyInventoryChanged(player);
				return new Packet(PacketType.RESPONSE);
			}
			
			final Slot slot = equipment.getSlotForObjectId(objectId);
			
			if (slot != null)
			{
				final ItemInstance item = slot.getItem();
				item.setOwnerId(0);
				slot.setItem(null);
				dbManager.updateLocationItem(item);
				eventManager.notifyEquipmentChanged(player);
				return new Packet(PacketType.RESPONSE);
			}
		}
		
		finally
		{
			Locks.unlock(inventory, equipment);
		}
		return null;
	}
}

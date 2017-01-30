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

import java.util.ArrayList;

import tera.gameserver.model.World;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.Slot;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;
import tera.remotecontrol.PacketType;

/**
 * @author Ronn
 * @created 09.04.2012
 */
public class UpdateEquipmentItemsHandler implements PacketHandler
{
	public static final UpdateEquipmentItemsHandler instance = new UpdateEquipmentItemsHandler();
	
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
		
		final Equipment equipment = player.getEquipment();
		
		if (equipment == null)
		{
			return null;
		}
		
		final ArrayList<Object[]> items = new ArrayList<>();
		equipment.lock();
		
		try
		{
			final Slot[] slots = equipment.getSlots();
			
			for (Slot slot : slots)
			{
				if (slot.isEmpty())
				{
					continue;
				}
				
				final ItemInstance item = slot.getItem();
				items.add(new Object[]
				{
					item.getName(),
					slot.getIndex(),
					item.getObjectId(),
				});
			}
		}
		
		finally
		{
			equipment.unlock();
		}
		return new Packet(PacketType.RESPONSE, items);
	}
}

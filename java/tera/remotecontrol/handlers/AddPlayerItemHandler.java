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

import tera.Config;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.World;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;
import tera.remotecontrol.PacketType;

import rlib.logging.Loggers;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 09.04.2012
 */
public class AddPlayerItemHandler implements PacketHandler
{
	public static final AddPlayerItemHandler instance = new AddPlayerItemHandler();
	
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
		
		final int itemId = packet.nextInt();
		final long count = packet.nextLong();
		final ItemTable itemTable = ItemTable.getInstance();
		final ItemTemplate template = itemTable.getItem(itemId);
		
		if (template == null)
		{
			return null;
		}
		
		if (Arrays.contains(Config.WORLD_DONATE_ITEMS, template.getItemId()))
		{
			Loggers.warning(this, new Exception("not create donate item for id " + template.getItemId()));
			return null;
		}
		
		final ItemInstance item = template.newInstance();
		
		if (item == null)
		{
			return null;
		}
		
		item.setAutor("RemoteAdmin");
		
		if (item.isStackable())
		{
			item.setItemCount(count);
		}
		
		if (player.getInventory().putItem(item))
		{
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyInventoryChanged(player);
			return new Packet(PacketType.RESPONSE);
		}
		
		return null;
	}
}

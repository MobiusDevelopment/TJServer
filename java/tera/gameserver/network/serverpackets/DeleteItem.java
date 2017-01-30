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
package tera.gameserver.network.serverpackets;

import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class DeleteItem extends ServerPacket
{
	private static final ServerPacket instance = new DeleteItem();
	
	/**
	 * Method getInstance.
	 * @param item ItemInstance
	 * @return DeleteItem
	 */
	public static DeleteItem getInstance(ItemInstance item)
	{
		final DeleteItem packet = (DeleteItem) instance.newInstance();
		packet.objectId = item.getObjectId();
		packet.subId = item.getSubId();
		return packet;
	}
	
	private int objectId;
	
	private int subId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.ITEM_DELETE;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeInt(objectId);
		writeInt(subId);
	}
}

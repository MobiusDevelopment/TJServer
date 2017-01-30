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

import java.nio.ByteBuffer;

import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class MessageAddedItem extends ServerPacket
{
	private static final MessageAddedItem instance = new MessageAddedItem();
	private static final char split = 0x0B;
	
	/**
	 * Method getInstance.
	 * @param name String
	 * @param itemId int
	 * @param itemCount int
	 * @return MessageAddedItem
	 */
	public static MessageAddedItem getInstance(String name, int itemId, int itemCount)
	{
		final MessageAddedItem packet = (MessageAddedItem) instance.newInstance();
		final StringBuilder builder = new StringBuilder();
		builder.append("@379");
		builder.append(split);
		builder.append("UserName");
		builder.append(split);
		builder.append(name);
		builder.append(split);
		builder.append("ItemAmount");
		builder.append(split);
		builder.append(itemCount);
		builder.append(split);
		builder.append("ItemName");
		builder.append(split);
		builder.append("@item:").append(itemId);
		packet.itemId = itemId;
		packet.builder = builder;
		return packet;
	}
	
	private StringBuilder builder;
	private int itemId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.MESSAGE_ADD_ITEM;
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.SendablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	/**
	 * Method writeImpl.
	 * @param buffer ByteBuffer
	 */
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeShort(buffer, 32);
		writeInt(buffer, itemId);
		writeInt(buffer, 0);
		writeLong(buffer, 1);
		writeLong(buffer, 0);
		writeShort(buffer, 0);
		writeString(buffer, builder.toString());
	}
}
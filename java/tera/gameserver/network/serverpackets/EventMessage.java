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

import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class EventMessage extends ServerPacket
{
	private static final ServerPacket instance = new EventMessage();
	
	/**
	 * Method getInstance.
	 * @param head String
	 * @param message String
	 * @param info String
	 * @return EventMessage
	 */
	public static EventMessage getInstance(String head, String message, String info)
	{
		final EventMessage packet = (EventMessage) instance.newInstance();
		packet.head = head;
		packet.message = message;
		packet.info = info;
		return packet;
	}
	
	private String head;
	private String message;
	private String info;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		head = null;
		info = null;
		message = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.EVENT_MESSAGE;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeShort(6);
		writeString(head);
		buffer.position(buffer.position() - 2);
		writeShort(11);
		writeString(message);
		buffer.position(buffer.position() - 2);
		writeShort(11);
		writeString(info);
	}
}
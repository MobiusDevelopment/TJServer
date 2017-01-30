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

import tera.gameserver.model.Character;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class CharTurn extends ServerPacket
{
	private static final ServerPacket instance = new CharTurn();
	
	/**
	 * Method getInstance.
	 * @param character Character
	 * @param newHeading int
	 * @param time int
	 * @return CharTurn
	 */
	public static CharTurn getInstance(Character character, int newHeading, int time)
	{
		final CharTurn packet = (CharTurn) instance.newInstance();
		
		if (character == null)
		{
			log.warning(packet, new Exception("not found character"));
			return null;
		}
		
		packet.objectId = character.getObjectId();
		packet.subId = character.getSubId();
		packet.time = time;
		packet.newHeading = newHeading;
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int time;
	private int newHeading;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.CHAR_TURN;
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
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeShort(buffer, newHeading);
		writeShort(buffer, time);
		writeShort(buffer, 0);
	}
}
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
public class NameColor extends ServerPacket
{
	public static final int COLOR_NORMAL = 1;
	public static final int COLOR_BLUE = 2;
	public static final int COLOR_RED = 3;
	public static final int COLOR_ORANGE = 4;
	public static final int COLOR_RED_PVP = 5;
	public static final int COLOR_GREEN = 6;
	public static final int COLOR_LIGHT_BLUE = 7;
	public static final int COLOR_RED_STATUS = 8;
	private static final ServerPacket instance = new NameColor();
	
	/**
	 * Method getInstance.
	 * @param color int
	 * @param character Character
	 * @return NameColor
	 */
	public static NameColor getInstance(int color, Character character)
	{
		final NameColor packet = (NameColor) instance.newInstance();
		packet.color = color;
		packet.objectId = character.getObjectId();
		packet.subId = character.getSubId();
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int color;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.NAME_COLOR;
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
		writeInt(buffer, color);
	}
}
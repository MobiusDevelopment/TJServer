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

import tera.gameserver.model.SayType;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;

/**
 * @author Ronn
 */
public class CharSay extends ServerPacket
{
	private static final ServerPacket instance = new CharSay();
	
	/**
	 * Method getInstance.
	 * @param name String
	 * @param text String
	 * @param type SayType
	 * @param objectId int
	 * @param subId int
	 * @return CharSay
	 */
	public static CharSay getInstance(String name, String text, SayType type, int objectId, int subId)
	{
		return getInstance(name, text, type, objectId, subId, false);
	}
	
	public static CharSay getInstance(String name, String text, SayType type, int objectId, int subId, boolean isGM)
	{
		final CharSay packet = (CharSay) instance.newInstance();
		packet.name = name;
		packet.text = text;
		packet.type = type;
		packet.objectId = objectId;
		packet.subId = subId;
		packet.isGmAccount = (isGM) ? 1 : 0;
		return packet;
	}
	
	private String text;
	
	private String name;
	
	private SayType type;
	
	private int objectId;
	
	private int subId;
	
	private int isGmAccount;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		text = null;
		name = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SAY;
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
	 * Method write.
	 * @param buffer ByteBuffer
	 * @see rlib.network.packets.SendablePacket#write(ByteBuffer)
	 */
	@Override
	public void write(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeShort(buffer, 22);
		writeShort(buffer, 24 + Strings.length(name));
		writeInt(buffer, type.ordinal());
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeByte(buffer, 0);
		writeByte(buffer, isGmAccount);
		writeString(buffer, name);
		writeByte(buffer, 0);
		
		if ((name == null) || name.isEmpty())
		{
			writeShort(buffer, 0x2000);
		}
		
		writeByte(buffer, 0);
		writeString(buffer, text);
		writeByte(buffer, 0);
	}
}
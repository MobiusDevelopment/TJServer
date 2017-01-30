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

import tera.gameserver.model.Character;
import tera.gameserver.model.EmotionType;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class Emotion extends ServerPacket
{
	private static final ServerPacket instance = new Emotion();
	
	/**
	 * Method getInstance.
	 * @param character Character
	 * @param type EmotionType
	 * @return Emotion
	 */
	public static Emotion getInstance(Character character, EmotionType type)
	{
		final Emotion packet = (Emotion) instance.newInstance();
		packet.objectId = character.getObjectId();
		packet.subId = character.getSubId();
		packet.type = type;
		return packet;
	}
	
	private EmotionType type;
	private int objectId;
	private int subId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.EMOTION;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(objectId);
		writeInt(subId);
		writeInt(type.ordinal());
		writeInt(0);
		writeByte(0);
	}
}
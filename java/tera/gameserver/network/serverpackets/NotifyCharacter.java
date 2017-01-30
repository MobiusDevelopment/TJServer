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
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class NotifyCharacter extends ServerPacket
{
	/**
	 */
	public static enum NotifyType
	{
		BAD_NETWORK,
		BAD_ENERGY,
		RED_FULL_EYE,
		RED_HALF_EYE,
		NOTICE,
		PREPARE_SPELL,
		NOTICE_AGRRESION,
		NOTICE_SUB_AGRRESSION,
		NOTICE_THINK,
		NONE9,
		NONE10,
		NONE11,
		NONE12,
		READ_REAR,
		YELLOW_QUESTION,
		NONE15,
		NONE16,
		NONE17;
	}
	
	private static final ServerPacket instance = new NotifyCharacter();
	
	/**
	 * Method getInstance.
	 * @param character Character
	 * @param type NotifyType
	 * @return NotifyCharacter
	 */
	public static NotifyCharacter getInstance(Character character, NotifyType type)
	{
		final NotifyCharacter packet = (NotifyCharacter) instance.newInstance();
		packet.objectId = character.getObjectId();
		packet.subId = character.getSubId();
		packet.type = type;
		return packet;
	}
	
	private NotifyType type;
	private int objectId;
	private int subId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.NOTIFY_CHARACTER;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(objectId);
		writeInt(subId);
		writeInt(type.ordinal());
		writeByte(0);
	}
}
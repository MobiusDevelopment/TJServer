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
public class CharShieldBlock extends ServerPacket
{
	private static final ServerPacket instance = new CharShieldBlock();
	
	/**
	 * Method getInstance.
	 * @param attacked Character
	 * @return CharShieldBlock
	 */
	public static CharShieldBlock getInstance(Character attacked)
	{
		final CharShieldBlock packet = (CharShieldBlock) instance.newInstance();
		packet.objectId = attacked.getObjectId();
		packet.subId = attacked.getSubId();
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
		return ServerPacketType.SHIELD_BLOCK;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(objectId);
		writeInt(subId);
		writeInt(0x440D044D);
		writeInt(0x00000119);
	}
}
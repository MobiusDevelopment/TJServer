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
public class TargetHp extends ServerPacket
{
	private static final ServerPacket instance = new TargetHp();
	public static final int BLUE = 0;
	public static final int RED = 1;
	
	/**
	 * Method getInstance.
	 * @param target Character
	 * @param type int
	 * @return TargetHp
	 */
	public static TargetHp getInstance(Character target, int type)
	{
		final TargetHp packet = (TargetHp) instance.newInstance();
		packet.objectId = target.getObjectId();
		packet.subId = target.getSubId();
		packet.hp = target.getCurrentHp() / (float) target.getMaxHp();
		packet.type = type;
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int type;
	private float hp;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.TARGET_NPC_HP;
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
		writeFloat(buffer, hp);
		writeLong(buffer, type);
		writeByte(buffer, 0);
		writeInt(buffer, 0x00001F40);
		writeInt(buffer, 5);
	}
}
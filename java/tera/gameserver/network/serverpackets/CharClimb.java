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
public class CharClimb extends ServerPacket
{
	private static final ServerPacket instance = new CharClimb();
	
	/**
	 * Method getInstance.
	 * @param actor Character
	 * @param heading int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return CharClimb
	 */
	public static CharClimb getInstance(Character actor, int heading, float targetX, float targetY, float targetZ)
	{
		final CharClimb packet = (CharClimb) instance.newInstance();
		packet.objectId = actor.getObjectId();
		packet.subId = actor.getSubId();
		packet.x = actor.getX();
		packet.y = actor.getY();
		packet.z = actor.getZ();
		packet.heading = heading;
		packet.targetX = targetX;
		packet.targetY = targetY;
		packet.targetZ = targetZ;
		return packet;
	}
	
	private int objectId;
	
	private int subId;
	
	private float x;
	private float y;
	private float z;
	
	private int heading;
	
	private float targetX;
	private float targetY;
	private float targetZ;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.CHAR_CLIMB;
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
	protected final void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeFloat(buffer, x);
		writeFloat(buffer, y);
		writeFloat(buffer, z);
		writeShort(buffer, heading);
		writeFloat(buffer, targetX);
		writeFloat(buffer, targetY);
		writeFloat(buffer, targetZ);
		writeByte(buffer, 0);
	}
}
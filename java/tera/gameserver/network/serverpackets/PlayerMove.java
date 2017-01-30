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
import tera.gameserver.model.MoveType;
import tera.gameserver.network.ServerPacketType;

import rlib.geom.Geometry;

/**
 * @author Ronn
 */
public class PlayerMove extends ServerPacket
{
	private static final ServerPacket instance = new PlayerMove();
	
	/**
	 * Method getInstance.
	 * @param actor Character
	 * @param type MoveType
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param heading int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return PlayerMove
	 */
	public static PlayerMove getInstance(Character actor, MoveType type, float x, float y, float z, int heading, float targetX, float targetY, float targetZ)
	{
		final PlayerMove packet = (PlayerMove) instance.newInstance();
		packet.objectId = actor.getObjectId();
		packet.subId = actor.getSubId();
		packet.speed = (type == MoveType.JUMP) && (Geometry.getSquareDistance(x, y, z, targetX, targetY, z) < 100) ? 0 : actor.getRunSpeed();
		packet.type = type;
		packet.x = x;
		packet.y = y;
		packet.z = z;
		packet.heading = heading;
		packet.targetX = targetX;
		packet.targetY = targetY;
		packet.targetZ = targetZ;
		return packet;
	}
	
	private MoveType type;
	
	private float x;
	private float y;
	private float z;
	
	private int objectId;
	
	private int subId;
	
	private int heading;
	
	private int speed;
	
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
		return ServerPacketType.PLAYER_MOVE;
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
		writeShort(buffer, speed);
		writeFloat(buffer, targetX);
		writeFloat(buffer, targetY);
		writeFloat(buffer, targetZ);
		writeByte(buffer, type.ordinal());
		writeInt(buffer, 0);
	}
}
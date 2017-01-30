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

import tera.gameserver.model.worldobject.WorldObject;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class WorldObjectInfo extends ServerPacket
{
	private static final ServerPacket instance = new WorldObjectInfo();
	
	/**
	 * Method getInstance.
	 * @param object WorldObject
	 * @return WorldObjectInfo
	 */
	public static WorldObjectInfo getInstance(WorldObject object)
	{
		final WorldObjectInfo packet = (WorldObjectInfo) instance.newInstance();
		packet.objectId = object.getObjectId();
		packet.subId = object.getSubId();
		packet.x = object.getX();
		packet.y = object.getY();
		packet.z = object.getZ();
		return packet;
	}
	
	private int objectId;
	
	private int subId;
	
	private float x;
	private float y;
	private float z;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.BONFIRE_INFO;
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
		writeInt(buffer, 0);
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeInt(buffer, 1);
		writeFloat(buffer, x);
		writeFloat(buffer, y);
		writeFloat(buffer, z);
		writeInt(buffer, 0);
	}
}

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

import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class SummonInfo extends ServerPacket
{
	private static final ServerPacket instance = new SummonInfo();
	
	/**
	 * Method getInstance.
	 * @param summon Summon
	 * @return SummonInfo
	 */
	public static SummonInfo getInstance(Summon summon)
	{
		final SummonInfo packet = (SummonInfo) instance.newInstance();
		packet.objectId = summon.getObjectId();
		packet.subId = summon.getSubId();
		packet.x = summon.getX();
		packet.y = summon.getY();
		packet.z = summon.getZ();
		packet.heading = summon.getHeading();
		packet.id = summon.getTemplateId();
		packet.type = summon.getTemplateType();
		packet.spawned = summon.isSpawned() ? 1 : 0;
		return packet;
	}
	
	private int objectId;
	private int subId;
	private float x;
	private float y;
	private float z;
	private int heading;
	private int id;
	private int type;
	private int spawned;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SUMMON_INFO;
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
		writeShort(buffer, 109);
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeLong(buffer, 0);
		writeFloat(buffer, x);
		writeFloat(buffer, y);
		writeFloat(buffer, z);
		writeShort(buffer, heading);
		writeInt(buffer, 12);
		writeInt(buffer, id);
		writeShort(buffer, type);
		writeShort(buffer, 0);
		writeInt(buffer, 110);
		writeShort(buffer, 0);
		writeInt(buffer, 5);
		writeByte(buffer, 1);
		writeByte(buffer, 1);
		writeShort(buffer, spawned);
		writeLong(buffer, 0);
		writeLong(buffer, 0);
		writeLong(buffer, 0);
		writeLong(buffer, 0);
		writeLong(buffer, 0);
		writeShort(buffer, 0);
		writeByte(buffer, 0);
	}
}
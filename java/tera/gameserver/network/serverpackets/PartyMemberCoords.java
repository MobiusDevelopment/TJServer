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

import tera.gameserver.model.playable.Playable;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class PartyMemberCoords extends ServerPacket
{
	private static final ServerPacket instance = new PartyMemberCoords();
	
	/**
	 * Method getInstance.
	 * @param member Playable
	 * @return PartyMemberCoords
	 */
	public static PartyMemberCoords getInstance(Playable member)
	{
		final PartyMemberCoords packet = (PartyMemberCoords) instance.newInstance();
		packet.objectId = member.getObjectId();
		packet.zoneId = member.getZoneId();
		packet.x = member.getX();
		packet.y = member.getY();
		packet.z = member.getZ();
		return packet;
	}
	
	private int objectId;
	
	private int zoneId;
	
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
		return ServerPacketType.PARTY_MEMBER_COORDS;
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
		writeFloat(buffer, x);
		writeFloat(buffer, y);
		writeFloat(buffer, z);
		writeInt(buffer, zoneId);
		writeInt(buffer, 6);
	}
}

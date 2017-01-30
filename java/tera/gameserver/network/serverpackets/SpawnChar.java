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

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class SpawnChar extends ServerPacket
{
	private static final ServerPacket instance = new SpawnChar();
	
	public static SpawnChar getInstance(Player player)
	{
		SpawnChar packet = (SpawnChar) instance.newInstance();
		
		packet.objectId = player.getObjectId();
		packet.subId = player.getSubId();
		packet.heading = player.getHeading();
		packet.dead = player.isDead() ? 0 : 1;
		
		packet.x = player.getX();
		packet.y = player.getY();
		packet.z = player.getZ();
		
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int heading;
	private int dead;
	
	private float x;
	private float y;
	private float z;
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.CHAR_SPAWN;
	}
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeFloat(buffer, x);
		writeFloat(buffer, y);
		writeFloat(buffer, z);
		writeShort(buffer, heading);
		writeByte(buffer, dead);
	}
}
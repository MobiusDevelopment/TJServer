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
public class WorldZone extends ServerPacket
{
	private static final ServerPacket instance = new WorldZone();
	
	public static WorldZone getInstance(Player player)
	{
		WorldZone packet = (WorldZone) instance.newInstance();
		
		packet.player = player;
		packet.zoneId = player.getZoneId();
		
		return packet;
	}
	
	public static WorldZone getInstance(Player player, int zoneId)
	{
		WorldZone packet = (WorldZone) instance.newInstance();
		
		packet.player = player;
		packet.zoneId = zoneId;
		
		return packet;
	}
	
	private Player player;
	
	private int zoneId;
	
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.WORLD_ZONE;
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
		writeInt(buffer, zoneId);
		writeFloat(buffer, player.getX());
		writeFloat(buffer, player.getY());
		writeFloat(buffer, player.getZ());
		writeByte(buffer, 0);
	}
}

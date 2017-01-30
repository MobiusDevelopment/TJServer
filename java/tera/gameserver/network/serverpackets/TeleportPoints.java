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
import java.nio.ByteOrder;

import tera.gameserver.model.TeleportRegion;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.territory.LocalTerritory;
import tera.gameserver.network.ServerPacketType;
import tera.util.Location;

/**
 * @author Ronn
 * @created 26.02.2012
 */
public class TeleportPoints extends ServerPacket
{
	private static final ServerPacket instance = new TeleportPoints();
	
	/**
	 * Method getInstance.
	 * @param npc Npc
	 * @param player Player
	 * @param regions TeleportRegion[]
	 * @return TeleportPoints
	 */
	public static TeleportPoints getInstance(Npc npc, Player player, TeleportRegion[] regions)
	{
		final TeleportPoints packet = (TeleportPoints) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			packet.writeShort(buffer, regions.length);
			int bytes = 24;
			packet.writeShort(buffer, bytes);
			packet.writeFloat(buffer, npc.getX());
			packet.writeFloat(buffer, npc.getY());
			packet.writeFloat(buffer, npc.getZ());
			packet.writeInt(buffer, 1);
			
			for (int i = 0, length = regions.length; i < length; i++)
			{
				final TeleportRegion region = regions[i];
				final LocalTerritory local = region.getRegion();
				final Location loc = local.getTeleportLoc();
				packet.writeShort(buffer, bytes);
				
				if (i == (length - 1))
				{
					bytes = 0;
				}
				else
				{
					bytes += 29;
				}
				
				packet.writeShort(buffer, bytes);
				packet.writeInt(buffer, region.getIndex());
				packet.writeFloat(buffer, loc.getX());
				packet.writeFloat(buffer, loc.getY());
				packet.writeFloat(buffer, loc.getZ());
				packet.writeInt(buffer, region.getPrice());
				packet.writeInt(buffer, 0);
				packet.writeByte(buffer, player.isWhetherIn(local) ? 1 : 0);
			}
			
			return packet;
		}
		
		finally
		{
			buffer.flip();
		}
	}
	
	private final ByteBuffer prepare;
	
	public TeleportPoints()
	{
		prepare = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		prepare.clear();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.TELEPORT_POINTS;
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
		final ByteBuffer prepare = getPrepare();
		buffer.put(prepare.array(), 0, prepare.limit());
	}
	
	/**
	 * Method getPrepare.
	 * @return ByteBuffer
	 */
	public ByteBuffer getPrepare()
	{
		return prepare;
	}
}
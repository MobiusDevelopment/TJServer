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

import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildLog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class GuildLogs extends ServerPacket
{
	private static final ServerPacket instance = new GuildLogs();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @return GuildLogs
	 */
	public static GuildLogs getInstance(Player player)
	{
		final GuildLogs packet = (GuildLogs) instance.newInstance();
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			return packet;
		}
		
		final ByteBuffer buffer = packet.getPrepare();
		final Array<GuildLog> logs = guild.getLogs();
		final int bytes = 16;
		packet.writeShort(buffer, logs.size());
		packet.writeShort(buffer, bytes);
		packet.writeInt(buffer, 1);
		packet.writeInt(buffer, 1);
		buffer.flip();
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public GuildLogs()
	{
		prepare = ByteBuffer.allocate(204800).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.GUILD_LOGS;
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

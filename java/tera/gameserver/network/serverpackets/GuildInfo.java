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
import java.util.Iterator;

import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildMember;
import tera.gameserver.model.GuildRank;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public class GuildInfo extends ServerPacket
{
	private static final ServerPacket instance = new GuildInfo();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @return GuildInfo
	 */
	public static GuildInfo getInstance(Player player)
	{
		final GuildInfo packet = (GuildInfo) instance.newInstance();
		final Guild guild = player.getGuild();
		final ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			if (guild == null)
			{
				return packet;
			}
			
			final GuildMember leader = guild.getLeader();
			
			if (leader == null)
			{
				return packet;
			}
			
			final GuildRank myrank = player.getGuildRank();
			final String advertisment = "";
			final int guildNameLength = Strings.length(guild.getName());
			final int guildTitleLength = Strings.length(guild.getTitle());
			final int leaderNameLength = Strings.length(leader.getName());
			final int leaderTitleLength = Strings.length(guild.getMessage());
			final int advertismentLength = Strings.length(advertisment);
			final int myRankLenght = Strings.length(myrank.getName());
			final int fb = 79;
			int n = fb + guildNameLength + guildTitleLength + leaderNameLength + leaderTitleLength + myRankLenght + advertismentLength;
			packet.writeShort(buffer, 3);
			packet.writeShort(buffer, n);
			packet.writeShort(buffer, fb);
			packet.writeShort(buffer, fb + guildNameLength);
			packet.writeShort(buffer, fb + guildNameLength + guildTitleLength);
			packet.writeShort(buffer, fb + guildNameLength + guildTitleLength + leaderNameLength);
			packet.writeShort(buffer, fb + guildNameLength + guildTitleLength + leaderNameLength + 2);
			packet.writeShort(buffer, fb + guildNameLength + guildTitleLength + leaderNameLength + 2 + myRankLenght);
			packet.writeInt(buffer, 2127);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, leader.getObjectId());
			packet.writeInt(buffer, (int) (System.currentTimeMillis() / 1000));
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, guild.getLevel());
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeByte(buffer, 1);
			packet.writeByte(buffer, 0);
			packet.writeByte(buffer, 1);
			packet.writeByte(buffer, 15);
			packet.writeInt(buffer, 14);
			packet.writeShort(buffer, 0);
			packet.writeByte(buffer, 0);
			packet.writeInt(buffer, -1);
			packet.writeInt(buffer, -1);
			packet.writeString(buffer, guild.getName());
			packet.writeString(buffer, guild.getTitle());
			packet.writeString(buffer, leader.getName());
			packet.writeString(buffer, guild.getMessage());
			packet.writeString(buffer, myrank.getName());
			packet.writeString(buffer, advertisment);
			final Table<IntKey, GuildRank> ranks = guild.getRanks();
			synchronized (guild)
			{
				int k = 0;
				
				for (Iterator<GuildRank> iterator = ranks.iterator(); iterator.hasNext();)
				{
					final GuildRank rank = iterator.next();
					packet.writeShort(buffer, n);
					k = Strings.length(rank.getName());
					n += (14 + k);
					
					if (iterator.hasNext())
					{
						packet.writeShort(buffer, n);
					}
					else
					{
						packet.writeShort(buffer, 0);
					}
					
					packet.writeShort(buffer, n - k);
					packet.writeInt(buffer, rank.getIndex());
					packet.writeInt(buffer, rank.getLawId());
					packet.writeString(buffer, rank.getName());
				}
			}
			return packet;
		}
		
		finally
		{
			buffer.flip();
		}
	}
	
	private final ByteBuffer prepare;
	
	public GuildInfo()
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
		return ServerPacketType.GUILD_INFO;
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

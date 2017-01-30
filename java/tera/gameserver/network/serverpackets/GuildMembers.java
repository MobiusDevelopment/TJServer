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
import tera.gameserver.model.GuildMember;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class GuildMembers extends ServerPacket
{
	private static final ServerPacket instance = new GuildMembers();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @return GuildMembers
	 */
	public static GuildMembers getInstance(Player player)
	{
		final GuildMembers packet = (GuildMembers) instance.newInstance();
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			return packet;
		}
		
		final ByteBuffer buffer = packet.getPrepare();
		packet.writeShort(buffer, 2);
		packet.writeShort(buffer, 11);
		packet.writeByte(buffer, 1);
		packet.writeByte(buffer, 1);
		packet.writeByte(buffer, 1);
		int byets = 11;
		final Array<GuildMember> members = guild.getMembers();
		members.readLock();
		
		try
		{
			final GuildMember[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				packet.writeShort(buffer, byets);
				final GuildMember member = array[i];
				final int nameLength = Strings.length(member.getName());
				final int titleLength = Strings.length(member.getNote());
				byets += (57 + nameLength + titleLength);
				
				if (i != (length - 1))
				{
					packet.writeShort(buffer, byets);
				}
				else
				{
					packet.writeShort(buffer, 0);
				}
				
				packet.writeShort(buffer, byets - nameLength - titleLength);
				packet.writeShort(buffer, byets - titleLength);
				packet.writeInt(buffer, member.getObjectId());
				packet.writeInt(buffer, 1);
				packet.writeInt(buffer, 1);
				packet.writeInt(buffer, 1);
				packet.writeInt(buffer, member.getRankId());
				packet.writeInt(buffer, member.getLevel());
				packet.writeInt(buffer, member.getRaceId());
				packet.writeInt(buffer, member.getClassId());
				packet.writeInt(buffer, member.getSex());
				packet.writeInt(buffer, member.isOnline() ? 0 : 2);
				packet.writeInt(buffer, member.getLastOnline());
				packet.writeInt(buffer, 0);
				packet.writeByte(buffer, 0);
				packet.writeString(buffer, member.getName());
				packet.writeString(buffer, member.getNote());
			}
		}
		
		finally
		{
			members.readUnlock();
		}
		buffer.flip();
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public GuildMembers()
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
		return ServerPacketType.GUILD_MEMBERS;
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

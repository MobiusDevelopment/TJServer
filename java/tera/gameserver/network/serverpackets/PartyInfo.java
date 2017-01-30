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

import tera.gameserver.model.Party;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class PartyInfo extends ServerPacket
{
	private static final ServerPacket instance = new PartyInfo();
	
	/**
	 * Method getInstance.
	 * @param party Party
	 * @return PartyInfo
	 */
	public static PartyInfo getInstance(Party party)
	{
		final PartyInfo packet = (PartyInfo) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		packet.writeShort(buffer, 2);
		packet.writeShort(buffer, 48);
		packet.writeShort(buffer, 48);
		packet.writeShort(buffer, 0);
		packet.writeByte(buffer, 0);
		packet.writeInt(buffer, party.getObjectId());
		packet.writeShort(buffer, 9);
		packet.writeShort(buffer, 0);
		packet.writeInt(buffer, 0);
		packet.writeInt(buffer, party.getLeaderId());
		packet.writeInt(buffer, party.isRoundLoot() ? 1 : 0);
		packet.writeInt(buffer, 0);
		packet.writeShort(buffer, 0);
		packet.writeInt(buffer, 0);
		packet.writeInt(buffer, 0);
		packet.writeByte(buffer, party.isLootInCombat() ? 0 : 1);
		final Array<Player> members = party.getMembers();
		members.readLock();
		
		try
		{
			int byets = 48;
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				packet.writeShort(buffer, byets);
				final int nameLength = Strings.length(member.getName());
				byets += (35 + nameLength);
				
				if (i < length)
				{
					packet.writeShort(buffer, byets);
				}
				else
				{
					packet.writeShort(buffer, 0);
				}
				
				packet.writeShort(buffer, byets - nameLength);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, member.getObjectId());
				packet.writeInt(buffer, member.getLevel());
				packet.writeInt(buffer, member.getClassId());
				packet.writeByte(buffer, 1);
				packet.writeInt(buffer, member.getObjectId());
				packet.writeInt(buffer, member.getSubId());
				packet.writeShort(buffer, 0);
				packet.writeByte(buffer, 0);
				packet.writeByte(buffer, 0);
				packet.writeString(buffer, member.getName());
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
	
	public PartyInfo()
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
		return ServerPacketType.PARTY_INFO;
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
	 * Method getPrepare.
	 * @return ByteBuffer
	 */
	public ByteBuffer getPrepare()
	{
		return prepare;
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
}
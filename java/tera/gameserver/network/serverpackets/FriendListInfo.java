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

import tera.gameserver.model.FriendInfo;
import tera.gameserver.model.FriendList;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;

/**
 * @author Ronn
 */
public class FriendListInfo extends ServerPacket
{
	private static final ServerPacket instance = new FriendListInfo();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @return FriendListInfo
	 */
	public static FriendListInfo getInstance(Player player)
	{
		final FriendListInfo packet = (FriendListInfo) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			final FriendList friendList = player.getFriendList();
			packet.writeShort(buffer, 8);
			packet.writeShort(buffer, 8);
			int n = 8;
			synchronized (friendList)
			{
				final FriendInfo[] friends = friendList.getFriends();
				
				for (int i = 0, length = friendList.size(); i < length; i++)
				{
					final FriendInfo friend = friends[i];
					final String name = friend.getName();
					final int nameLength = Strings.length(name);
					packet.writeShort(buffer, n);
					
					if (i == length)
					{
						packet.writeShort(buffer, 0);
					}
					else
					{
						packet.writeShort(buffer, n + 42 + nameLength);
					}
					
					packet.writeShort(buffer, n + 40);
					packet.writeShort(buffer, n + 40 + nameLength);
					packet.writeInt(buffer, friend.getObjectId());
					packet.writeInt(buffer, friend.getLevel());
					packet.writeInt(buffer, friend.getRaceId());
					packet.writeInt(buffer, friend.getClassId());
					packet.writeInt(buffer, 0);
					packet.writeInt(buffer, 0);
					packet.writeInt(buffer, 0);
					packet.writeInt(buffer, 0);
					packet.writeString(buffer, name);
					packet.writeShort(buffer, 0);
					n = n + 42 + nameLength;
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
	
	public FriendListInfo()
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
		return ServerPacketType.FRIEND_LIST_INFO;
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

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

import tera.gameserver.model.FriendList;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class FriendListState extends ServerPacket
{
	private static final ServerPacket instance = new FriendListState();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @return FriendListState
	 */
	public static FriendListState getInstance(Player player)
	{
		final FriendListState packet = (FriendListState) instance.newInstance();
		final ByteBuffer buffer = packet.prepare;
		final FriendList friendList = player.getFriendList();
		int n = 8;
		synchronized (friendList)
		{
			final Player[] players = friendList.getPlayers();
			packet.writeShort(buffer, friendList.online());
			packet.writeShort(buffer, 8);
			
			for (int i = 0, length = friendList.online(); i < length; i++)
			{
				final Player target = players[i];
				packet.writeShort(buffer, n);
				
				if (i == length)
				{
					packet.writeShort(buffer, 0);
				}
				else
				{
					packet.writeShort(buffer, n += 30);
				}
				
				packet.writeInt(buffer, target.getObjectId());
				packet.writeInt(buffer, target.getLevel());
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeShort(buffer, 1);
			}
		}
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public FriendListState()
	{
		super();
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
		return ServerPacketType.FRIEND_LIST_STATE;
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
		prepare.flip();
		buffer.put(prepare);
	}
}

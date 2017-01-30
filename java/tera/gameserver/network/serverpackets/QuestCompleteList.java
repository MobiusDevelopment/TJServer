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

import tera.gameserver.model.quests.QuestDate;
import tera.gameserver.network.ServerPacketType;

import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public class QuestCompleteList extends ServerPacket
{
	private static final ServerPacket instance = new QuestCompleteList();
	
	/**
	 * Method getInstance.
	 * @param completeTable Table<IntKey,QuestDate>
	 * @return QuestCompleteList
	 */
	public static QuestCompleteList getInstance(Table<IntKey, QuestDate> completeTable)
	{
		final QuestCompleteList packet = (QuestCompleteList) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			final Array<QuestDate> completed = packet.getCompleted();
			completeTable.values(completed);
			final QuestDate[] array = completed.array();
			final QuestDate last = completed.last();
			int bytes = 8;
			packet.writeShort(buffer, completeTable.size());
			packet.writeShort(buffer, bytes);
			
			for (int i = 0, length = completed.size(); i < length; i++)
			{
				final QuestDate next = array[i];
				packet.writeShort(buffer, bytes);
				
				if (next != last)
				{
					bytes += 8;
				}
				else
				{
					bytes = 0;
				}
				
				packet.writeShort(buffer, bytes);
				packet.writeInt(buffer, next.getQuestId());
			}
			
			return packet;
		}
		
		finally
		{
			buffer.flip();
		}
	}
	
	private final Array<QuestDate> completed;
	private final ByteBuffer prepare;
	
	public QuestCompleteList()
	{
		prepare = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
		completed = Arrays.toSortedArray(QuestDate.class);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		prepare.clear();
		completed.clear();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.QUEST_COMPLETE_LIST;
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
	
	/**
	 * Method getCompleted.
	 * @return Array<QuestDate>
	 */
	public Array<QuestDate> getCompleted()
	{
		return completed;
	}
}
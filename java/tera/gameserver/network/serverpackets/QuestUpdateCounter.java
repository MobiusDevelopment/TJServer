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

import tera.gameserver.model.quests.QuestState;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class QuestUpdateCounter extends ServerPacket
{
	private static final ServerPacket instance = new QuestUpdateCounter();
	
	/**
	 * Method getInstance.
	 * @param state QuestState
	 * @param counts int[]
	 * @param complete boolean
	 * @return QuestUpdateCounter
	 */
	public static QuestUpdateCounter getInstance(QuestState state, int[] counts, boolean complete)
	{
		final QuestUpdateCounter packet = (QuestUpdateCounter) instance.newInstance();
		packet.buffer = packet.prepare;
		int n = 23;
		packet.writeInt(0);
		packet.writeShort(counts.length);
		packet.writeShort(n);
		packet.writeInt(state.getQuestId());
		packet.writeInt(state.getObjectId());
		packet.writeShort(0);
		packet.writeByte(complete ? 1 : 0);
		
		for (int i = 0, length = counts.length; i < length; i++)
		{
			packet.writeShort(n);
			
			if (i == (length - 1))
			{
				n = 0;
			}
			else
			{
				n += 8;
			}
			
			packet.writeShort(n);
			packet.writeInt(counts[i]);
		}
		
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public QuestUpdateCounter()
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
		return ServerPacketType.QUEST_UPDATE_COUNTER;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		prepare.flip();
		buffer.put(prepare);
	}
}
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

import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.QuestState;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class QuestStarted extends ServerPacket
{
	private static final ServerPacket instance = new QuestStarted();
	
	/**
	 * Method getInstance.
	 * @param quest QuestState
	 * @param npcType int
	 * @param npcId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @return QuestStarted
	 */
	public static QuestStarted getInstance(QuestState quest, int npcType, int npcId, float x, float y, float z)
	{
		final QuestStarted packet = (QuestStarted) instance.newInstance();
		packet.buffer = packet.prepare;
		final Player player = quest.getPlayer();
		packet.writeShort(1);
		packet.writeShort(14);
		packet.writeInt(12);
		packet.writeShort(0);
		packet.writeInt(14);
		packet.writeShort(1);
		packet.writeShort(57);
		packet.writeShort(1);
		packet.writeShort(85);
		packet.writeInt(quest.getQuestId());
		packet.writeInt(quest.getObjectId());
		packet.writeInt(quest.getState());
		packet.writeInt(1);
		packet.writeInt(0);
		packet.writeInt(quest.getPanelStateId());
		packet.writeInt(0x00010100);
		packet.writeShort(0);
		packet.writeByte(0);
		packet.writeInt(57);
		packet.writeFloat(x);
		packet.writeFloat(y);
		packet.writeFloat(z);
		packet.writeInt(npcType);
		packet.writeInt(npcId);
		packet.writeInt(player != null ? player.getZoneId() : 13);
		packet.writeLong(85);
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public QuestStarted()
	{
		super();
		prepare = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.QUEST_STARTED;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		prepare.flip();
		buffer.put(prepare);
	}
}
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

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.quests.NpcIconType;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class QuestNpcNotice extends ServerPacket
{
	private static final ServerPacket instance = new QuestNpcNotice();
	
	/**
	 * Method getInstance.
	 * @param npc Npc
	 * @param type NpcIconType
	 * @return QuestNpcNotice
	 */
	public static QuestNpcNotice getInstance(Npc npc, NpcIconType type)
	{
		final QuestNpcNotice packet = (QuestNpcNotice) instance.newInstance();
		packet.id = npc.getObjectId();
		packet.subId = npc.getSubId();
		packet.type = type;
		return packet;
	}
	
	private NpcIconType type;
	private int id;
	private int subId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.QUEST_NPC_NOTICE;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeInt(id);
		writeInt(subId);
		writeInt(type.ordinal());
		writeByte(1);
	}
}
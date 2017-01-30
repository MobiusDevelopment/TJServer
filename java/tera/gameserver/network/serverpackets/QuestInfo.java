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

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.Reward;
import tera.gameserver.model.quests.actions.ActionAddItem;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class QuestInfo extends ServerPacket
{
	private static final ServerPacket instance = new QuestInfo();
	
	/**
	 * Method getInstance.
	 * @param npc Npc
	 * @param player Player
	 * @param quest Quest
	 * @param dialogId int
	 * @param page int
	 * @param button String
	 * @return QuestInfo
	 */
	public static QuestInfo getInstance(Npc npc, Player player, Quest quest, int dialogId, int page, String button)
	{
		final QuestInfo packet = (QuestInfo) instance.newInstance();
		packet.buffer = packet.prepare;
		packet.writeShort(1);
		packet.writeShort(64);
		packet.writeShort(2);
		final int endNameLink = 88 + button.length();
		packet.writeShort(endNameLink);
		packet.writeInt(npc.getObjectId());
		packet.writeInt(npc.getSubId());
		packet.writeInt(dialogId);
		packet.writeInt(quest.getId());
		packet.writeInt(4);
		packet.writeInt(0);
		packet.writeInt(page);
		packet.writeInt(0x3296B647);
		packet.writeInt(0);
		packet.writeByte(0);
		packet.writeByte(1);
		packet.writeLong(0);
		packet.writeShort(0);
		packet.writeInt(0xFFFFFFFF);
		packet.writeInt(64);
		packet.writeShort(78);
		packet.writeInt(1);
		packet.writeInt(4);
		packet.writeString(button);
		final Reward reward = quest.getReward();
		final ActionAddItem[] items = reward.getItems();
		ActionAddItem last = null;
		
		if (items != null)
		{
			for (ActionAddItem item : items)
			{
				if (item.test(npc, player))
				{
					last = item;
				}
			}
		}
		
		packet.writeInt(endNameLink);
		
		if (last == null)
		{
			packet.writeInt(0);
		}
		else
		{
			packet.writeShort(1);
			packet.writeShort(140);
		}
		
		packet.writeInt(0);
		packet.writeInt(0);
		packet.writeInt(reward.getExp());
		packet.writeInt(reward.getMoney());
		packet.writeInt(0);
		packet.writeInt(0);
		packet.writeInt(0);
		packet.writeInt(0);
		packet.writeInt(0);
		
		if ((last != null) && (items != null))
		{
			int bytes = 140;
			
			for (ActionAddItem item : items)
			{
				if (item.test(npc, player))
				{
					packet.writeShort(bytes);
					
					if (item == last)
					{
						bytes = 0;
					}
					else
					{
						bytes += 12;
					}
					
					packet.writeShort(bytes);
					packet.writeInt(item.getItemId());
					packet.writeInt(item.getItemCount());
				}
			}
		}
		
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public QuestInfo()
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
		return ServerPacketType.QUEST_INFO;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		prepare.flip();
		buffer.put(prepare);
	}
}
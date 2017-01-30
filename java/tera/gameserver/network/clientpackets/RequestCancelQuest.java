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
package tera.gameserver.network.clientpackets;

import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestState;
import tera.util.LocalObjects;

/**
 * @author Ronn
 */
public class RequestCancelQuest extends ClientPacket
{
	private Player player;
	private int objectId;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	@Override
	public void readImpl()
	{
		if (buffer.remaining() < 12)
		{
			return;
		}
		
		player = owner.getOwner();
		readInt();
		readInt();
		objectId = readInt();
	}
	
	@Override
	public void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final QuestList questList = player.getQuestList();
		
		if (questList == null)
		{
			log.warning(this, new Exception("not found quest list for player " + player.getName()));
			return;
		}
		
		final QuestState state = questList.getQuestState(objectId);
		
		if (state == null)
		{
			return;
		}
		
		final Quest quest = state.getQuest();
		final LocalObjects local = LocalObjects.get();
		final QuestEvent event = local.getNextQuestEvent();
		event.setPlayer(player);
		event.setQuest(quest);
		quest.cancel(event, false);
	}
}
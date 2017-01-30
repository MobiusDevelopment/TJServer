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
package tera.gameserver.model.quests.actions;

import org.w3c.dom.Node;

import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestState;
import tera.gameserver.network.serverpackets.QuestCompleted;

/**
 * @author Ronn
 */
public class ActionFinishQuest extends AbstractQuestAction
{
	/**
	 * Constructor for ActionFinishQuest.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionFinishQuest(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
	}
	
	/**
	 * Method apply.
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.QuestAction#apply(QuestEvent)
	 */
	@Override
	public void apply(QuestEvent event)
	{
		final Player player = event.getPlayer();
		final Quest quest = event.getQuest();
		
		if (player == null)
		{
			log.warning(this, "not found player");
			return;
		}
		
		if (quest == null)
		{
			log.warning(this, "not found quest");
			return;
		}
		
		final QuestList questList = player.getQuestList();
		
		if (questList == null)
		{
			log.warning(this, "not found quest list");
			return;
		}
		
		final QuestState state = questList.getQuestState(quest);
		
		if (state == null)
		{
			log.warning(this, "not found quest state");
			return;
		}
		
		player.sendPacket(QuestCompleted.getInstance(state, false), true);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ActionRemoveToPanel ";
	}
}
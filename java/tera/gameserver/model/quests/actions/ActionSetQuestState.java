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

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestState;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class ActionSetQuestState extends AbstractQuestAction
{
	private final int state;
	
	/**
	 * Constructor for ActionSetQuestState.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionSetQuestState(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		state = VarTable.newInstance(node).getInteger("state");
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
		
		if (player == null)
		{
			return;
		}
		
		final QuestList questList = player.getQuestList();
		
		if (questList == null)
		{
			return;
		}
		
		final QuestState state = questList.getQuestState(quest);
		
		if (state == null)
		{
			return;
		}
		
		state.setState(getState());
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updateQuest(state);
	}
	
	/**
	 * Method getState.
	 * @return int
	 */
	private final int getState()
	{
		return state;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ActionSetQuestState state = " + state;
	}
}
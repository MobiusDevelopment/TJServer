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
package tera.gameserver.model.npc.interaction.conditions;

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestState;

/**
 * @author Ronn
 */
public class ConditionQuestState extends AbstractCondition
{
	private final int state;
	
	/**
	 * Constructor for ConditionQuestState.
	 * @param quest Quest
	 * @param state int
	 */
	public ConditionQuestState(Quest quest, int state)
	{
		super(quest);
		this.state = state;
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
	 * Method test.
	 * @param npc Npc
	 * @param player Player
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.Condition#test(Npc, Player)
	 */
	@Override
	public boolean test(Npc npc, Player player)
	{
		if (player == null)
		{
			log.warning(this, "not found player");
			return false;
		}
		
		final QuestList questList = player.getQuestList();
		
		if (questList == null)
		{
			log.warning(this, "not found quest list");
			return false;
		}
		
		final QuestState state = questList.getQuestState(quest);
		
		if (state == null)
		{
			return getState() == 0;
		}
		
		return (state.getState() == getState());
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ConditionQuestState state = " + state;
	}
}
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

import rlib.util.wraps.Wrap;

/**
 * @author Ronn
 */
public class ConditionPlayerVar extends AbstractCondition
{
	private final String name;
	private final int value;
	
	/**
	 * Constructor for ConditionPlayerVar.
	 * @param quest Quest
	 * @param name String
	 * @param value int
	 */
	public ConditionPlayerVar(Quest quest, String name, int value)
	{
		super(quest);
		this.name = name;
		this.value = value;
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
			return false;
		}
		
		final Wrap wrap = state.getVar(name);
		
		if (wrap == null)
		{
			return value == 0;
		}
		
		return wrap.getInt() == value;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ConditionPlayerVar name = " + name + ", value = " + value;
	}
}
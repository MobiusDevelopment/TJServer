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
import tera.gameserver.model.quests.QuestDate;
import tera.gameserver.model.quests.QuestList;

/**
 * @author Ronn
 */
public class ConditionQuestAgoComplete extends AbstractCondition
{
	private final long time;
	
	/**
	 * Constructor for ConditionQuestAgoComplete.
	 * @param quest Quest
	 * @param time long
	 */
	public ConditionQuestAgoComplete(Quest quest, long time)
	{
		super(quest);
		this.time = time * 60 * 1000;
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
		
		final QuestDate date = questList.getQuestDate(quest.getId());
		
		if (date == null)
		{
			return false;
		}
		
		return (System.currentTimeMillis() - date.getTime()) > time;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ConditionQuestAgoComplete time = " + time;
	}
}
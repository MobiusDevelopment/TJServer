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
package tera.gameserver.model.quests;

import rlib.util.pools.Foldable;

/**
 * @author Ronn
 */
public class QuestDate implements Foldable, Comparable<QuestDate>
{
	private Quest quest;
	private long time;
	
	/**
	 * Method compareTo.
	 * @param date QuestDate
	 * @return int
	 */
	@Override
	public int compareTo(QuestDate date)
	{
		return quest.getId() - date.quest.getId();
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		quest = null;
		time = 0;
	}
	
	/**
	 * Method getQuest.
	 * @return Quest
	 */
	public Quest getQuest()
	{
		return quest;
	}
	
	/**
	 * Method getQuestId.
	 * @return int
	 */
	public int getQuestId()
	{
		return quest.getId();
	}
	
	/**
	 * Method getTime.
	 * @return long
	 */
	public long getTime()
	{
		return time;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	/**
	 * Method setQuest.
	 * @param quest Quest
	 */
	public void setQuest(Quest quest)
	{
		this.quest = quest;
	}
	
	/**
	 * Method setTime.
	 * @param time long
	 */
	public void setTime(long time)
	{
		this.time = time;
	}
}
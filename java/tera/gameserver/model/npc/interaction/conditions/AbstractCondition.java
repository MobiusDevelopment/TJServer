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

import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.quests.Quest;

import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public abstract class AbstractCondition implements Condition
{
	protected static final Logger log = Loggers.getLogger(AbstractCondition.class);
	protected Quest quest;
	
	/**
	 * Constructor for AbstractCondition.
	 * @param quest Quest
	 */
	public AbstractCondition(Quest quest)
	{
		this.quest = quest;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
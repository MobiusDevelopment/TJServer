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
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class ConditionLogicOr implements Condition
{
	
	private Condition[] conditions;
	
	public ConditionLogicOr()
	{
		conditions = Condition.EMPTY_CONDITIONS;
	}
	
	/**
	 * Method add.
	 * @param condition Condition
	 */
	public void add(Condition condition)
	{
		if (condition == null)
		{
			return;
		}
		
		conditions = Arrays.addToArray(conditions, condition, Condition.class);
	}
	
	/**
	 * Method getConditions.
	 * @return Condition[]
	 */
	private final Condition[] getConditions()
	{
		return conditions;
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
		final Condition[] conditions = getConditions();
		
		if (conditions.length < 1)
		{
			return true;
		}
		
		for (Condition condition : conditions)
		{
			if (condition.test(npc, player))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ":" + Arrays.toString(conditions);
	}
}

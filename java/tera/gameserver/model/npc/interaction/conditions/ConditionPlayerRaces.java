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

import tera.gameserver.model.base.Race;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;

import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class ConditionPlayerRaces extends AbstractCondition
{
	private final Race[] races;
	
	/**
	 * Constructor for ConditionPlayerRaces.
	 * @param quest Quest
	 * @param races Race[]
	 */
	public ConditionPlayerRaces(Quest quest, Race[] races)
	{
		super(quest);
		this.races = races;
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
			return false;
		}
		
		return Arrays.contains(races, player.getRace());
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ConditionPlayerRaces races = " + Arrays.toString(races);
	}
}
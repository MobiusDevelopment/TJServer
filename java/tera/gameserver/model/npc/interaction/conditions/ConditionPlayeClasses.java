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

import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;

import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class ConditionPlayeClasses extends AbstractCondition
{
	private final PlayerClass[] classes;
	
	/**
	 * Constructor for ConditionPlayeClasses.
	 * @param quest Quest
	 * @param classes PlayerClass[]
	 */
	public ConditionPlayeClasses(Quest quest, PlayerClass[] classes)
	{
		super(quest);
		this.classes = classes;
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
		
		return Arrays.contains(classes, player.getPlayerClass());
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ConditionPlayerClasses classes = " + Arrays.toString(classes);
	}
}
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

import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;

/**
 * @author Ronn
 */
public class ConditionPlayerHasItem extends AbstractCondition
{
	private final int id;
	private final int count;
	
	/**
	 * Constructor for ConditionPlayerHasItem.
	 * @param quest Quest
	 * @param id int
	 * @param count int
	 */
	public ConditionPlayerHasItem(Quest quest, int id, int count)
	{
		super(quest);
		this.id = id;
		this.count = count;
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
		
		final Inventory inventory = player.getInventory();
		
		if (inventory == null)
		{
			log.warning(this, "not found inventory");
			return false;
		}
		
		return inventory.containsItems(id, count);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ConditionPlayerHasItem id = " + id + ", count = " + count;
	}
}
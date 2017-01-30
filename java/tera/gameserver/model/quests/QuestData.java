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

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class QuestData
{
	private Quest[] quests;
	
	/**
	 * Method addLinks.
	 * @param container Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	public void addLinks(Array<Link> container, Npc npc, Player player)
	{
		final Quest[] quests = getQuests();
		
		if (quests == null)
		{
			return;
		}
		
		for (Quest quest : quests)
		{
			quest.addLinks(container, npc, player);
		}
	}
	
	/**
	 * Method addQuest.
	 * @param quest Quest
	 */
	public void addQuest(Quest quest)
	{
		final Quest[] quests = getQuests();
		
		if (quests == null)
		{
			setQuests(Arrays.toGenericArray(quest));
		}
		else
		{
			for (Quest quest2 : quests)
			{
				if (quest2 == quest)
				{
					return;
				}
			}
			
			setQuests(Arrays.addToArray(quests, quest, Quest.class));
		}
	}
	
	/**
	 * Method getQuests.
	 * @return Quest[]
	 */
	private final Quest[] getQuests()
	{
		return quests;
	}
	
	/**
	 * Method hasQuests.
	 * @param npc Npc
	 * @param player Player
	 * @return QuestType
	 */
	public QuestType hasQuests(Npc npc, Player player)
	{
		final Quest[] quests = getQuests();
		
		if (quests == null)
		{
			return null;
		}
		
		for (Quest quest : quests)
		{
			if (quest.isAvailable(npc, player))
			{
				return quest.getType();
			}
		}
		
		return null;
	}
	
	/**
	 * Method setQuests.
	 * @param quests Quest[]
	 */
	private final void setQuests(Quest[] quests)
	{
		this.quests = quests;
	}
}
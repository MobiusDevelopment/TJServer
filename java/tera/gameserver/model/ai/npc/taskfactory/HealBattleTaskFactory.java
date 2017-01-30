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
package tera.gameserver.model.ai.npc.taskfactory;

import org.w3c.dom.Node;

import tera.gameserver.model.World;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillGroup;
import tera.util.LocalObjects;

import rlib.util.Strings;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class HealBattleTaskFactory extends DefaultBattleTaskFactory
{
	/**
	 * Constructor for HealBattleTaskFactory.
	 * @param node Node
	 */
	public HealBattleTaskFactory(Node node)
	{
		super(node);
	}
	
	/**
	 * Method addNewTask.
	 * @param ai NpcAI<A>
	 * @param actor A
	 * @param local LocalObjects
	 * @param config ConfigAI
	 * @param currentTime long
	 */
	@Override
	public <A extends Npc> void addNewTask(NpcAI<A> ai, A actor, LocalObjects local, ConfigAI config, long currentTime)
	{
		if (chance(SkillGroup.HEAL))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.HEAL);
			
			if ((skill != null) && !actor.isSkillDisabled(skill))
			{
				if (actor.getCurrentHpPercent() < 100)
				{
					ai.addCastTask(skill, actor);
				}
				else if (actor.getFraction() != Strings.EMPTY)
				{
					final Array<Npc> npcs = local.getNextNpcList();
					World.getAround(Npc.class, npcs, actor, 450);
					
					if (!npcs.isEmpty())
					{
						final Npc[] array = npcs.array();
						
						for (int i = 0, length = npcs.size(); i < length; i++)
						{
							final Npc npc = array[i];
							
							if ((npc.getCurrentHpPercent() < 100) && npc.getFraction().equals(actor.getFraction()))
							{
								ai.addCastTask(skill, npc);
								return;
							}
						}
					}
				}
			}
		}
		
		super.addNewTask(ai, actor, local, config, currentTime);
	}
}
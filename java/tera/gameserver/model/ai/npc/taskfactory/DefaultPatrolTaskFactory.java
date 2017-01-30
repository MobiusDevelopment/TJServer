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

import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillGroup;
import tera.gameserver.network.serverpackets.NotifyCharacter.NotifyType;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.util.Rnd;
import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class DefaultPatrolTaskFactory extends AbstractTaskFactory
{
	protected final int[] groupChance;
	protected final int noticeRange;
	protected final int patrolInterval;
	
	/**
	 * Constructor for DefaultPatrolTaskFactory.
	 * @param node Node
	 */
	public DefaultPatrolTaskFactory(Node node)
	{
		super(node);
		
		try
		{
			final SkillGroup[] groups = SkillGroup.values();
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			noticeRange = vars.getInteger("noticeRange", ConfigAI.DEFAULT_NOTICE_RANGE);
			patrolInterval = vars.getInteger("patrolInterval", 0);
			groupChance = new int[groups.length];
			
			for (int i = 0, length = groupChance.length; i < length; i++)
			{
				groupChance[i] = vars.getInteger(groups[i].name(), 0);
			}
		}
		catch (Exception e)
		{
			log.warning(this, e);
			throw new IllegalArgumentException(e);
		}
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
		final boolean battle = actor.isBattleStanced();
		Character target = ai.getTarget();
		final int noticeRange = getNoticeRange();
		
		if (!battle || (target == null) || (actor.getGeomDistance(target) > noticeRange))
		{
			target = null;
			final Array<Character> charList = local.getNextCharList();
			World.getAround(Character.class, charList, actor, noticeRange);
			
			if (!charList.isEmpty())
			{
				final Character[] array = charList.array();
				
				for (int i = 0, length = charList.size(); i < length; i++)
				{
					final Character character = array[i];
					
					if (actor.checkTarget(character))
					{
						target = character;
						break;
					}
				}
			}
			
			if (target != null)
			{
				actor.stopMove();
				ai.clearTaskList();
				PacketManager.showNotifyIcon(actor, NotifyType.NOTICE);
				ai.setLastNotifyIcon(currentTime);
				ai.addNoticeTask(target, false);
				return;
			}
		}
		
		if (battle)
		{
			if (actor.isTurner())
			{
				if (!actor.isInTurnFront(target))
				{
					ai.addNoticeTask(target, false);
				}
			}
			else if (!actor.isInFront(target))
			{
				ai.addNoticeTask(target, false);
			}
			
			return;
		}
		
		if (chance(SkillGroup.HEAL))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.HEAL);
			
			if ((skill != null) && !actor.isSkillDisabled(skill))
			{
				if (!skill.isNoCaster() && (actor.getCurrentHp() < actor.getMaxHp()))
				{
					ai.addCastTask(skill, actor);
					return;
				}
				else if (!skill.isTargetSelf() && (actor.getFractionRange() > 0))
				{
					final String fraction = actor.getFraction();
					final Array<Npc> npcs = World.getAround(Npc.class, local.getNextNpcList(), actor, actor.getFractionRange());
					
					if (!npcs.isEmpty())
					{
						for (Npc npc : npcs.array())
						{
							if (npc == null)
							{
								break;
							}
							
							if (fraction.equals(npc.getFraction()) && (npc.getCurrentHp() < npc.getMaxHp()))
							{
								ai.addCastTask(skill, npc);
								return;
							}
						}
					}
				}
			}
		}
		
		if (chance(SkillGroup.BUFF))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.BUFF);
			
			if ((skill != null) && !actor.isSkillDisabled(skill))
			{
				if (!skill.isNoCaster() && !actor.containsEffect(skill))
				{
					ai.addCastTask(skill, actor);
					return;
				}
				else if (!skill.isTargetSelf() && (actor.getFractionRange() > 0))
				{
					final String fraction = actor.getFraction();
					final Array<Npc> npcs = World.getAround(Npc.class, local.getNextNpcList(), actor, actor.getFractionRange());
					
					if (!npcs.isEmpty())
					{
						for (Npc npc : npcs.array())
						{
							if (npc == null)
							{
								break;
							}
							
							if (fraction.equals(npc.getFraction()) && !npc.containsEffect(skill))
							{
								ai.addCastTask(skill, npc);
								return;
							}
						}
					}
				}
			}
		}
		
		final Location[] route = actor.getRoute();
		int currentIndex = ai.getRouteIndex();
		Location point = route[currentIndex];
		final long nextRoutePoint = ai.getNextRoutePoint();
		
		if (actor.getDistance(point.getX(), point.getY(), point.getZ()) > 10)
		{
			ai.addMoveTask(point, true);
		}
		else if (nextRoutePoint == -1)
		{
			ai.setNextRoutePoint(currentTime + getPatrolInterval());
		}
		else if (currentTime > nextRoutePoint)
		{
			currentIndex += 1;
			
			if (route.length >= currentIndex)
			{
				currentIndex = 0;
			}
			
			ai.setRouteIndex(currentIndex);
			point = route[currentIndex];
			ai.addMoveTask(point, true);
			ai.setNextRoutePoint(-1);
		}
	}
	
	/**
	 * Method chance.
	 * @param group SkillGroup
	 * @return boolean
	 */
	protected boolean chance(SkillGroup group)
	{
		return Rnd.chance(groupChance[group.ordinal()]);
	}
	
	/**
	 * Method getNoticeRange.
	 * @return int
	 */
	public int getNoticeRange()
	{
		return noticeRange;
	}
	
	/**
	 * Method getPatrolInterval.
	 * @return int
	 */
	public int getPatrolInterval()
	{
		return patrolInterval;
	}
}
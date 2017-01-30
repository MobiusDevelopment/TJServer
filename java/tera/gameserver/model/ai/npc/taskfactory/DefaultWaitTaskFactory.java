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

import tera.gameserver.manager.GeoManager;
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

import rlib.geom.Coords;
import rlib.util.Rnd;
import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class DefaultWaitTaskFactory extends AbstractTaskFactory
{
	protected final int[] groupChance;
	protected final int randomWalkMinRange;
	protected final int randomWalkMaxRange;
	protected final int noticeRange;
	protected final int randomWalkMinDelay;
	protected final int randomWalkMaxDelay;
	
	/**
	 * Constructor for DefaultWaitTaskFactory.
	 * @param node Node
	 */
	public DefaultWaitTaskFactory(Node node)
	{
		super(node);
		
		try
		{
			final SkillGroup[] groups = SkillGroup.values();
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			randomWalkMinRange = vars.getInteger("randomWalkMinRange", ConfigAI.DEFAULT_RANDOM_MIN_WALK_RANGE);
			randomWalkMaxRange = vars.getInteger("randomWalkMaxRange", ConfigAI.DEFAULT_RANDOM_MAX_WALK_RANGE);
			randomWalkMinDelay = vars.getInteger("randomWalkMinDelay", ConfigAI.DEFAULT_RANDOM_MIN_WALK_DELAY);
			randomWalkMaxDelay = vars.getInteger("randomWalkMaxDelay", ConfigAI.DEFAULT_RANDOM_MAX_WALK_DELAY);
			noticeRange = vars.getInteger("noticeRange", ConfigAI.DEFAULT_NOTICE_RANGE);
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
		
		if ((getRandomWalkMaxRange() > 0) && (currentTime > ai.getNextRandomWalk()))
		{
			if ((currentTime - ai.getLastNotifyIcon()) > 5000)
			{
				PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_THINK);
				ai.setLastNotifyIcon(currentTime);
			}
			
			ai.setNextRandomWalk(currentTime + Rnd.nextInt(getRandomWalkMinDelay(), getRandomWalkMaxDelay()));
			final Location loc = actor.getSpawnLoc();
			final int distance = Rnd.nextInt(getRandomWalkMinRange(), getRandomWalkMaxRange());
			final int newHeading = Rnd.nextInt(65000);
			final float newX = Coords.calcX(loc.getX(), distance, newHeading);
			final float newY = Coords.calcY(loc.getY(), distance, newHeading);
			final GeoManager geoManager = GeoManager.getInstance();
			ai.addMoveTask(newX, newY, geoManager.getHeight(actor.getContinentId(), newX, newY, loc.getZ()), true);
		}
	}
	
	/**
	 * Method getNoticeRange.
	 * @return int
	 */
	protected final int getNoticeRange()
	{
		return noticeRange;
	}
	
	/**
	 * Method getRandomWalkMaxDelay.
	 * @return int
	 */
	protected final int getRandomWalkMaxDelay()
	{
		return randomWalkMaxDelay;
	}
	
	/**
	 * Method getRandomWalkMaxRange.
	 * @return int
	 */
	protected final int getRandomWalkMaxRange()
	{
		return randomWalkMaxRange;
	}
	
	/**
	 * Method getRandomWalkMinDelay.
	 * @return int
	 */
	protected final int getRandomWalkMinDelay()
	{
		return randomWalkMinDelay;
	}
	
	/**
	 * Method getRandomWalkMinRange.
	 * @return int
	 */
	protected final int getRandomWalkMinRange()
	{
		return randomWalkMinRange;
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
}
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

import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.skillengine.OperateType;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillGroup;
import tera.gameserver.model.skillengine.SkillType;
import tera.util.LocalObjects;

import rlib.geom.Angles;
import rlib.util.Rnd;
import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class DefaultBattleTaskFactory extends AbstractTaskFactory
{
	protected final int[] groupChance;
	protected final int shortRange;
	protected final boolean fastTurn;
	
	/**
	 * Constructor for DefaultBattleTaskFactory.
	 * @param node Node
	 */
	public DefaultBattleTaskFactory(Node node)
	{
		super(node);
		
		try
		{
			VarTable vars = VarTable.newInstance(node);
			final int def = vars.getInteger("groupChance", ConfigAI.DEFAULT_GROUP_CHANCE);
			final SkillGroup[] groups = SkillGroup.values();
			vars = VarTable.newInstance(node, "set", "name", "val");
			groupChance = new int[groups.length];
			shortRange = vars.getInteger("shortRange", ConfigAI.DEFAULT_SHORT_RATE);
			fastTurn = vars.getBoolean("fastTurn", false);
			
			for (int i = 0, length = groupChance.length; i < length; i++)
			{
				groupChance[i] = vars.getInteger(groups[i].name(), def);
			}
		}
		catch (Exception e)
		{
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
		
		if (chance(SkillGroup.TRAP))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.TRAP);
			
			if ((skill != null) && !actor.isSkillDisabled(skill))
			{
				ai.addCastTask(skill, actor);
				return;
			}
		}
		
		final Character target = ai.getTarget();
		
		if (target == null)
		{
			return;
		}
		
		if (chance(SkillGroup.DEBUFF))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.DEBUFF);
			
			if ((skill != null) && !actor.isSkillDisabled(skill))
			{
				ai.addCastTask(skill, target);
				return;
			}
		}
		
		if (!actor.isBattleStanced())
		{
			ai.addNoticeTask(target, true);
			return;
		}
		
		if (!isFastTurn())
		{
			if (!actor.isInFront(target) && !actor.isMoving())
			{
				ai.addNoticeTask(target, false);
				return;
			}
			
			if (!actor.isMoving())
			{
				if (actor.isTurner())
				{
					if (!actor.isInTurnFront(target))
					{
						ai.addNoticeTask(target, false);
						return;
					}
				}
				else if (!actor.isInFront(target))
				{
					ai.addNoticeTask(target, false);
					return;
				}
			}
		}
		
		final Skill castingSkill = target.getCastingSkill();
		
		if ((castingSkill != null) && (castingSkill.getOperateType() == OperateType.ACTIVE) && (castingSkill.getSkillType() != SkillType.BUFF))
		{
			if (chance(SkillGroup.SHIELD))
			{
				final Skill skill = actor.getRandomSkill(SkillGroup.SHIELD);
				
				if ((skill != null) && !actor.isSkillDisabled(skill))
				{
					final int range = castingSkill.getRange() + castingSkill.getRadius();
					
					if (actor.isInRange(target, range) && target.isInFront(actor))
					{
						ai.addCastTask(skill, target);
						return;
					}
				}
			}
			
			if (chance(SkillGroup.JUMP))
			{
				final Skill skill = actor.getRandomSkill(SkillGroup.JUMP);
				
				if ((skill != null) && !actor.isSkillDisabled(skill))
				{
					final int range = skill.getMoveDistance();
					final boolean positive = range > 0;
					final boolean isSide = skill.getHeading() != 0;
					
					if (isSide)
					{
						if (castingSkill.isOneTarget())
						{
							ai.addCastTask(skill, actor, Angles.calcHeading(actor.getX(), actor.getY(), target.getX(), target.getY()));
							return;
						}
					}
					else if (target.isDefenseStance() && target.isInFront(actor))
					{
						int newHeading = 0;
						
						if (positive)
						{
							newHeading = actor.calcHeading(target.getX(), target.getY());
						}
						else
						{
							newHeading = target.calcHeading(actor.getX(), actor.getY());
						}
						
						ai.addCastTask(skill, actor, newHeading);
						return;
					}
					else if (!positive)
					{
						if ((castingSkill.getRange() < getShortRange()) && (actor.getGeomDistance(target) < getShortRange()))
						{
							ai.addCastTask(skill, actor, actor.calcHeading(target.getX(), target.getY()));
							return;
						}
					}
					else if (skill.isEvasion())
					{
						ai.addCastTask(skill, target);
						return;
					}
				}
			}
		}
		
		Skill shortSkill = actor.getRandomSkill(SkillGroup.SHORT_ATTACK);
		final Skill longSkill = actor.getRandomSkill(SkillGroup.LONG_ATTACK);
		
		if (actor.getGeomDistance(target) < getShortRange())
		{
			if ((shortSkill != null) && actor.isSkillDisabled(shortSkill))
			{
				shortSkill = actor.getFirstEnabledSkill(SkillGroup.SHORT_ATTACK);
			}
			
			if (shortSkill != null)
			{
				ai.addCastTask(shortSkill, target);
				return;
			}
			else if ((longSkill != null) && !actor.isSkillDisabled(longSkill))
			{
				ai.addCastTask(longSkill, target);
				return;
			}
		}
		else
		{
			if ((longSkill != null) && actor.isSkillDisabled(longSkill))
			{
				shortSkill = actor.getFirstEnabledSkill(SkillGroup.LONG_ATTACK);
			}
			
			if (longSkill != null)
			{
				ai.addCastTask(longSkill, target);
				return;
			}
			else if ((shortSkill != null) && !actor.isSkillDisabled(shortSkill))
			{
				ai.addCastTask(shortSkill, target);
				return;
			}
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
	 * Method getShortRange.
	 * @return int
	 */
	protected final int getShortRange()
	{
		return shortRange;
	}
	
	/**
	 * Method isFastTurn.
	 * @return boolean
	 */
	public boolean isFastTurn()
	{
		return fastTurn;
	}
}
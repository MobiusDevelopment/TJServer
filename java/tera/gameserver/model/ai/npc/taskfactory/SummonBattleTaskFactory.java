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
import tera.gameserver.model.Party;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.MessagePackage;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.OperateType;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillGroup;
import tera.gameserver.tables.MessagePackageTable;
import tera.util.LocalObjects;

import rlib.geom.Angles;
import rlib.util.Rnd;
import rlib.util.Strings;
import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class SummonBattleTaskFactory extends AbstractTaskFactory
{
	protected final MessagePackage evasionMessages;
	protected final MessagePackage buffMessages;
	protected final MessagePackage debuffMesages;
	protected final MessagePackage shieldMessages;
	protected final MessagePackage trapMessages;
	protected final MessagePackage attackMessages;
	protected final MessagePackage healMessages;
	protected final int[] groupChance;
	protected final int shortRange;
	protected final int messageInterval;
	protected final int supportRange;
	protected final boolean fastTurn;
	
	/**
	 * Constructor for SummonBattleTaskFactory.
	 * @param node Node
	 */
	public SummonBattleTaskFactory(Node node)
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
			supportRange = vars.getInteger("supportRange", getShortRange() * 2);
			fastTurn = vars.getBoolean("fastTurn", false);
			
			for (int i = 0, length = groupChance.length; i < length; i++)
			{
				groupChance[i] = vars.getInteger(groups[i].name(), def);
			}
			
			messageInterval = vars.getInteger("messageInterval", 120000);
			final MessagePackageTable messageTable = MessagePackageTable.getInstance();
			evasionMessages = messageTable.getPackage(vars.getString("evasionMessages", Strings.EMPTY));
			buffMessages = messageTable.getPackage(vars.getString("buffMessages", Strings.EMPTY));
			debuffMesages = messageTable.getPackage(vars.getString("debuffMesages", Strings.EMPTY));
			shieldMessages = messageTable.getPackage(vars.getString("shieldMessages", Strings.EMPTY));
			trapMessages = messageTable.getPackage(vars.getString("trapMessages", Strings.EMPTY));
			attackMessages = messageTable.getPackage(vars.getString("attackMessages", Strings.EMPTY));
			healMessages = messageTable.getPackage(vars.getString("healMessages", Strings.EMPTY));
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
		final Character owner = actor.getOwner();
		
		if (owner == null)
		{
			return;
		}
		
		MessagePackage messagePackage = null;
		
		if (chance(SkillGroup.HEAL))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.HEAL);
			
			if ((skill != null) && skill.checkCondition(actor, actor.getX(), actor.getY(), actor.getZ()))
			{
				Character target = null;
				
				if (!skill.isNoCaster() && (actor.getCurrentHp() < actor.getMaxHp()))
				{
					target = actor;
				}
				else if (!skill.isTargetSelf() && (owner.getCurrentHp() < owner.getMaxHp()))
				{
					target = owner;
				}
				else if (!skill.isTargetSelf())
				{
					final Party party = owner.getParty();
					
					if (party != null)
					{
						float distance = getSupportRange();
						final Array<Player> members = party.getMembers();
						members.readLock();
						
						try
						{
							final Player[] array = members.array();
							
							for (int i = 0, length = members.size(); i < length; i++)
							{
								final Player member = array[i];
								
								if ((members == owner) || (member.getCurrentHp() >= member.getMaxHp()))
								{
									continue;
								}
								
								final float dist = owner.getDistance3D(member);
								
								if (dist < distance)
								{
									target = member;
									distance = dist;
								}
							}
						}
						
						finally
						{
							members.readUnlock();
						}
					}
				}
				
				if (target != null)
				{
					String message = Strings.EMPTY;
					messagePackage = getHealMessages();
					
					if ((messagePackage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
					{
						message = messagePackage.getRandomMessage();
						ai.setLastMessage(currentTime + getMessageInterval());
					}
					
					ai.addCastTask(skill, target, message);
					return;
				}
			}
		}
		
		if (chance(SkillGroup.BUFF))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.BUFF);
			
			if ((skill != null) && skill.checkCondition(actor, actor.getX(), actor.getY(), actor.getZ()))
			{
				Character target = null;
				
				if (!actor.containsEffect(skill))
				{
					target = actor;
				}
				else if (skill.isTargetSelf() && !owner.containsEffect(skill))
				{
					target = owner;
				}
				else if (skill.isTargetSelf())
				{
					final Party party = owner.getParty();
					
					if (party != null)
					{
						float distance = getSupportRange();
						final Array<Player> members = party.getMembers();
						members.readLock();
						
						try
						{
							final Player[] array = members.array();
							
							for (int i = 0, length = members.size(); i < length; i++)
							{
								final Player member = array[i];
								
								if ((members == owner) || member.containsEffect(skill))
								{
									continue;
								}
								
								final float dist = owner.getDistance3D(member);
								
								if (dist < distance)
								{
									target = member;
									distance = dist;
								}
							}
						}
						
						finally
						{
							members.readUnlock();
						}
					}
				}
				
				if (target != null)
				{
					String message = Strings.EMPTY;
					messagePackage = getBuffMessages();
					
					if ((messagePackage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
					{
						message = messagePackage.getRandomMessage();
						ai.setLastMessage(currentTime + getMessageInterval());
					}
					
					ai.addCastTask(skill, target, message);
					return;
				}
			}
		}
		
		if (chance(SkillGroup.TRAP))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.TRAP);
			
			if ((skill != null) && skill.checkCondition(actor, actor.getX(), actor.getY(), actor.getZ()))
			{
				String message = Strings.EMPTY;
				messagePackage = getTrapMessages();
				
				if ((messagePackage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
				{
					message = messagePackage.getRandomMessage();
					ai.setLastMessage(currentTime + getMessageInterval());
				}
				
				ai.addCastTask(skill, actor, message);
				return;
			}
		}
		
		final Character target = ai.getTarget();
		
		if (target == null)
		{
			return;
		}
		
		if (!actor.isBattleStanced())
		{
			actor.startBattleStance(target);
		}
		
		if (chance(SkillGroup.DEBUFF))
		{
			final Skill skill = actor.getRandomSkill(SkillGroup.DEBUFF);
			
			if ((skill != null) && skill.checkCondition(actor, target.getX(), target.getY(), target.getZ()))
			{
				String message = Strings.EMPTY;
				messagePackage = getDebuffMesages();
				
				if ((messagePackage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
				{
					message = messagePackage.getRandomMessage();
					ai.setLastMessage(currentTime + getMessageInterval());
				}
				
				ai.addCastTask(skill, target, message);
				return;
			}
		}
		
		if (!actor.isBattleStanced())
		{
			ai.addNoticeTask(target, true);
			return;
		}
		
		final Skill castingSkill = target.getCastingSkill();
		
		if ((castingSkill != null) && (castingSkill.getOperateType() == OperateType.ACTIVE))
		{
			if (chance(SkillGroup.SHIELD))
			{
				final Skill skill = actor.getRandomSkill(SkillGroup.SHIELD);
				
				if ((skill != null) && skill.checkCondition(actor, actor.getX(), actor.getY(), actor.getZ()))
				{
					final int range = castingSkill.getRange() + castingSkill.getRadius();
					
					if (actor.isInRange(target, range) && target.isInFront(actor))
					{
						String message = Strings.EMPTY;
						messagePackage = getShieldMessages();
						
						if ((messagePackage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
						{
							message = messagePackage.getRandomMessage();
							ai.setLastMessage(currentTime + getMessageInterval());
						}
						
						ai.addCastTask(skill, target, message);
						return;
					}
				}
			}
			
			if (chance(SkillGroup.JUMP))
			{
				final Skill skill = actor.getRandomSkill(SkillGroup.JUMP);
				
				if ((skill != null) && skill.checkCondition(actor, actor.getX(), actor.getY(), actor.getZ()))
				{
					String message = Strings.EMPTY;
					messagePackage = getEvasionMessages();
					
					if ((messagePackage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
					{
						message = messagePackage.getRandomMessage();
						ai.setLastMessage(currentTime + getMessageInterval());
					}
					
					final int range = skill.getMoveDistance();
					final boolean positive = range > 0;
					final boolean isSide = skill.getHeading() != 0;
					
					if (isSide)
					{
						if (castingSkill.isOneTarget())
						{
							ai.addCastTask(skill, actor, Angles.calcHeading(actor.getX(), actor.getY(), target.getX(), target.getY()), message);
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
						
						ai.addCastTask(skill, actor, newHeading, message);
						return;
					}
					else if (!positive)
					{
						if ((castingSkill.getRange() < getShortRange()) && (actor.getGeomDistance(target) < getShortRange()))
						{
							ai.addCastTask(skill, actor, actor.calcHeading(target.getX(), target.getY()), message);
							return;
						}
					}
					else if (skill.isEvasion())
					{
						ai.addCastTask(skill, target, message);
						return;
					}
				}
			}
		}
		
		Skill shortSkill = actor.getRandomSkill(SkillGroup.SHORT_ATTACK);
		Skill longSkill = actor.getRandomSkill(SkillGroup.LONG_ATTACK);
		String message = Strings.EMPTY;
		messagePackage = getAttackMessages();
		
		if ((messagePackage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
		{
			message = messagePackage.getRandomMessage();
			ai.setLastMessage(currentTime + getMessageInterval());
		}
		
		if (actor.getGeomDistance(target) < getShortRange())
		{
			for (int i = 0, length = 5; ((shortSkill == null) || !shortSkill.checkCondition(actor, actor.getX(), actor.getY(), actor.getZ())) && (i < length); i++)
			{
				shortSkill = actor.getRandomSkill(SkillGroup.SHORT_ATTACK);
			}
			
			if ((shortSkill != null) && !actor.isSkillDisabled(shortSkill))
			{
				ai.addCastTask(shortSkill, target, message);
				return;
			}
			else if ((longSkill != null) && !actor.isSkillDisabled(longSkill))
			{
				ai.addCastTask(longSkill, target, message);
				return;
			}
		}
		else
		{
			for (int i = 0, length = 5; ((longSkill == null) || !longSkill.checkCondition(actor, actor.getX(), actor.getY(), actor.getZ())) && (i < length); i++)
			{
				longSkill = actor.getRandomSkill(SkillGroup.LONG_ATTACK);
			}
			
			if ((longSkill != null) && !actor.isSkillDisabled(longSkill))
			{
				ai.addCastTask(longSkill, target, message);
				return;
			}
			else if ((shortSkill != null) && !actor.isSkillDisabled(shortSkill))
			{
				ai.addCastTask(shortSkill, target, message);
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
	 * Method getAttackMessages.
	 * @return MessagePackage
	 */
	public MessagePackage getAttackMessages()
	{
		return attackMessages;
	}
	
	/**
	 * Method getBuffMessages.
	 * @return MessagePackage
	 */
	public MessagePackage getBuffMessages()
	{
		return buffMessages;
	}
	
	/**
	 * Method getDebuffMesages.
	 * @return MessagePackage
	 */
	public MessagePackage getDebuffMesages()
	{
		return debuffMesages;
	}
	
	/**
	 * Method getEvasionMessages.
	 * @return MessagePackage
	 */
	public MessagePackage getEvasionMessages()
	{
		return evasionMessages;
	}
	
	/**
	 * Method getMessageInterval.
	 * @return int
	 */
	public int getMessageInterval()
	{
		return messageInterval;
	}
	
	/**
	 * Method getShieldMessages.
	 * @return MessagePackage
	 */
	public MessagePackage getShieldMessages()
	{
		return shieldMessages;
	}
	
	/**
	 * Method getTrapMessages.
	 * @return MessagePackage
	 */
	public MessagePackage getTrapMessages()
	{
		return trapMessages;
	}
	
	/**
	 * Method getHealMessages.
	 * @return MessagePackage
	 */
	public MessagePackage getHealMessages()
	{
		return healMessages;
	}
	
	/**
	 * Method getSupportRange.
	 * @return int
	 */
	public int getSupportRange()
	{
		return supportRange;
	}
}
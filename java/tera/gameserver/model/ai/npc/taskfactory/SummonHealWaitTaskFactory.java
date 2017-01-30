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
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillGroup;
import tera.gameserver.tables.MessagePackageTable;
import tera.util.LocalObjects;

import rlib.util.Rnd;
import rlib.util.Strings;
import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class SummonHealWaitTaskFactory extends SummonWaitTaskFactory
{
	protected final MessagePackage healMessages;
	protected final MessagePackage buffMessages;
	protected final int[] groupChance;
	protected final int supportRange;
	
	/**
	 * Constructor for SummonHealWaitTaskFactory.
	 * @param node Node
	 */
	public SummonHealWaitTaskFactory(Node node)
	{
		super(node);
		
		try
		{
			VarTable vars = VarTable.newInstance(node);
			final SkillGroup[] groups = SkillGroup.values();
			vars = VarTable.newInstance(node, "set", "name", "val");
			groupChance = new int[groups.length];
			supportRange = vars.getInteger("supportRange", 300);
			
			for (int i = 0, length = groupChance.length; i < length; i++)
			{
				groupChance[i] = vars.getInteger(groups[i].name(), 0);
			}
			
			final MessagePackageTable messageTable = MessagePackageTable.getInstance();
			buffMessages = messageTable.getPackage(vars.getString("buffMessages", Strings.EMPTY));
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
		
		super.addNewTask(ai, actor, local, config, currentTime);
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
	 * Method getBuffMessages.
	 * @return MessagePackage
	 */
	public MessagePackage getBuffMessages()
	{
		return buffMessages;
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
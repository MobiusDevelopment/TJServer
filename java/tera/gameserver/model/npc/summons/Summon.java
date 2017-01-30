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
package tera.gameserver.model.npc.summons;

import tera.gameserver.IdFactory;
import tera.gameserver.model.Character;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.network.serverpackets.TargetHp;
import tera.gameserver.templates.NpcTemplate;

import rlib.idfactory.IdGenerator;
import rlib.idfactory.IdGenerators;

/**
 * @author Ronn
 */
public class Summon extends Npc
{
	private static final IdGenerator ID_FACTORY = IdGenerators.newSimpleIdGenerator(1300001, 1600000);
	
	protected Character owner;
	
	/**
	 * Constructor for Summon.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public Summon(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method addAggro.
	 * @param aggressor Character
	 * @param aggro long
	 * @param damage boolean
	 */
	@Override
	public void addAggro(Character aggressor, long aggro, boolean damage)
	{
	}
	
	/**
	 * Method subAggro.
	 * @param aggressor Character
	 * @param aggro long
	 */
	@Override
	public void subAggro(Character aggressor, long aggro)
	{
	}
	
	/**
	 * Method getMostDamager.
	 * @return Character
	 */
	@Override
	public Character getMostDamager()
	{
		return null;
	}
	
	@Override
	public void clearAggroList()
	{
	}
	
	/**
	 * Method getAggro.
	 * @param aggressor Character
	 * @return long
	 */
	@Override
	public long getAggro(Character aggressor)
	{
		return 0;
	}
	
	/**
	 * Method checkTarget.
	 * @param target Character
	 * @return boolean
	 */
	@Override
	public boolean checkTarget(Character target)
	{
		final Character owner = getOwner();
		
		if (owner != null)
		{
			return owner.checkTarget(target);
		}
		
		return false;
	}
	
	/**
	 * Method addCounter.
	 * @param attacker Character
	 */
	@Override
	protected void addCounter(Character attacker)
	{
	}
	
	/**
	 * Method doDie.
	 * @param attacker Character
	 */
	@Override
	public void doDie(Character attacker)
	{
		final Character owner = getOwner();
		
		if ((owner != null) && owner.isPlayer())
		{
			if (owner == attacker)
			{
				owner.sendMessage(MessageType.YOUR_PET_HAS_BEEN_DESTRUYED);
			}
			else
			{
				owner.sendPacket(SystemMessage.getInstance(MessageType.ATTACKER_DESTROYED_YOUR_PET).addAttacker(attacker.getName()), true);
			}
		}
		
		if (owner != null)
		{
			owner.setSummon(null);
		}
		
		super.doDie(attacker);
		getAI().stopAITask();
	}
	
	/**
	 * Method calculateRewards.
	 * @param killer Character
	 */
	@Override
	protected void calculateRewards(Character killer)
	{
	}
	
	/**
	 * Method getMostHated.
	 * @return Character
	 */
	@Override
	public Character getMostHated()
	{
		return null;
	}
	
	/**
	 * Method removeAggro.
	 * @param agressor Character
	 */
	@Override
	public void removeAggro(Character agressor)
	{
	}
	
	/**
	 * Method getOwerturnTime.
	 * @return int
	 */
	@Override
	public int getOwerturnTime()
	{
		return 3500;
	}
	
	/**
	 * Method isSummon.
	 * @return boolean
	 */
	@Override
	public final boolean isSummon()
	{
		return true;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		final IdFactory idFactory = IdFactory.getInstance();
		setObjectId(idFactory.getNextNpcId());
		getAI().startAITask();
	}
	
	public void remove()
	{
		if (!isDead())
		{
			setCurrentHp(0);
			doDie(owner);
		}
	}
	
	/**
	 * Method setOwner.
	 * @param owner Character
	 */
	@Override
	public void setOwner(Character owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Method getOwner.
	 * @return Character
	 */
	@Override
	public Character getOwner()
	{
		return owner;
	}
	
	@Override
	public void updateHp()
	{
		final Character owner = getOwner();
		
		if ((owner != null) && owner.isPlayer())
		{
			owner.sendPacket(TargetHp.getInstance(this, TargetHp.BLUE), true);
		}
	}
	
	/**
	 * Method nextCastId.
	 * @return int
	 */
	@Override
	public int nextCastId()
	{
		return ID_FACTORY.getNextId();
	}
	
	/**
	 * Method effectHealHp.
	 * @param heal int
	 * @param healer Character
	 */
	@Override
	public void effectHealHp(int heal, Character healer)
	{
		super.effectHealHp(heal, healer);
		updateHp();
	}
	
	@Override
	public void doRegen()
	{
		final int currentHp = getCurrentHp();
		super.doRegen();
		
		if (currentHp != getCurrentHp())
		{
			updateHp();
		}
	}
}

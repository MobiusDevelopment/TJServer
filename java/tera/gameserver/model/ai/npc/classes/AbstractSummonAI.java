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
package tera.gameserver.model.ai.npc.classes;

import tera.gameserver.model.Character;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.model.skillengine.Skill;

/**
 * @author Ronn
 */
public abstract class AbstractSummonAI extends AbstractNpcAI<Summon>
{
	/**
	 * Constructor for AbstractSummonAI.
	 * @param actor Summon
	 * @param config ConfigAI
	 */
	public AbstractSummonAI(Summon actor, ConfigAI config)
	{
		super(actor, config);
	}
	
	/**
	 * Method startAttack.
	 * @param target Character
	 * @see tera.gameserver.model.ai.CharacterAI#startAttack(Character)
	 */
	@Override
	public void startAttack(Character target)
	{
		setTarget(target);
		final Summon actor = getActor();
		
		if ((actor == null) || actor.isDead())
		{
			return;
		}
		
		actor.abortCast(true);
		actor.stopMove();
	}
	
	/**
	 * Method abortAttack.
	 * @see tera.gameserver.model.ai.CharacterAI#abortAttack()
	 */
	@Override
	public void abortAttack()
	{
		setTarget(null);
		final Summon actor = getActor();
		
		if ((actor == null) || actor.isDead())
		{
			return;
		}
		
		actor.abortCast(true);
		actor.stopMove();
	}
	
	/**
	 * Method notifyAttacked.
	 * @param attacker Character
	 * @param skill Skill
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyAttacked(Character, Skill, int)
	 */
	@Override
	public void notifyAttacked(Character attacker, Skill skill, int damage)
	{
		final Character target = getTarget();
		
		if (target == null)
		{
			setTarget(attacker);
		}
	}
	
	/**
	 * Method notifyDead.
	 * @param killer Character
	 * @see tera.gameserver.model.ai.AI#notifyDead(Character)
	 */
	@Override
	public void notifyDead(Character killer)
	{
		setTarget(null);
	}
}

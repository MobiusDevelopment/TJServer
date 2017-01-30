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
import tera.gameserver.model.npc.playable.EventEpicBattleNpc;

import rlib.util.Rnd;

/**
 * @author Ronn
 */
public final class EpicBattleAI extends AbstractNpcAI<EventEpicBattleNpc>
{
	/**
	 * Constructor for EpicBattleAI.
	 * @param actor EventEpicBattleNpc
	 * @param config ConfigAI
	 */
	public EpicBattleAI(EventEpicBattleNpc actor, ConfigAI config)
	{
		super(actor, config);
	}
	
	/**
	 * Method notifyClanAttacked.
	 * @param attackedMember Character
	 * @param attacker Character
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyClanAttacked(Character, Character, int)
	 */
	@Override
	public void notifyClanAttacked(Character attackedMember, Character attacker, int damage)
	{
		super.notifyClanAttacked(attackedMember, attacker, 1);
	}
	
	/**
	 * Method checkAggression.
	 * @param target Character
	 * @return boolean
	 * @see tera.gameserver.model.ai.npc.NpcAI#checkAggression(Character)
	 */
	@Override
	public boolean checkAggression(Character target)
	{
		final EventEpicBattleNpc actor = getActor();
		
		if ((actor == null) || (target == null) || !target.isInRange(actor, actor.getAggroRange()))
		{
			return false;
		}
		
		if (actor.checkTarget(target) && Rnd.chance(25))
		{
			actor.addAggro(target, (long) (actor.getMaxHp() * 0.05F), true);
			return true;
		}
		
		return false;
	}
}
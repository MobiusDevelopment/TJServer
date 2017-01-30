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
package tera.gameserver.model.skillengine.classes;

import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.Party;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class LockOnStrikePartyBuff extends LockOnStrike
{
	/**
	 * Constructor for LockOnStrikePartyBuff.
	 * @param template SkillTemplate
	 */
	public LockOnStrikePartyBuff(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method applySkill.
	 * @param attacker Character
	 * @param target Character
	 * @return AttackInfo
	 * @see tera.gameserver.model.skillengine.Skill#applySkill(Character, Character)
	 */
	@Override
	public AttackInfo applySkill(Character attacker, Character target)
	{
		final LocalObjects local = LocalObjects.get();
		final Formulas formulas = Formulas.getInstance();
		final AttackInfo info = formulas.calcDamageSkill(local.getNextAttackInfo(), this, attacker, target);
		target.causingDamage(this, info, attacker);
		
		if (!info.isBlocked())
		{
			addEffects(attacker, attacker);
			addAggroTo(attacker, attacker, getAggroPoint());
			final Party party = attacker.getParty();
			
			if (party != null)
			{
				final Array<Player> members = party.getMembers();
				members.readLock();
				
				try
				{
					final Player[] array = members.array();
					
					for (int i = 0, length = members.size(); i < length; i++)
					{
						final Player member = array[i];
						
						if ((member == null) || (member == attacker) || !attacker.isInRange(member, getRadius()))
						{
							continue;
						}
						
						addEffects(attacker, member);
						addAggroTo(attacker, member, getAggroPoint());
					}
				}
				
				finally
				{
					members.readUnlock();
				}
			}
		}
		
		return info;
	}
}
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
package tera.gameserver.model.skillengine.targethandler;

import tera.gameserver.model.Character;
import tera.gameserver.model.Party;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class AreaPartyTargetHandler extends AreaTargetHandler
{
	/**
	 * Method addAllTargets.
	 * @param targets Array<Character>
	 * @param caster Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param radius int
	 */
	@Override
	protected void addAllTargets(Array<Character> targets, Character caster, float targetX, float targetY, float targetZ, int radius)
	{
		final Party party = caster.getParty();
		
		if (party == null)
		{
			targets.add(caster);
			final Summon summon = caster.getSummon();
			
			if (summon != null)
			{
				targets.add(summon);
			}
		}
		else
		{
			final Array<Player> members = party.getMembers();
			members.readLock();
			
			try
			{
				for (Player member : members.array())
				{
					if (member == null)
					{
						break;
					}
					
					targets.add(member);
					final Summon summon = member.getSummon();
					
					if (summon != null)
					{
						targets.add(summon);
					}
				}
			}
			
			finally
			{
				members.readUnlock();
			}
		}
	}
	
	/**
	 * Method checkTarget.
	 * @param caster Character
	 * @param target Character
	 * @return boolean
	 */
	@Override
	protected boolean checkTarget(Character caster, Character target)
	{
		return true;
	}
}
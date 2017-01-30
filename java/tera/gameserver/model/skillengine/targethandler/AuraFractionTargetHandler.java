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
import tera.gameserver.model.World;
import tera.gameserver.model.npc.Npc;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class AuraFractionTargetHandler extends AuraTargetHandler
{
	/**
	 * Method addAllTargets.
	 * @param targets Array<Character>
	 * @param caster Character
	 * @param radius int
	 */
	@Override
	protected void addAllTargets(Array<Character> targets, Character caster, int radius)
	{
		World.getAround(Npc.class, targets, caster, radius).add(caster);
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
		if (caster == target)
		{
			return true;
		}
		
		if (!caster.isNpc() || !target.isNpc())
		{
			return false;
		}
		
		final Npc casterNpc = caster.getNpc();
		final Npc targetNpc = target.getNpc();
		return casterNpc.getFraction().equals(targetNpc.getFraction());
	}
}
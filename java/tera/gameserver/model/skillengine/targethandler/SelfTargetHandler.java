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
import tera.gameserver.model.skillengine.Skill;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public final class SelfTargetHandler extends AbstractTargetHandler
{
	/**
	 * Method addTargetsTo.
	 * @param targets Array<Character>
	 * @param caster Character
	 * @param skill Skill
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	@Override
	public void addTargetsTo(Array<Character> targets, Character caster, Skill skill, float targetX, float targetY, float targetZ)
	{
		targets.add(caster);
	}
}
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

/**
 * @author Ronn
 */
public abstract class AbstractTargetHandler implements TargetHandler
{
	/**
	 * Method checkTarget.
	 * @param caster Character
	 * @param target Character
	 * @return boolean
	 */
	protected boolean checkTarget(Character caster, Character target)
	{
		return caster.checkTarget(target);
	}
	
	/**
	 * Method updateImpact.
	 * @param skill Skill
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	protected void updateImpact(Skill skill, float targetX, float targetY, float targetZ)
	{
		skill.setImpactX(targetX);
		skill.setImpactY(targetY);
		skill.setImpactZ(targetZ);
	}
}
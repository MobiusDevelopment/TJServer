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

import tera.Config;
import tera.gameserver.manager.DebugManager;
import tera.gameserver.manager.GeoManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.skillengine.Skill;

import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class AreaTargetHandler extends AbstractTargetHandler
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
	protected void addAllTargets(Array<Character> targets, Character caster, float targetX, float targetY, float targetZ, int radius)
	{
		World.getAround(Character.class, targets, caster.getContinentId(), targetX, targetY, targetZ, caster.getObjectId(), caster.getSubId(), radius);
	}
	
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
		final int radius = skill.getRadius();
		final float radians = Angles.headingToRadians(caster.getHeading() + skill.getHeading());
		targetX = Coords.calcX(caster.getX(), skill.getRange(), radians);
		targetY = Coords.calcY(caster.getY(), skill.getRange(), radians);
		final GeoManager geoManager = GeoManager.getInstance();
		targetZ = geoManager.getHeight(caster.getContinentId(), targetX, targetY, caster.getZ());
		updateImpact(skill, targetX, targetY, targetZ);
		
		if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
		{
			DebugManager.showAreaDebug(caster.getContinentId(), targetX, targetY, targetZ, radius);
		}
		
		addAllTargets(targets, caster, targetX, targetY, targetZ, radius);
		
		if (!targets.isEmpty())
		{
			final Character[] array = targets.array();
			
			for (int i = 0, length = targets.size(); i < length; i++)
			{
				final Character target = array[i];
				
				if (!checkTarget(caster, target))
				{
					targets.fastRemove(i--);
					length--;
					continue;
				}
				
				if (!target.isHit(targetX, targetY, targetZ, 100, radius))
				{
					targets.fastRemove(i--);
					length--;
				}
			}
		}
	}
}
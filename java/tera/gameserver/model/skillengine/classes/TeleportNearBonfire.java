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

import tera.gameserver.model.Character;
import tera.gameserver.model.territory.BonfireTerritory;
import tera.gameserver.tables.BonfireTable;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class TeleportNearBonfire extends AbstractSkill
{
	private final Location loc;
	
	/**
	 * Constructor for TeleportNearBonfire.
	 * @param template SkillTemplate
	 */
	public TeleportNearBonfire(SkillTemplate template)
	{
		super(template);
		loc = new Location();
	}
	
	/**
	 * Method useSkill.
	 * @param character Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#useSkill(Character, float, float, float)
	 */
	@Override
	public void useSkill(Character character, float targetX, float targetY, float targetZ)
	{
		final LocalObjects local = LocalObjects.get();
		final Array<Character> targets = local.getNextCharList();
		addTargets(targets, character, targetX, targetY, targetZ);
		
		if (targets.isEmpty())
		{
			return;
		}
		
		final BonfireTerritory near = BonfireTable.getNearBonfire(character);
		
		if (near == null)
		{
			return;
		}
		
		for (Character player : targets)
		{
			if (!player.isDead())
			{
				player.teleToLocation(Coords.randomCoords(loc, near.getCenterX(), near.getCenterY(), near.getCenterZ(), 60, 150));
			}
		}
	}
}
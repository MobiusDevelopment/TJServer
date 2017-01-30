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
import tera.gameserver.model.MessageType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.worldobject.BonfireObject;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;

import rlib.geom.Coords;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class SpawnBonfire extends AbstractSkill
{
	/**
	 * Constructor for SpawnBonfire.
	 * @param template SkillTemplate
	 */
	public SpawnBonfire(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method checkCondition.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Skill#checkCondition(Character, float, float, float)
	 */
	@Override
	public boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ)
	{
		final LocalObjects local = LocalObjects.get();
		final Array<TObject> bonfires = World.getAround(BonfireObject.class, local.getNextObjectList(), attacker, 200);
		
		if (!bonfires.isEmpty())
		{
			attacker.sendMessage(MessageType.THERE_ANOTHER_CAMPFIRE_NEAR_HERE);
			return false;
		}
		
		return super.checkCondition(attacker, targetX, targetY, targetZ);
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
		final float x = Coords.calcX(character.getX(), 5, character.getHeading());
		final float y = Coords.calcY(character.getY(), 5, character.getHeading());
		BonfireObject.startBonfire(template.getRegenPower(), template.getLifeTime(), character.getContinentId(), x, y, character.getZ());
	}
}

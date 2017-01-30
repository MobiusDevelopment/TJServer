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
import tera.gameserver.model.MoveType;
import tera.gameserver.network.serverpackets.SkillLeash;
import tera.gameserver.network.serverpackets.SkillStart;
import tera.gameserver.templates.SkillTemplate;

import rlib.geom.Coords;
import rlib.util.Rnd;

/**
 * @author Ronn
 */
public class Leash extends Strike
{
	/**
	 * Constructor for Leash.
	 * @param template SkillTemplate
	 */
	public Leash(SkillTemplate template)
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
		final AttackInfo info = super.applySkill(attacker, target);
		
		if (!info.isBlocked() && !target.isLeashImmunity())
		{
			final boolean result = Rnd.chance(80);
			
			if (result)
			{
				target.stopMove();
				target.abortCast(true);
				final int distance = (int) (attacker.getGeomRadius() + target.getGeomRadius());
				final float newX = Coords.calcX(attacker.getX(), distance, attacker.getHeading());
				final float newY = Coords.calcY(attacker.getY(), distance, attacker.getHeading());
				target.setXYZ(newX, newY, attacker.getZ());
				target.broadcastMove(target.getX(), target.getY(), target.getZ(), target.getHeading(), MoveType.STOP, target.getX(), target.getY(), target.getZ(), true);
			}
			
			attacker.broadcastPacket(SkillLeash.getInstance(attacker.getObjectId(), attacker.getSubId(), target.getObjectId(), target.getSubId(), result));
		}
		
		return info;
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
		super.useSkill(character, targetX, targetY, targetZ);
		character.broadcastPacket(SkillStart.getInstance(character, template.getIconId(), castId, 1));
	}
}
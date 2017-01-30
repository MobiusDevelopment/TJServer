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
import tera.gameserver.model.skillengine.shots.FastShot;
import tera.gameserver.network.serverpackets.StartFastShot;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class NpcSingleFastShot extends Strike
{
	private Character target;
	
	/**
	 * Constructor for NpcSingleFastShot.
	 * @param template SkillTemplate
	 */
	public NpcSingleFastShot(SkillTemplate template)
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
		target = attacker.getTarget();
		
		if (target == null)
		{
			return false;
		}
		
		return super.checkCondition(attacker, targetX, targetY, targetZ);
	}
	
	/**
	 * Method startSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#startSkill(Character, float, float, float)
	 */
	@Override
	public void startSkill(Character attacker, float targetX, float targetY, float targetZ)
	{
		super.startSkill(attacker, targetX, targetY, targetZ);
		impactX = attacker.getX();
		impactY = attacker.getY();
		impactZ = attacker.getZ();
		attacker.broadcastPacket(StartFastShot.getInstance(attacker, this, castId, target.getX(), target.getY(), target.getZ() + (target.getGeomHeight() * 0.5F)));
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
		if (target != null)
		{
			FastShot.startShot(character, this, target.getX(), target.getY(), target.getZ() + (target.getGeomHeight() * 0.5F));
		}
		
		target = null;
	}
}

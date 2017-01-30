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
import tera.gameserver.model.skillengine.shots.FastAutoShot;
import tera.gameserver.model.skillengine.shots.FastShot;
import tera.gameserver.network.serverpackets.StartFastShot;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class AutoSingleShot extends Strike
{
	
	private Character target;
	
	/**
	 * Constructor for AutoSingleShot.
	 * @param template SkillTemplate
	 */
	public AutoSingleShot(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method endSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param force boolean
	 * @see tera.gameserver.model.skillengine.Skill#endSkill(Character, float, float, float, boolean)
	 */
	@Override
	public void endSkill(Character attacker, float targetX, float targetY, float targetZ, boolean force)
	{
		setTarget(null);
		super.endSkill(attacker, targetX, targetY, targetZ, force);
	}
	
	/**
	 * Method getTarget.
	 * @return Character
	 */
	public Character getTarget()
	{
		return target;
	}
	
	/**
	 * Method setTarget.
	 * @param target Character
	 */
	public void setTarget(Character target)
	{
		this.target = target;
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
		setImpactX(attacker.getX());
		setImpactY(attacker.getY());
		setImpactZ(attacker.getZ());
		super.startSkill(attacker, targetX, targetY, targetZ);
		final Character target = attacker.getTarget();
		setTarget(target);
		
		if ((target != null) && attacker.checkTarget(target))
		{
			attacker.broadcastPacket(StartFastShot.getInstance(attacker, target, this, castId));
		}
		else
		{
			attacker.broadcastPacket(StartFastShot.getInstance(attacker, this, castId, targetX, targetY, targetZ));
		}
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
		final Character target = getTarget();
		
		if ((target != null) && character.checkTarget(target))
		{
			FastAutoShot.startShot(character, target, this);
		}
		else
		{
			FastShot.startShot(character, this, targetX, targetY, targetZ);
		}
	}
}

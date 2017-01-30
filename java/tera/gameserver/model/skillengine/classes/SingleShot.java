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
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.shots.ObjectShot;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;

/**
 * @author Ronn
 */
public class SingleShot extends Strike
{
	/**
	 * Constructor for SingleShot.
	 * @param template SkillTemplate
	 */
	public SingleShot(SkillTemplate template)
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
		final LocalObjects local = LocalObjects.get();
		final Formulas formulas = Formulas.getInstance();
		final AttackInfo info = formulas.calcDamageSkill(local.getNextAttackInfo(), this, attacker, target);
		fineRange(attacker, target, info);
		target.causingDamage(this, info, attacker);
		
		if (!info.isBlocked())
		{
			addEffects(attacker, target);
		}
		
		return info;
	}
	
	/**
	 * Method fineRange.
	 * @param attacker Character
	 * @param attacked Character
	 * @param info AttackInfo
	 */
	protected void fineRange(Character attacker, Character attacked, AttackInfo info)
	{
		if (info.getDamage() < 2)
		{
			return;
		}
		
		final float range = getRange() * getRange();
		final float current = Math.max(attacker.getSquareDistance(attacked.getX(), attacked.getY(), attacked.getZ()), 1F);
		info.divDamage(3F - Math.min(range / current, 2F));
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
		setImpactX(character.getX());
		setImpactY(character.getY());
		setImpactZ(character.getZ());
		ObjectShot.startShot(character, this, targetX, targetY, targetZ);
	}
}

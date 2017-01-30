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
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;

/**
 * @author Ronn
 */
public class WarriorFuryStrike extends Strike
{
	private static final float[] MOD = new float[101];
	
	static
	{
		for (int i = 0; i < MOD.length; i++)
		{
			MOD[i] = Math.min((100 + (500 / Math.max(i, 1))) - 5, 140) / 100F;
		}
	}
	
	/**
	 * Constructor for WarriorFuryStrike.
	 * @param template SkillTemplate
	 */
	public WarriorFuryStrike(SkillTemplate template)
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
		info.mulDamage(MOD[attacker.getCurrentHpPercent()]);
		target.causingDamage(this, info, attacker);
		
		if (!info.isBlocked())
		{
			addEffects(attacker, target);
		}
		
		return info;
	}
}

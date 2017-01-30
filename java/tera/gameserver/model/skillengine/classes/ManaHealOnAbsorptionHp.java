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
import tera.gameserver.templates.SkillTemplate;

import rlib.util.wraps.Wrap;
import rlib.util.wraps.WrapType;

/**
 * @author Ronn
 */
public class ManaHealOnAbsorptionHp extends ManaHeal
{
	
	private int power;
	
	/**
	 * Constructor for ManaHealOnAbsorptionHp.
	 * @param template SkillTemplate
	 */
	public ManaHealOnAbsorptionHp(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method getPower.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Skill#getPower()
	 */
	@Override
	public int getPower()
	{
		return power;
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
		power = 0;
		final Wrap wrap = attacker.getSkillVariables().get(template.getId());
		
		if ((wrap != null) && (wrap.getWrapType() == WrapType.INTEGER))
		{
			power = wrap.getInt();
			wrap.setInt(0);
		}
	}
}

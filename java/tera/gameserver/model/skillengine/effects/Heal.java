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
package tera.gameserver.model.skillengine.effects;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class Heal extends AbstractEffect
{
	/**
	 * Constructor for Heal.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 */
	public Heal(EffectTemplate template, Character effector, Character effected, SkillTemplate skill)
	{
		super(template, effector, effected, skill);
	}
	
	/**
	 * Method onActionTime.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		final Character effected = getEffected();
		final Character effector = getEffector();
		
		if ((effected == null) || (effector == null))
		{
			return false;
		}
		
		if (effected.getCurrentHp() >= effected.getMaxHp())
		{
			return true;
		}
		
		int power = template.getPower();
		power = (int) (power * effector.calcStat(StatType.HEAL_POWER_PERCENT, 1, null, null));
		power += effector.calcStat(StatType.HEAL_POWER_STATIC, 0, null, null);
		
		if (power < 1)
		{
			return true;
		}
		
		effected.effectHealHp(power, effector);
		return true;
	}
}

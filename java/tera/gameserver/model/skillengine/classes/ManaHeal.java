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
import tera.util.LocalObjects;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class ManaHeal extends AbstractSkill
{
	/**
	 * Constructor for ManaHeal.
	 * @param template SkillTemplate
	 */
	public ManaHeal(SkillTemplate template)
	{
		super(template);
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
		final int power = getPower();
		
		if (power < 1)
		{
			return;
		}
		
		final LocalObjects local = LocalObjects.get();
		final Array<Character> targets = local.getNextCharList();
		addTargets(targets, character, targetX, targetY, targetZ);
		final Character[] array = targets.array();
		
		for (int i = 0, length = targets.size(); i < length; i++)
		{
			final Character target = array[i];
			
			if (target.isDead() || target.isInvul())
			{
				continue;
			}
			
			addEffects(character, target);
			target.skillHealMp(getDamageId(), power, character);
		}
	}
}

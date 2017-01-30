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
package tera.gameserver.model.skillengine.conditions;

import tera.gameserver.model.Character;
import tera.gameserver.model.EffectList;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Skill;

import rlib.util.array.Array;

/**
 * @author Ronn
 * @created 13.03.2012
 */
public class ConditionHasEffectId extends AbstractCondition
{
	private final int effectId;
	
	/**
	 * Constructor for ConditionHasEffectId.
	 * @param effectId int
	 */
	public ConditionHasEffectId(int effectId)
	{
		this.effectId = effectId;
	}
	
	/**
	 * Method test.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @param val float
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Condition#test(Character, Character, Skill, float)
	 */
	@Override
	public boolean test(Character attacker, Character attacked, Skill skill, float val)
	{
		final EffectList effectList = attacker.getEffectList();
		
		if ((effectList == null) || (effectList.size() < 1))
		{
			return false;
		}
		
		final Array<Effect> effects = effectList.getEffects();
		final Effect[] array = effects.array();
		
		for (int i = 0, length = effects.size(); i < length; i++)
		{
			final Effect effect = array[i];
			
			if ((effect != null) && (effect.getEffectId() == effectId))
			{
				return true;
			}
		}
		
		return false;
	}
}
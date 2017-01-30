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
import tera.gameserver.model.EffectList;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class CancelDebuff extends AbstractEffect
{
	/**
	 * Constructor for CancelDebuff.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 */
	public CancelDebuff(EffectTemplate template, Character effector, Character effected, SkillTemplate skill)
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
		final EffectList effectList = effected.getEffectList();
		
		if ((effectList == null) || (effectList.size() < 1))
		{
			return true;
		}
		
		final Array<Effect> effects = effectList.getEffects();
		final Effect[] array = effects.array();
		
		for (int g = 0, length = effects.size(); g < length; g++)
		{
			final Effect effect = array[g];
			
			if ((effect == null) || effect.isAura() || !effect.isDebuff())
			{
				continue;
			}
			
			effect.exit();
		}
		
		return true;
	}
}
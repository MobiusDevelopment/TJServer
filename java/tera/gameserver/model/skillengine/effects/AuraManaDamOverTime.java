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

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.EffectList;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class AuraManaDamOverTime extends AbstractAura
{
	/**
	 * Constructor for AuraManaDamOverTime.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 */
	public AuraManaDamOverTime(EffectTemplate template, Character effector, Character effected, SkillTemplate skill)
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
		final Character effector = getEffector();
		
		if (effector.isDead())
		{
			return false;
		}
		
		final EffectList effectList = effector.getEffectList();
		
		if ((effectList == null) || !effectList.contains(this))
		{
			return false;
		}
		
		final Character effected = getEffected();
		
		if (effected == null)
		{
			return false;
		}
		
		if (((effected != effector) && (effected.getParty() == null)) || (effected.getParty() != effector.getParty()))
		{
			return false;
		}
		
		if (effected == effector)
		{
			final int cost = template.getPower();
			
			if (effected.getCurrentMp() < cost)
			{
				return false;
			}
			
			effector.setCurrentMp(effector.getCurrentMp() - cost);
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyMpChanged(effector);
		}
		else if (!effector.isInRange(effected, 1400))
		{
			return false;
		}
		
		return true;
	}
}

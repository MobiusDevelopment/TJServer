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

import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.Character;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.geom.Angles;

/**
 * @author Ronn
 */
public class Turn extends AbstractEffect
{
	/**
	 * Constructor for Turn.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skillTemplate SkillTemplate
	 */
	public Turn(EffectTemplate template, Character effector, Character effected, SkillTemplate skillTemplate)
	{
		super(template, effector, effected, skillTemplate);
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
		
		if ((effected != null) && (effector != null))
		{
			final int heding = Angles.calcHeading(effector.getX(), effector.getY(), effected.getX(), effected.getY());
			effected.setHeading(heding);
			PacketManager.showTurnCharacter(effected, heding, 1);
		}
		
		return false;
	}
}

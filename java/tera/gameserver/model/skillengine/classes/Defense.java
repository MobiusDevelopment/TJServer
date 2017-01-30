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

/**
 * @author Ronn
 */
public class Defense extends AbstractSkill
{
	/**
	 * Constructor for Defense.
	 * @param template SkillTemplate
	 */
	public Defense(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method checkCondition.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Skill#checkCondition(Character, float, float, float)
	 */
	@Override
	public boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ)
	{
		if (isToggle() && attacker.isDefenseStance())
		{
			return true;
		}
		
		return super.checkCondition(attacker, targetX, targetY, targetZ);
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
		super.endSkill(attacker, targetX, targetY, targetZ, force);
		attacker.setDefenseStance(false);
	}
	
	/**
	 * Method isWaitable.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Skill#isWaitable()
	 */
	@Override
	public boolean isWaitable()
	{
		return false;
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
		attacker.setDefenseStance(true);
	}
}

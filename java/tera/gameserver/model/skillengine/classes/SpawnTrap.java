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
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.traps.Trap;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class SpawnTrap extends AbstractSkill
{
	protected Skill trapSkill;
	
	/**
	 * Constructor for SpawnTrap.
	 * @param template SkillTemplate
	 */
	public SpawnTrap(SkillTemplate template)
	{
		super(template);
		final SkillTable skillTable = SkillTable.getInstance();
		final SkillTemplate temp = skillTable.getSkill(template.getClassId(), template.getId() + template.getOffsetId());
		
		if (temp != null)
		{
			setTrapSkill(temp.newInstance());
		}
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
		final Skill trapSkill = getTrapSkill();
		
		if (trapSkill == null)
		{
			return;
		}
		
		final int range = Math.max(getRange(), 20);
		Trap.newInstance(character, trapSkill, range, template.getLifeTime(), getRadius());
	}
	
	/**
	 * Method setTrapSkill.
	 * @param trapSkill Skill
	 */
	private void setTrapSkill(Skill trapSkill)
	{
		this.trapSkill = trapSkill;
	}
	
	/**
	 * Method getTrapSkill.
	 * @return Skill
	 */
	private Skill getTrapSkill()
	{
		return trapSkill;
	}
}
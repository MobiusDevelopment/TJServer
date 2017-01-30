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
package tera.gameserver.tasks;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;

import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public class SkillApplyTask extends SafeTask
{
	
	private final Skill skill;
	
	private final Character attacker;
	
	private final Character target;
	
	/**
	 * Constructor for SkillApplyTask.
	 * @param skill Skill
	 * @param attacker Character
	 * @param target Character
	 */
	public SkillApplyTask(Skill skill, Character attacker, Character target)
	{
		this.skill = skill;
		this.target = target;
		this.attacker = attacker;
	}
	
	@Override
	protected void runImpl()
	{
		skill.applySkill(attacker, target);
	}
}

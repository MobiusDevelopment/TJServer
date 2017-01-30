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
package tera.gameserver.model.skillengine.funcs.chance;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.Skill;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class UseSkill extends AbstractChanceFunc
{
	/**
	 * Constructor for UseSkill.
	 * @param vars VarTable
	 * @param cond Condition
	 */
	public UseSkill(VarTable vars, Condition cond)
	{
		super(vars, cond);
	}
	
	/**
	 * Method apply.
	 * @param attacker Character
	 * @param attacked Character
	 * @param eventSkill Skill
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.funcs.chance.ChanceFunc#apply(Character, Character, Skill)
	 */
	@Override
	public boolean apply(Character attacker, Character attacked, Skill eventSkill)
	{
		if ((skill != null) && super.apply(attacker, attacked, eventSkill))
		{
			skill.useSkill(attacker, attacked.getX(), attacked.getY(), attacked.getZ());
			return true;
		}
		
		return false;
	}
}
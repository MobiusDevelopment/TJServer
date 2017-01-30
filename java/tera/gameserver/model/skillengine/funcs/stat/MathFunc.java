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
package tera.gameserver.model.skillengine.funcs.stat;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.lambdas.Lambda;

/**
 * @author Ronn
 */
public class MathFunc extends AbstractStatFunc
{
	/**
	 * Constructor for MathFunc.
	 * @param stat StatType
	 * @param order int
	 * @param condition Condition
	 * @param lambda Lambda
	 */
	public MathFunc(StatType stat, int order, Condition condition, Lambda lambda)
	{
		super(stat, order, condition, lambda);
	}
	
	/**
	 * Method calc.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @param val float
	 * @return float
	 * @see tera.gameserver.model.skillengine.funcs.StatFunc#calc(Character, Character, Skill, float)
	 */
	@Override
	public float calc(Character attacker, Character attacked, Skill skill, float val)
	{
		if ((condition == null) || condition.test(attacker, attacked, skill, val))
		{
			return lambda.calc(val);
		}
		
		return val;
	}
}

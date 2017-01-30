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
package tera.gameserver.model.skillengine;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.funcs.StatFunc;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class Calculator
{
	
	private Array<StatFunc> funcs;
	
	public Calculator()
	{
		funcs = Arrays.toSortedArray(StatFunc.class, 1);
	}
	
	/**
	 * Method addFunc.
	 * @param func StatFunc
	 */
	public void addFunc(StatFunc func)
	{
		funcs.add(func);
	}
	
	/**
	 * Method calc.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @param value float
	 * @return float
	 */
	public float calc(Character attacker, Character attacked, Skill skill, float value)
	{
		final StatFunc[] array = funcs.array();
		
		for (int i = 0, length = funcs.size(); i < length; i++)
		{
			value = array[i].calc(attacker, attacked, skill, value);
		}
		
		return value;
	}
	
	/**
	 * Method calcToOrder.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @param value float
	 * @param order int
	 * @return float
	 */
	public float calcToOrder(Character attacker, Character attacked, Skill skill, float value, int order)
	{
		final StatFunc[] array = funcs.array();
		
		for (int i = 0, length = funcs.size(); i < length; i++)
		{
			final StatFunc func = array[i];
			
			if (func.getOrder() > order)
			{
				break;
			}
			
			value = func.calc(attacker, attacked, skill, value);
		}
		
		return value;
	}
	
	/**
	 * Method getFuncs.
	 * @return Array<StatFunc>
	 */
	public Array<StatFunc> getFuncs()
	{
		return funcs;
	}
	
	/**
	 * Method isEmpty.
	 * @return boolean
	 */
	public boolean isEmpty()
	{
		return funcs.isEmpty();
	}
	
	/**
	 * Method removeFunc.
	 * @param func StatFunc
	 */
	public void removeFunc(StatFunc func)
	{
		funcs.slowRemove(func);
	}
	
	/**
	 * Method setFuncs.
	 * @param funcs Array<StatFunc>
	 */
	public void setFuncs(Array<StatFunc> funcs)
	{
		this.funcs = funcs;
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	public int size()
	{
		return funcs.size();
	}
}

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

import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.skillengine.lambdas.FloatAdd;
import tera.gameserver.model.skillengine.lambdas.FloatDiv;
import tera.gameserver.model.skillengine.lambdas.FloatMul;
import tera.gameserver.model.skillengine.lambdas.FloatSet;
import tera.gameserver.model.skillengine.lambdas.FloatSub;

/**
 * @author Ronn
 */
public class FuncFactory
{
	private final static FuncFactory instance = new FuncFactory();
	
	/**
	 * Method getInstance.
	 * @return FuncFactory
	 */
	public static FuncFactory getInstance()
	{
		return instance;
	}
	
	/**
	 * Method createFunc.
	 * @param type String
	 * @param stat StatType
	 * @param order int
	 * @param cond Condition
	 * @param value String
	 * @return Func
	 */
	public Func createFunc(String type, StatType stat, int order, Condition cond, String value)
	{
		Func func = null;
		
		switch (type)
		{
			case "add":
				func = new MathFunc(stat, order, cond, new FloatAdd(Float.parseFloat(value)));
				break;
			
			case "sub":
				func = new MathFunc(stat, order, cond, new FloatSub(Float.parseFloat(value)));
				break;
			
			case "mul":
				func = new MathFunc(stat, order, cond, new FloatMul(Float.parseFloat(value)));
				break;
			
			case "div":
				func = new MathFunc(stat, order, cond, new FloatDiv(Float.parseFloat(value)));
				break;
			
			case "set":
				func = new MathFunc(stat, order, cond, new FloatSet(Float.parseFloat(value)));
				break;
		}
		
		return func;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}

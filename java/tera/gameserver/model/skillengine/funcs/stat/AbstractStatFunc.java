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
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.StatFunc;
import tera.gameserver.model.skillengine.lambdas.Lambda;

/**
 * @author Ronn
 */
public abstract class AbstractStatFunc implements StatFunc
{
	public static final StatFunc[] EMPTY_FUNC = new StatFunc[0];
	
	protected StatType stat;
	
	protected int order;
	
	protected Condition condition;
	
	protected Lambda lambda;
	
	/**
	 * Constructor for AbstractStatFunc.
	 * @param stat StatType
	 * @param order int
	 * @param condition Condition
	 * @param lambda Lambda
	 */
	public AbstractStatFunc(StatType stat, int order, Condition condition, Lambda lambda)
	{
		this.stat = stat;
		this.order = order;
		this.condition = condition;
		this.lambda = lambda;
	}
	
	/**
	 * Method addFuncTo.
	 * @param owner Character
	 * @see tera.gameserver.model.skillengine.funcs.Func#addFuncTo(Character)
	 */
	@Override
	public void addFuncTo(Character owner)
	{
		if (owner == null)
		{
			return;
		}
		
		owner.addStatFunc(this);
	}
	
	/**
	 * Method compareTo.
	 * @param func StatFunc
	 * @return int
	 */
	@Override
	public int compareTo(StatFunc func)
	{
		if (func == null)
		{
			return -1;
		}
		
		return order - func.getOrder();
	}
	
	/**
	 * Method getOrder.
	 * @return int
	 * @see tera.gameserver.model.skillengine.funcs.StatFunc#getOrder()
	 */
	@Override
	public int getOrder()
	{
		return order;
	}
	
	/**
	 * Method getStat.
	 * @return StatType
	 * @see tera.gameserver.model.skillengine.funcs.StatFunc#getStat()
	 */
	@Override
	public StatType getStat()
	{
		return stat;
	}
	
	/**
	 * Method removeFuncTo.
	 * @param owner Character
	 * @see tera.gameserver.model.skillengine.funcs.Func#removeFuncTo(Character)
	 */
	@Override
	public void removeFuncTo(Character owner)
	{
		if (owner == null)
		{
			return;
		}
		
		owner.removeStatFunc(this);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " stat = " + stat + ", order = " + order + ", condition = " + condition + ", lambda = " + lambda;
	}
}

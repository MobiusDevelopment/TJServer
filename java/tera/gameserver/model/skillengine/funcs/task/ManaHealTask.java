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
package tera.gameserver.model.skillengine.funcs.task;

import tera.gameserver.model.Character;

import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class ManaHealTask extends AbstractTaskFunc
{
	
	private final int interval;
	
	private final int power;
	
	/**
	 * Constructor for ManaHealTask.
	 * @param vars VarTable
	 */
	public ManaHealTask(VarTable vars)
	{
		super(vars);
		interval = vars.getInteger("interval");
		power = vars.getInteger("power");
	}
	
	@Override
	public void applyFunc()
	{
		final Array<Character> characters = getCharacters();
		characters.readLock();
		
		try
		{
			final Character[] array = characters.array();
			final int power = getPower();
			
			for (int i = 0, length = characters.size(); i < length; i++)
			{
				array[i].effectHealMp(power, array[i]);
			}
		}
		
		finally
		{
			characters.readUnlock();
		}
	}
	
	/**
	 * Method getInterval.
	 * @return int
	 * @see tera.gameserver.model.skillengine.funcs.task.TaskFunc#getInterval()
	 */
	@Override
	public int getInterval()
	{
		return interval;
	}
	
	/**
	 * Method getLimit.
	 * @return int
	 * @see tera.gameserver.model.skillengine.funcs.task.TaskFunc#getLimit()
	 */
	@Override
	public int getLimit()
	{
		return -2;
	}
	
	/**
	 * Method getPower.
	 * @return int
	 */
	public int getPower()
	{
		return power;
	}
}

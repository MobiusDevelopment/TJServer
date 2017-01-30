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

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.playable.Player;

import rlib.logging.Loggers;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class AbstractTaskFunc implements TaskFunc
{
	
	protected final Array<Character> characters;
	
	protected volatile ScheduledFuture<AbstractTaskFunc> schedule;
	
	protected volatile int currentCount;
	
	/**
	 * Constructor for AbstractTaskFunc.
	 * @param vars VarTable
	 */
	public AbstractTaskFunc(VarTable vars)
	{
		characters = Arrays.toConcurrentArray(Player.class);
	}
	
	/**
	 * Method addFuncTo.
	 * @param owner Character
	 * @see tera.gameserver.model.skillengine.funcs.Func#addFuncTo(Character)
	 */
	@Override
	public final void addFuncTo(Character owner)
	{
		final Array<Character> characters = getCharacters();
		characters.writeLock();
		
		try
		{
			characters.add(owner);
			currentCount = getLimit();
			final ExecutorManager executor = ExecutorManager.getInstance();
			
			if (schedule == null)
			{
				schedule = executor.scheduleAiAtFixedRate(this, getInterval(), getInterval());
			}
		}
		
		finally
		{
			characters.writeUnlock();
		}
	}
	
	public abstract void applyFunc();
	
	/**
	 * Method getCharacters.
	 * @return Array<Character>
	 */
	public final Array<Character> getCharacters()
	{
		return characters;
	}
	
	/**
	 * Method removeFuncTo.
	 * @param owner Character
	 * @see tera.gameserver.model.skillengine.funcs.Func#removeFuncTo(Character)
	 */
	@Override
	public final void removeFuncTo(Character owner)
	{
		final Array<Character> characters = getCharacters();
		characters.writeLock();
		
		try
		{
			characters.fastRemove(owner);
			
			if (characters.isEmpty() && (schedule != null))
			{
				schedule.cancel(true);
				schedule = null;
			}
		}
		
		finally
		{
			characters.writeUnlock();
		}
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run()
	{
		if (currentCount > -2)
		{
			if (currentCount < 1)
			{
				return;
			}
			
			currentCount--;
		}
		
		try
		{
			applyFunc();
		}
		catch (Exception e)
		{
			Loggers.warning(this, e);
		}
	}
}

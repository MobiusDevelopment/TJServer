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
package tera.gameserver.taskmanager;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class RegenTaskManager
{
	private static final Logger log = Loggers.getLogger(RegenTaskManager.class);
	
	private static RegenTaskManager instance;
	
	/**
	 * Method getInstance.
	 * @return RegenTaskManager
	 */
	public static RegenTaskManager getInstance()
	{
		if (instance == null)
		{
			instance = new RegenTaskManager();
		}
		
		return instance;
	}
	
	private final Table<Class<?>, Array<Character>> table;
	
	private RegenTaskManager()
	{
		table = Tables.newObjectTable();
		log.info("initializable.");
	}
	
	/**
	 * Method addCharacter.
	 * @param character Character
	 */
	public void addCharacter(Character character)
	{
		Array<Character> array = table.get(character.getClass());
		
		if (array == null)
		{
			synchronized (table)
			{
				array = table.get(character.getClass());
				
				if (array == null)
				{
					array = Arrays.toConcurrentArray(Character.class);
					final Array<Character> characters = array;
					final SafeTask task = new SafeTask()
					{
						@Override
						protected void runImpl()
						{
							characters.readLock();
							
							try
							{
								final Character[] array = characters.array();
								
								for (int i = 0, length = characters.size(); i < length; i++)
								{
									array[i].doRegen();
								}
							}
							
							finally
							{
								characters.readUnlock();
							}
						}
					};
					final ExecutorManager executor = ExecutorManager.getInstance();
					executor.scheduleGeneralAtFixedRate(task, 1000, 1000);
				}
			}
		}
		
		array.add(character);
	}
	
	/**
	 * Method removeCharacter.
	 * @param character Character
	 */
	public void removeCharacter(Character character)
	{
		final Array<Character> array = table.get(character.getClass());
		
		if (array != null)
		{
			array.fastRemove(character);
		}
	}
}

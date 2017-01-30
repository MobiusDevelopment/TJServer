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
import tera.gameserver.tasks.MoveNextTask;

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
public class MoveTaskManager
{
	private static final Logger log = Loggers.getLogger(MoveTaskManager.class);
	private static final int ARRAY_LIMIT = 100;
	private static MoveTaskManager instance;
	
	/**
	 * Method getInstance.
	 * @return MoveTaskManager
	 */
	public static MoveTaskManager getInstance()
	{
		if (instance == null)
		{
			instance = new MoveTaskManager();
		}
		
		return instance;
	}
	
	private final Table<Class<?>, Array<Array<MoveNextTask>>> taskTable;
	
	private MoveTaskManager()
	{
		taskTable = Tables.newObjectTable();
		log.info("initialized.");
	}
	
	/**
	 * Method addMoveTask.
	 * @param tasks Array<Array<MoveNextTask>>
	 * @param moveTask MoveNextTask
	 */
	public void addMoveTask(Array<Array<MoveNextTask>> tasks, MoveNextTask moveTask)
	{
		tasks.readLock();
		
		try
		{
			final Array<MoveNextTask>[] array = tasks.array();
			
			for (int i = 0, length = tasks.size(); i < length; i++)
			{
				final Array<MoveNextTask> container = array[i];
				
				if (container.size() < ARRAY_LIMIT)
				{
					addMoveTask(moveTask, container);
					return;
				}
			}
		}
		
		finally
		{
			tasks.readUnlock();
		}
		final Array<MoveNextTask> container = Arrays.toConcurrentArray(MoveNextTask.class);
		final SafeTask task = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				if (container.isEmpty())
				{
					return;
				}
				
				final long currentTime = System.currentTimeMillis();
				container.readLock();
				
				try
				{
					final MoveNextTask[] array = container.array();
					
					for (int i = 0, length = container.size(); i < length; i++)
					{
						array[i].update(currentTime);
					}
				}
				
				finally
				{
					container.readUnlock();
				}
			}
		};
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleMoveAtFixedRate(task, MoveNextTask.INTERVAL, MoveNextTask.INTERVAL);
		addMoveTask(moveTask, container);
		tasks.add(container);
	}
	
	/**
	 * Method addMoveTask.
	 * @param moveTask MoveNextTask
	 */
	public void addMoveTask(MoveNextTask moveTask)
	{
		final Character owner = moveTask.getOwner();
		final Table<Class<?>, Array<Array<MoveNextTask>>> taskTable = getTaskTable();
		Array<Array<MoveNextTask>> array = taskTable.get(owner.getClass());
		
		if (array == null)
		{
			synchronized (taskTable)
			{
				array = taskTable.get(owner.getClass());
				
				if (array == null)
				{
					array = Arrays.toConcurrentArray(Array.class);
					taskTable.put(owner.getClass(), array);
				}
			}
		}
		
		addMoveTask(array, moveTask);
	}
	
	/**
	 * Method addMoveTask.
	 * @param task MoveNextTask
	 * @param container Array<MoveNextTask>
	 */
	public void addMoveTask(MoveNextTask task, Array<MoveNextTask> container)
	{
		task.setContainer(container);
		container.add(task);
	}
	
	/**
	 * Method getTaskTable.
	 * @return Table<Class<?>,Array<Array<MoveNextTask>>>
	 */
	public Table<Class<?>, Array<Array<MoveNextTask>>> getTaskTable()
	{
		return taskTable;
	}
	
	/**
	 * Method removeTask.
	 * @param task MoveNextTask
	 */
	public void removeTask(MoveNextTask task)
	{
		final Array<MoveNextTask> container = task.getContainer();
		
		if (container != null)
		{
			container.fastRemove(task);
			task.setContainer(null);
		}
	}
}
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
package tera.gameserver.manager;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import rlib.concurrent.Locks;
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
public final class ServerVarManager
{
	private static final Logger log = Loggers.getLogger(ServerVarManager.class);
	private static ServerVarManager instance;
	
	/**
	 * Method getInstance.
	 * @return ServerVarManager
	 */
	public static ServerVarManager getInstance()
	{
		if (instance == null)
		{
			instance = new ServerVarManager();
		}
		
		return instance;
	}
	
	/**
	 * Method toString.
	 * @param value Object
	 * @return String
	 */
	private static String toString(Object value)
	{
		if (value == null)
		{
			return null;
		}
		
		return value.toString();
	}
	
	final Lock lock = Locks.newLock();
	private final Table<String, String> variables;
	private final Table<String, Object> cache;
	private final Array<String> varNames;
	private final ScheduledFuture<SafeTask> saveTask;
	
	private ServerVarManager()
	{
		variables = Tables.newObjectTable();
		cache = Tables.newObjectTable();
		varNames = Arrays.toArray(String.class);
		final DataBaseManager manager = DataBaseManager.getInstance();
		manager.loadServerVars(variables);
		final SafeTask task = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				lock.lock();
				
				try
				{
					updateVars();
				}
				
				finally
				{
					lock.unlock();
				}
			}
		};
		final ExecutorManager executor = ExecutorManager.getInstance();
		saveTask = executor.scheduleGeneralAtFixedRate(task, 300000, 300000);
		log.info("loaded " + variables.size() + " variables.");
	}
	
	public void finish()
	{
		lock.lock();
		
		try
		{
			if (saveTask != null)
			{
				saveTask.cancel(true);
			}
			
			updateVars();
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method getCache.
	 * @return Table<String,Object>
	 */
	public Table<String, Object> getCache()
	{
		return cache;
	}
	
	/**
	 * Method getString.
	 * @param name String
	 * @return String
	 */
	public String getString(String name)
	{
		if (name == null)
		{
			return null;
		}
		
		lock.lock();
		
		try
		{
			Object value = cache.get(name);
			
			if (value == null)
			{
				value = variables.get(name);
				
				if (value != null)
				{
					cache.put(name, value);
				}
			}
			
			return value == null ? null : (String) value;
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method getString.
	 * @param name String
	 * @param def String
	 * @return String
	 */
	public String getString(String name, String def)
	{
		if (name == null)
		{
			return null;
		}
		
		final String value = getString(name);
		return value == null ? def : value;
	}
	
	/**
	 * Method getVariables.
	 * @return Table<String,String>
	 */
	public Table<String, String> getVariables()
	{
		return variables;
	}
	
	/**
	 * Method getVarNames.
	 * @return Array<String>
	 */
	public Array<String> getVarNames()
	{
		return varNames;
	}
	
	/**
	 * Method remove.
	 * @param name String
	 */
	public void remove(String name)
	{
		if (name == null)
		{
			return;
		}
		
		lock.lock();
		
		try
		{
			final Object valCache = cache.remove(name);
			final String valVar = variables.remove(name);
			
			if ((valCache != null) || (valVar != null))
			{
				final DataBaseManager manager = DataBaseManager.getInstance();
				manager.removeServerVar(name);
			}
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method setString.
	 * @param name String
	 * @param value String
	 */
	public void setString(String name, String value)
	{
		if ((value == null) || (name == null))
		{
			return;
		}
		
		lock.lock();
		
		try
		{
			cache.put(name, value);
			
			if (!variables.containsKey(name))
			{
				variables.put(name, value);
				final DataBaseManager manager = DataBaseManager.getInstance();
				manager.insertServerVar(name, value);
			}
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	void updateVars()
	{
		final Table<String, String> variables = getVariables();
		final Table<String, Object> cache = getCache();
		final Array<String> varNames = getVarNames();
		final DataBaseManager manager = DataBaseManager.getInstance();
		variables.keyArray(varNames);
		final String[] array = varNames.array();
		
		for (int i = 0, length = varNames.size(); i < length; i++)
		{
			final String name = array[i];
			final String current = variables.get(name);
			final String last = toString(cache.get(name));
			
			if ((last == null) || current.equals(last))
			{
				continue;
			}
			
			manager.updateServerVar(name, last);
			variables.put(name, last);
		}
	}
}
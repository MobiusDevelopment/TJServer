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

import java.util.concurrent.locks.Lock;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.EffectList;
import tera.gameserver.model.skillengine.Effect;

import rlib.concurrent.Locks;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
@SuppressWarnings("unchecked")
public final class EffectTaskManager extends SafeTask
{
	private static final Logger log = Loggers.getLogger(EffectTaskManager.class);
	
	private static EffectTaskManager instance;
	
	/**
	 * Method getInstance.
	 * @return EffectTaskManager
	 */
	public static EffectTaskManager getInstance()
	{
		if (instance == null)
		{
			instance = new EffectTaskManager();
		}
		
		return instance;
	}
	
	private final Lock lock;
	
	private final Array<Effect>[] containers;
	
	private final FoldablePool<Array<Effect>> pool;
	
	private volatile int ordinal;
	
	private EffectTaskManager()
	{
		containers = new Array[178000];
		lock = Locks.newLock();
		pool = Pools.newFoldablePool(Array.class, 1000);
		ordinal = 0;
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleGeneralAtFixedRate(this, 1000, 1000);
		log.info("initialized.");
	}
	
	/**
	 * Method addTask.
	 * @param effect Effect
	 * @param interval int
	 */
	public final void addTask(Effect effect, int interval)
	{
		if (interval < 1)
		{
			interval = 1;
		}
		
		if (interval >= containers.length)
		{
			interval = containers.length - 1;
		}
		
		int cell = ordinal + interval;
		
		if (containers.length <= cell)
		{
			cell -= containers.length;
		}
		
		Array<Effect> container = null;
		lock.lock();
		
		try
		{
			container = containers[cell];
			
			if (container == null)
			{
				if (pool.isEmpty())
				{
					container = Arrays.toArray(Effect.class);
				}
				else
				{
					container = pool.take();
				}
				
				containers[cell] = container;
			}
			
			container.add(effect);
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	@Override
	protected void runImpl()
	{
		Array<Effect> container;
		lock.lock();
		
		try
		{
			container = containers[ordinal];
			containers[ordinal] = null;
		}
		
		finally
		{
			lock.unlock();
		}
		
		if (container == null)
		{
			ordinal += 1;
			
			if (ordinal >= containers.length)
			{
				ordinal = 0;
			}
			
			return;
		}
		
		if (!container.isEmpty())
		{
			final Effect[] array = container.array();
			
			for (int i = 0, length = container.size(); i < length; i++)
			{
				final Effect effect = array[i];
				
				if (effect == null)
				{
					log.warning(new Exception("not found effect"));
					continue;
				}
				
				if (effect.isFinished())
				{
					effect.fold();
					continue;
				}
				
				final EffectList effectList = effect.getEffectList();
				
				if (effectList == null)
				{
					log.warning("not found effect list to " + effect);
					continue;
				}
				
				effectList.lock();
				
				try
				{
					effect.scheduleEffect();
				}
				
				finally
				{
					effectList.unlock();
				}
				
				if (effect.isFinished())
				{
					effect.fold();
					continue;
				}
				
				addTask(effect, effect.getPeriod());
			}
		}
		
		ordinal += 1;
		
		if (ordinal >= containers.length)
		{
			ordinal = 0;
		}
		
		container.clear();
		pool.put(container);
	}
}

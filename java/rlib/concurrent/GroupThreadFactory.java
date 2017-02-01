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
package rlib.concurrent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadFactory;

import rlib.logging.Loggers;

public class GroupThreadFactory implements ThreadFactory
{
	private final int priority;
	private int ordinal;
	private final String name;
	private final ThreadGroup group;
	private Constructor<? extends Thread> constructor;
	
	public GroupThreadFactory(String name, Class<? extends Thread> cs, int priority)
	{
		this.priority = priority;
		this.name = name;
		try
		{
			constructor = cs.getConstructor(ThreadGroup.class, Runnable.class, String.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Loggers.warning("GroupThreadFactory", e);
		}
		group = new ThreadGroup(name);
	}
	
	@Override
	public Thread newThread(Runnable runnable)
	{
		Thread thread = null;
		try
		{
			Object[] arrobject = new Object[]
			{
				group,
				runnable,
				String.valueOf(name) + "-" + ordinal++
			};
			thread = constructor.newInstance(arrobject);
		}
		catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e)
		{
			Loggers.warning("GroupThreadFactory", e);
		}
		thread.setPriority(priority);
		return thread;
	}
}

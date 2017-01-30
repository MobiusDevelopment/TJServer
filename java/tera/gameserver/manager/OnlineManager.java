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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledFuture;

import tera.Config;
import tera.gameserver.model.World;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public final class OnlineManager
{
	private static final Logger log = Loggers.getLogger(OnlineManager.class);
	private static OnlineManager instance;
	
	/**
	 * Method getInstance.
	 * @return OnlineManager
	 */
	public static OnlineManager getInstance()
	{
		if (instance == null)
		{
			instance = new OnlineManager();
		}
		
		return instance;
	}
	
	File file;
	private ScheduledFuture<SafeTask> schedule;
	volatile int currentOnline;
	
	private OnlineManager()
	{
		if (!Config.SERVER_ONLINE_FILE.isEmpty())
		{
			file = new File(Config.SERVER_ONLINE_FILE);
		}
		
		final SafeTask task = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				currentOnline = (int) (World.online() * Config.SERVER_ONLINE_FAKE);
				
				if ((file != null) && file.canWrite())
				{
					try (PrintWriter writer = new PrintWriter(file))
					{
						writer.print(currentOnline);
					}
					catch (FileNotFoundException e)
					{
						Loggers.warning(OnlineManager.class, e);
					}
				}
			}
		};
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneralAtFixedRate(task, 60000, 60000);
		log.info("initialized.");
	}
	
	/**
	 * Method getCurrentOnline.
	 * @return int
	 */
	public int getCurrentOnline()
	{
		return currentOnline;
	}
	
	public synchronized void stop()
	{
		if (schedule != null)
		{
			schedule.cancel(false);
			schedule = null;
		}
		
		if ((file != null) && file.canWrite())
		{
			try (PrintWriter writer = new PrintWriter(file))
			{
				writer.print(0);
			}
			catch (FileNotFoundException e)
			{
				Loggers.warning(OnlineManager.class, e);
			}
		}
	}
}
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
package tera.gameserver.tasks;

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.OnlineManager;
import tera.gameserver.manager.ServerVarManager;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.tables.SpawnTable;

import rlib.logging.GameLoggers;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;

/**
 * @author Ronn
 * @created 25.04.2012
 */
public class ShutdownTask extends SafeTask
{
	private static final Logger log = Loggers.getLogger(ShutdownTask.class);
	
	private boolean restart;
	
	private long delay;
	
	private volatile ScheduledFuture<ShutdownTask> schedule;
	
	/**
	 * Method cancel.
	 * @return boolean
	 */
	public synchronized boolean cancel()
	{
		if ((delay > 60000) && (schedule != null))
		{
			schedule.cancel(true);
			schedule = null;
			log.info("cancel shutdown task.");
			return true;
		}
		
		return schedule == null;
	}
	
	/**
	 * Method getSchedule.
	 * @return ScheduledFuture<ShutdownTask>
	 */
	public final ScheduledFuture<ShutdownTask> getSchedule()
	{
		return schedule;
	}
	
	/**
	 * Method isComplete.
	 * @return boolean
	 */
	public boolean isComplete()
	{
		return schedule == null;
	}
	
	/**
	 * Method isRunning.
	 * @return boolean
	 */
	public boolean isRunning()
	{
		return schedule != null;
	}
	
	/**
	 * Method next.
	 * @param restart boolean
	 * @param delay long
	 */
	public void next(boolean restart, long delay)
	{
		if (cancel())
		{
			this.restart = restart;
			this.delay = delay;
			final ExecutorManager executor = ExecutorManager.getInstance();
			schedule = executor.scheduleGeneralAtFixedRate(this, 60000, 60000);
			
			if (restart)
			{
				World.sendAnnounce("The server will restart in " + (delay / 1000 / 60) + " minutes.");
			}
			else
			{
				World.sendAnnounce("The server will restart in " + (delay / 1000 / 60) + " minutes.");
			}
			
			log.info("will shutdown in " + (delay / 1000 / 60) + " minutes.");
		}
	}
	
	@Override
	protected synchronized void runImpl()
	{
		delay -= 60000;
		
		if (delay > 60000)
		{
			if (restart)
			{
				World.sendAnnounce("The server will restart in " + (delay / 1000 / 60) + " minutes.");
			}
			else
			{
				World.sendAnnounce("The server will restart in " + (delay / 1000 / 60) + " minutes.");
			}
		}
		else
		{
			World.sendAnnounce("The server will shutdown in 1 minute.");
			final SpawnTable spawnTable = SpawnTable.getInstance();
			spawnTable.stopSpawns();
		}
		
		log.info("will shutdown in " + (delay / 1000 / 60) + " minutes.");
		
		if (delay < 1)
		{
			log.info("start save players...");
			
			for (Player player : World.getPlayers())
			{
				log.info("store " + player.getName());
				player.store(false);
			}
			
			log.info("all players saved.");
			GameLoggers.finish();
			final ServerVarManager varManager = ServerVarManager.getInstance();
			varManager.finish();
			/**
			 * Method setSchedule.
			 * @param schedule ScheduledFuture<ShutdownTask>
			 */
			log.info("all game loggers writed.");
			final OnlineManager onlineManager = OnlineManager.getInstance();
			onlineManager.stop();
			System.exit(restart ? 2 : 0);
		}
	}
	
	public final void setSchedule(ScheduledFuture<ShutdownTask> schedule)
	{
		this.schedule = schedule;
	}
}

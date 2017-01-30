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
import tera.gameserver.model.World;

import rlib.util.SafeTask;

/**
 * @author Ronn
 * @created 29.03.2012
 */
public class AnnounceTask extends SafeTask
{
	private final String text;
	private final int interval;
	private final ScheduledFuture<AnnounceTask> schedule;
	
	/**
	 * Constructor for AnnounceTask.
	 * @param text String
	 * @param interval int
	 */
	public AnnounceTask(String text, int interval)
	{
		this.text = text;
		this.interval = interval;
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneralAtFixedRate(this, interval, interval);
	}
	
	public synchronized void cancel()
	{
		if (schedule != null)
		{
			schedule.cancel(true);
		}
	}
	
	/**
	 * Method getInterval.
	 * @return int
	 */
	public final int getInterval()
	{
		return interval;
	}
	
	/**
	 * Method getText.
	 * @return String
	 */
	public final String getText()
	{
		return text;
	}
	
	@Override
	protected void runImpl()
	{
		World.sendAnnounce(text);
	}
}
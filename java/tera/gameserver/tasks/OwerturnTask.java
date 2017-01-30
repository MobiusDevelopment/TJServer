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
import tera.gameserver.model.Character;

import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public class OwerturnTask extends SafeTask
{
	
	private final Character character;
	
	private volatile ScheduledFuture<OwerturnTask> schedule;
	
	/**
	 * Constructor for OwerturnTask.
	 * @param character Character
	 */
	public OwerturnTask(Character character)
	{
		this.character = character;
	}
	
	/**
	 * Method nextOwerturn.
	 * @param time int
	 */
	public synchronized void nextOwerturn(int time)
	{
		if (schedule != null)
		{
			schedule.cancel(true);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, time);
	}
	
	@Override
	protected synchronized void runImpl()
	{
		character.cancelOwerturn();
		schedule = null;
	}
}

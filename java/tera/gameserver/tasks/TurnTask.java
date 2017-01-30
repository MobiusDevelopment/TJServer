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
import java.util.concurrent.TimeUnit;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.Character;
import tera.gameserver.templates.CharTemplate;

import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public class TurnTask extends SafeTask
{
	private static final int HALF = Short.MAX_VALUE;
	
	private final Character actor;
	
	private final int turnSpeed;
	
	private volatile ScheduledFuture<TurnTask> schedule;
	
	private int startHeading;
	
	private int endHeading;
	
	private int time;
	
	private int diff;
	
	/**
	 * Constructor for TurnTask.
	 * @param actor Character
	 */
	public TurnTask(Character actor)
	{
		this.actor = actor;
		final CharTemplate template = actor.getTemplate();
		turnSpeed = template.getTurnSpeed() * 4;
	}
	
	public void cancel()
	{
		ScheduledFuture<TurnTask> schedule = getSchedule();
		
		if (schedule != null)
		{
			synchronized (this)
			{
				schedule = getSchedule();
				
				if (schedule != null)
				{
					final int res = (int) schedule.getDelay(TimeUnit.MILLISECONDS);
					schedule.cancel(false);
					setSchedule(null);
					final int time = getTime();
					final float done = ((time - res) * 1F) / time;
					int result = (int) (getDiff() * done);
					result = isPositive() ? getStartHeading() + result : getStartHeading() - result;
					actor.setHeading(result);
				}
			}
		}
	}
	
	/**
	 * Method isPositive.
	 * @return boolean
	 */
	public boolean isPositive()
	{
		return startHeading < endHeading;
	}
	
	/**
	 * Method getDiff.
	 * @return int
	 */
	public int getDiff()
	{
		return diff;
	}
	
	/**
	 * Method getStartHeading.
	 * @return int
	 */
	public int getStartHeading()
	{
		return startHeading;
	}
	
	/**
	 * Method getTime.
	 * @return int
	 */
	public int getTime()
	{
		return time;
	}
	
	/**
	 * Method setDiff.
	 * @param diff int
	 */
	public void setDiff(int diff)
	{
		this.diff = diff;
	}
	
	/**
	 * Method getSchedule.
	 * @return ScheduledFuture<TurnTask>
	 */
	public ScheduledFuture<TurnTask> getSchedule()
	{
		return schedule;
	}
	
	/**
	 * Method setSchedule.
	 * @param schedule ScheduledFuture<TurnTask>
	 */
	public void setSchedule(ScheduledFuture<TurnTask> schedule)
	{
		this.schedule = schedule;
	}
	
	/**
	 * Method getEndHeading.
	 * @return int
	 */
	public final int getEndHeading()
	{
		if (endHeading > 65536)
		{
			endHeading -= 65536;
		}
		
		return endHeading;
	}
	
	/**
	 * Method isTurner.
	 * @return boolean
	 */
	public boolean isTurner()
	{
		return schedule != null;
	}
	
	/**
	 * Method getActor.
	 * @return Character
	 */
	public Character getActor()
	{
		return actor;
	}
	
	/**
	 * Method nextTurn.
	 * @param newHeading int
	 */
	public void nextTurn(int newHeading)
	{
		cancel();
		final Character actor = getActor();
		int startHeading = actor.getHeading();
		int endHeading = newHeading;
		synchronized (this)
		{
			int diff = Math.abs(startHeading - endHeading);
			
			if (diff > HALF)
			{
				if (startHeading < endHeading)
				{
					startHeading += 65536;
				}
				else
				{
					endHeading += 65536;
				}
				
				diff = Math.abs(startHeading - endHeading);
			}
			
			setDiff(diff);
			diff *= 1000;
			final int time = diff / turnSpeed;
			final ExecutorManager executor = ExecutorManager.getInstance();
			setSchedule(executor.scheduleGeneral(this, time + 700));
			setTime(time);
			PacketManager.showTurnCharacter(actor, endHeading, (time * 80) / 100);
		}
		setStartHeading(startHeading);
		setEndHeading(endHeading);
	}
	
	/**
	 * Method setTime.
	 * @param time int
	 */
	public void setTime(int time)
	{
		this.time = time;
	}
	
	/**
	 * Method setStartHeading.
	 * @param startHeading int
	 */
	public void setStartHeading(int startHeading)
	{
		this.startHeading = startHeading;
	}
	
	/**
	 * Method setEndHeading.
	 * @param endHeading int
	 */
	public void setEndHeading(int endHeading)
	{
		this.endHeading = endHeading;
	}
	
	@Override
	protected void runImpl()
	{
		actor.setHeading(endHeading);
		setSchedule(null);
	}
}

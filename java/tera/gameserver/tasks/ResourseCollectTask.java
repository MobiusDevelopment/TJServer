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
import tera.gameserver.model.EmotionType;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.network.serverpackets.Emotion;
import tera.gameserver.network.serverpackets.ResourseCollectProgress;
import tera.gameserver.network.serverpackets.ResourseEndCollect;
import tera.gameserver.network.serverpackets.ResourseStartCollect;

import rlib.util.Rnd;
import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public final class ResourseCollectTask extends SafeTask
{
	
	private final Player collector;
	
	private ResourseInstance resourse;
	
	private int chance;
	
	private int counter;
	
	private volatile ScheduledFuture<ResourseCollectTask> schedule;
	
	/**
	 * Constructor for ResourseCollectTask.
	 * @param collector Player
	 */
	public ResourseCollectTask(Player collector)
	{
		this.collector = collector;
	}
	
	/**
	 * Method cancel.
	 * @param force boolean
	 */
	public synchronized void cancel(boolean force)
	{
		final ResourseInstance resourse = getResourse();
		synchronized (this)
		{
			if (schedule != null)
			{
				schedule.cancel(false);
				schedule = null;
			}
			
			if (resourse != null)
			{
				setResourse(null);
			}
		}
		
		if (resourse != null)
		{
			if (force)
			{
				collector.broadcastPacket(ResourseEndCollect.getInstance(collector, resourse, ResourseEndCollect.INTERRUPTED));
			}
			else
			{
				collector.broadcastPacket(ResourseEndCollect.getInstance(collector, resourse, ResourseEndCollect.FAILED));
			}
			
			resourse.onCollected(collector, true);
		}
	}
	
	/**
	 * Method getResourse.
	 * @return ResourseInstance
	 */
	protected final ResourseInstance getResourse()
	{
		return resourse;
	}
	
	/**
	 * Method isRunning.
	 * @return boolean
	 */
	public boolean isRunning()
	{
		return (schedule != null) && !schedule.isDone();
	}
	
	/**
	 * Method nextTask.
	 * @param resourse ResourseInstance
	 */
	public void nextTask(ResourseInstance resourse)
	{
		cancel(true);
		synchronized (this)
		{
			counter = 3;
			this.resourse = resourse;
			chance = resourse.getChanceFor(collector);
			final ExecutorManager executor = ExecutorManager.getInstance();
			schedule = executor.scheduleGeneralAtFixedRate(this, 1000, 1000);
		}
		collector.broadcastPacket(ResourseStartCollect.getInstance(collector, resourse));
	}
	
	@Override
	protected void runImpl()
	{
		collector.sendPacket(ResourseCollectProgress.getInstance((100 - (25 * counter))), true);
		ResourseInstance resourse = null;
		boolean cancel = false;
		synchronized (this)
		{
			if (counter > 0)
			{
				if (Rnd.chance(chance))
				{
					counter -= 1;
					return;
				}
				
				cancel = true;
			}
			else
			{
				if (schedule != null)
				{
					schedule.cancel(false);
					schedule = null;
				}
				
				resourse = getResourse();
				setResourse(null);
			}
		}
		
		if (cancel)
		{
			cancel(false);
			collector.broadcastPacket(Emotion.getInstance(collector, EmotionType.FAIL));
		}
		else if (resourse != null)
		{
			collector.broadcastPacket(ResourseEndCollect.getInstance(collector, resourse, ResourseEndCollect.SUCCESSFUL));
			resourse.onCollected(collector, false);
			collector.broadcastPacket(Emotion.getInstance(collector, EmotionType.BOASTING));
		}
	}
	
	/**
	 * Method setResourse.
	 * @param resourse ResourseInstance
	 */
	protected final void setResourse(ResourseInstance resourse)
	{
		this.resourse = resourse;
	}
}

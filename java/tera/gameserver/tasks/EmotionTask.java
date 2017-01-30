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
import tera.gameserver.model.EmotionType;

import rlib.util.Rnd;
import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public class EmotionTask extends SafeTask
{
	
	public static final EmotionType[] MONSTER_TYPES =
	{
		EmotionType.INSPECTION,
		EmotionType.FAST_INSPECTION,
	};
	
	public static final EmotionType[] NPC_TYPES =
	{
		EmotionType.INSPECTION,
		EmotionType.FAST_INSPECTION,
		EmotionType.CAST,
		EmotionType.MUSE,
		EmotionType.KNEAD_FISTS,
	};
	
	public static final EmotionType[] PLAYER_TYPES =
	{
		EmotionType.INSPECTION,
		EmotionType.FAST_INSPECTION,
		EmotionType.BUMPING,
		EmotionType.MUSE,
		EmotionType.KNEAD_FISTS,
	};
	
	private final Character actor;
	
	private final EmotionType[] types;
	
	private final int max;
	
	private volatile ScheduledFuture<EmotionTask> schedule;
	
	/**
	 * Constructor for EmotionTask.
	 * @param actor Character
	 * @param types EmotionType[]
	 */
	public EmotionTask(Character actor, EmotionType[] types)
	{
		this.actor = actor;
		this.types = types;
		max = types.length - 1;
	}
	
	/**
	 * Method getSchedule.
	 * @return ScheduledFuture<EmotionTask>
	 */
	public final ScheduledFuture<EmotionTask> getSchedule()
	{
		return schedule;
	}
	
	@Override
	protected void runImpl()
	{
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, Rnd.nextInt(30000, 120000));
		actor.getAI().startEmotion(types[Rnd.nextInt(0, max)]);
	}
	
	/**
	 * Method setSchedule.
	 * @param schedule ScheduledFuture<EmotionTask>
	 */
	public final void setSchedule(ScheduledFuture<EmotionTask> schedule)
	{
		this.schedule = schedule;
	}
	
	public synchronized void start()
	{
		if (schedule != null)
		{
			return;
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, Rnd.nextInt(30000, 120000));
	}
	
	public synchronized void stop()
	{
		final ScheduledFuture<EmotionTask> schedule = getSchedule();
		
		if (schedule != null)
		{
			schedule.cancel(false);
			setSchedule(null);
		}
	}
}

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

import tera.Config;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.network.serverpackets.PlayerBattleStance;

import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public class BattleStanceTask extends SafeTask
{
	
	private final Character character;
	
	private long last;
	
	private volatile ScheduledFuture<BattleStanceTask> schedule;
	
	/**
	 * Constructor for BattleStanceTask.
	 * @param character Character
	 */
	public BattleStanceTask(Character character)
	{
		this.character = character;
	}
	
	/**
	 * Method getLast.
	 * @return long
	 */
	public long getLast()
	{
		return last;
	}
	
	/**
	 * Method getSchedule.
	 * @return ScheduledFuture<BattleStanceTask>
	 */
	public ScheduledFuture<BattleStanceTask> getSchedule()
	{
		return schedule;
	}
	
	public synchronized void now()
	{
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, Config.WORLD_PLAYER_TIME_BATTLE_STANCE);
		character.broadcastPacket(PlayerBattleStance.getInstance(character, PlayerBattleStance.STANCE_ON));
		setLast(System.currentTimeMillis());
	}
	
	@Override
	protected synchronized void runImpl()
	{
		character.stopBattleStance();
	}
	
	/**
	 * Method setLast.
	 * @param last long
	 */
	public void setLast(long last)
	{
		this.last = last;
	}
	
	/**
	 * Method setSchedule.
	 * @param schedule ScheduledFuture<BattleStanceTask>
	 */
	public void setSchedule(ScheduledFuture<BattleStanceTask> schedule)
	{
		this.schedule = schedule;
	}
	
	public synchronized void stop()
	{
		final ScheduledFuture<BattleStanceTask> schedule = getSchedule();
		
		if (schedule != null)
		{
			schedule.cancel(false);
			setSchedule(null);
		}
		
		character.broadcastPacket(PlayerBattleStance.getInstance(character, PlayerBattleStance.STANCE_OFF));
	}
	
	public synchronized void update()
	{
		if ((System.currentTimeMillis() - getLast()) < 2000L)
		{
			return;
		}
		
		final ScheduledFuture<BattleStanceTask> schedule = getSchedule();
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		setSchedule(executor.scheduleGeneral(this, Config.WORLD_PLAYER_TIME_BATTLE_STANCE));
		setLast(System.currentTimeMillis());
	}
}

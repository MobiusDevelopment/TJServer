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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import tera.Config;
import tera.gameserver.ServerThread;

import rlib.concurrent.GroupThreadFactory;
import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 * @created 25.03.2012
 */
public final class ExecutorManager
{
	private static final Logger log = Loggers.getLogger(ExecutorManager.class);
	private static ExecutorManager instance;
	
	/**
	 * Method getInstance.
	 * @return ExecutorManager
	 */
	public static ExecutorManager getInstance()
	{
		if (instance == null)
		{
			instance = new ExecutorManager();
		}
		
		return instance;
	}
	
	private final ScheduledExecutorService generalExecutor;
	private final ScheduledExecutorService moveExecutor;
	private final ScheduledExecutorService aiExecutor;
	private final ScheduledExecutorService skillUseExecutor;
	private final ScheduledExecutorService skillCastExecutor;
	private final ScheduledExecutorService skillMoveExecutor;
	private final ScheduledExecutorService idFactoryExecutor;
	private final ExecutorService synchPacketExecutor;
	private final ExecutorService asynchPacketExecutor;
	private final ExecutorService serverPacketExecutor;
	private final ExecutorService executor;
	
	private ExecutorManager()
	{
		generalExecutor = Executors.newScheduledThreadPool(Config.THREAD_POOL_SIZE_GENERAL, new GroupThreadFactory("GeneralThreadExecutor", ServerThread.class, Thread.NORM_PRIORITY + 1));
		moveExecutor = Executors.newScheduledThreadPool(Config.THREAD_POOL_SIZE_MOVE, new GroupThreadFactory("MoveThreadExecutor", ServerThread.class, Thread.NORM_PRIORITY + 3));
		aiExecutor = Executors.newScheduledThreadPool(Config.THREAD_POOL_SIZE_AI, new GroupThreadFactory("AIThreadExecutor", ServerThread.class, Thread.MIN_PRIORITY));
		skillUseExecutor = Executors.newScheduledThreadPool(Config.THREAD_POOL_SIZE_SKILL_USE, new GroupThreadFactory("SkillUseExecutor", ServerThread.class, Thread.NORM_PRIORITY));
		skillCastExecutor = Executors.newScheduledThreadPool(Config.THREAD_POOL_SIZE_SKILL_CAST, new GroupThreadFactory("SkillCastExecutor", ServerThread.class, Thread.NORM_PRIORITY));
		skillMoveExecutor = Executors.newScheduledThreadPool(Config.THREAD_POOL_SIZE_SKILL_MOVE, new GroupThreadFactory("SkillMoveExecutor", ServerThread.class, Thread.NORM_PRIORITY + 3));
		idFactoryExecutor = Executors.newSingleThreadScheduledExecutor();
		synchPacketExecutor = Executors.newSingleThreadExecutor(new GroupThreadFactory("SynchPacketExecutor", ServerThread.class, Thread.MAX_PRIORITY));
		asynchPacketExecutor = Executors.newFixedThreadPool(Config.THREAD_POOL_PACKET_RUNNER, new GroupThreadFactory("AsynchPacketExecutor", ServerThread.class, Thread.NORM_PRIORITY));
		serverPacketExecutor = Executors.newSingleThreadExecutor(new GroupThreadFactory("ServerPacketExecutor", ServerThread.class, Thread.MAX_PRIORITY));
		executor = Executors.newFixedThreadPool(2, new GroupThreadFactory("AsynThreadExecutor", ServerThread.class, Thread.MAX_PRIORITY));
		log.info("initialized.");
	}
	
	/**
	 * Method execute.
	 * @param runnable Runnable
	 */
	public void execute(Runnable runnable)
	{
		executor.execute(runnable);
	}
	
	/**
	 * Method getIdFactoryExecutor.
	 * @return ScheduledExecutorService
	 */
	public ScheduledExecutorService getIdFactoryExecutor()
	{
		return idFactoryExecutor;
	}
	
	/**
	 * Method runAsynchPacket.
	 * @param packet Runnable
	 */
	public void runAsynchPacket(Runnable packet)
	{
		asynchPacketExecutor.execute(packet);
	}
	
	/**
	 * Method runServerPacket.
	 * @param packet Runnable
	 */
	public void runServerPacket(Runnable packet)
	{
		serverPacketExecutor.execute(packet);
	}
	
	/**
	 * Method runSynchPacket.
	 * @param packet Runnable
	 */
	public void runSynchPacket(Runnable packet)
	{
		synchPacketExecutor.execute(packet);
	}
	
	/**
	 * Method scheduleAiAtFixedRate.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @param interval long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleAiAtFixedRate(T runnable, long delay, long interval)
	{
		try
		{
			if (interval < 0)
			{
				interval = 0;
			}
			
			if (delay < 0)
			{
				delay = 0;
			}
			
			return (ScheduledFuture<T>) aiExecutor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleGeneral.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleGeneral(T runnable, long delay)
	{
		try
		{
			if (delay < 0)
			{
				delay = 0;
			}
			
			return (ScheduledFuture<T>) generalExecutor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleGeneralAtFixedRate.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @param interval long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleGeneralAtFixedRate(T runnable, long delay, long interval)
	{
		try
		{
			if (interval <= 0)
			{
				interval = 1;
			}
			
			return (ScheduledFuture<T>) generalExecutor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleMove.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleMove(T runnable, long delay)
	{
		try
		{
			if (delay < 1)
			{
				delay = 1;
			}
			
			return (ScheduledFuture<T>) moveExecutor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleMoveAtFixedRate.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @param interval long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleMoveAtFixedRate(T runnable, long delay, long interval)
	{
		try
		{
			if (delay < 1)
			{
				delay = 1;
			}
			
			return (ScheduledFuture<T>) moveExecutor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleSkillCast.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleSkillCast(T runnable, long delay)
	{
		try
		{
			if (delay < 0)
			{
				delay = 0;
			}
			
			return (ScheduledFuture<T>) skillCastExecutor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleSkillCastAtFixedRate.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @param interval long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleSkillCastAtFixedRate(T runnable, long delay, long interval)
	{
		try
		{
			if (interval < 0)
			{
				interval = 0;
			}
			
			if (delay < 0)
			{
				delay = 0;
			}
			
			return (ScheduledFuture<T>) skillCastExecutor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleSkillMove.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @param isPlayer boolean
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleSkillMove(T runnable, long delay, boolean isPlayer)
	{
		try
		{
			if (delay < 0)
			{
				delay = 0;
			}
			
			return (ScheduledFuture<T>) skillMoveExecutor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleSkillMoveAtFixedRate.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @param interval long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleSkillMoveAtFixedRate(T runnable, long delay, long interval)
	{
		try
		{
			if (interval < 0)
			{
				interval = 0;
			}
			
			if (delay < 0)
			{
				delay = 0;
			}
			
			return (ScheduledFuture<T>) skillMoveExecutor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleSkillUse.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleSkillUse(T runnable, long delay)
	{
		try
		{
			if (delay < 0)
			{
				delay = 0;
			}
			
			return (ScheduledFuture<T>) skillUseExecutor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
	
	/**
	 * Method scheduleSkillUseAtFixedRate.
	 * @param <T>
	 * @param runnable T
	 * @param delay long
	 * @param interval long
	 * @return ScheduledFuture<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleSkillUseAtFixedRate(T runnable, long delay, long interval)
	{
		try
		{
			if (interval < 0)
			{
				interval = 0;
			}
			
			if (delay < 0)
			{
				delay = 1;
			}
			
			return (ScheduledFuture<T>) skillUseExecutor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			log.warning(e);
		}
		
		return null;
	}
}
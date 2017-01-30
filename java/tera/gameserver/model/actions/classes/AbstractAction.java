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
package tera.gameserver.model.actions.classes;

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.IdFactory;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.actions.Action;
import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.playable.Player;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.pools.FoldablePool;

/**
 * @author Ronn
 * @created 06.03.2012
 * @param <T>
 */
public abstract class AbstractAction<T> extends SafeTask implements Action
{
	protected static final Logger log = Loggers.getLogger(AbstractAction.class);
	protected Player actor;
	protected T target;
	protected ScheduledFuture<?> schedule;
	protected int objectId;
	
	public AbstractAction()
	{
		final IdFactory idFactory = IdFactory.getInstance();
		this.objectId = idFactory.getNextActionId();
	}
	
	/**
	 * Method assent.
	 * @param player Player
	 * @see tera.gameserver.model.actions.Action#assent(Player)
	 */
	@Override
	public synchronized void assent(Player player)
	{
		final ScheduledFuture<?> schedule = getSchedule();
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		clear();
	}
	
	/**
	 * Method cancel.
	 * @param player Player
	 * @see tera.gameserver.model.actions.Action#cancel(Player)
	 */
	@Override
	public synchronized void cancel(Player player)
	{
		final ScheduledFuture<?> schedule = getSchedule();
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		clear();
	}
	
	protected synchronized void clear()
	{
		final Player actor = getActor();
		
		if (actor != null)
		{
			actor.setLastAction(null);
		}
		
		getPool().put(this);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		actor = null;
		target = null;
		schedule = null;
	}
	
	/**
	 * Method getActor.
	 * @return Player
	 * @see tera.gameserver.model.actions.Action#getActor()
	 */
	@Override
	public final Player getActor()
	{
		return actor;
	}
	
	/**
	 * Method getId.
	 * @return int
	 * @see tera.gameserver.model.actions.Action#getId()
	 */
	@Override
	public final int getId()
	{
		return getType().ordinal();
	}
	
	/**
	 * Method getObjectId.
	 * @return int
	 * @see tera.gameserver.model.actions.Action#getObjectId()
	 */
	@Override
	public final int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Method getPool.
	 * @return FoldablePool<Action>
	 */
	protected final FoldablePool<Action> getPool()
	{
		return getType().getPool();
	}
	
	/**
	 * Method getSchedule.
	 * @return ScheduledFuture<?>
	 */
	protected final ScheduledFuture<?> getSchedule()
	{
		return schedule;
	}
	
	/**
	 * Method getTarget.
	 * @return T
	 * @see tera.gameserver.model.actions.Action#getTarget()
	 */
	@Override
	public final T getTarget()
	{
		return target;
	}
	
	/**
	 * Method getType.
	 * @return ActionType
	 * @see tera.gameserver.model.actions.Action#getType()
	 */
	@Override
	public abstract ActionType getType();
	
	/**
	 * Method invite.
	 * @see tera.gameserver.model.actions.Action#invite()
	 */
	@Override
	public synchronized void invite()
	{
		final Player actor = getActor();
		
		if (actor == null)
		{
			return;
		}
		
		actor.setLastAction(this);
		final ExecutorManager executor = ExecutorManager.getInstance();
		setSchedule(executor.scheduleGeneral(this, 20000));
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	@Override
	protected void runImpl()
	{
		cancel(actor);
	}
	
	/**
	 * Method setActor.
	 * @param actor Player
	 * @see tera.gameserver.model.actions.Action#setActor(Player)
	 */
	@Override
	public final void setActor(Player actor)
	{
		this.actor = actor;
	}
	
	/**
	 * Method setSchedule.
	 * @param schedule ScheduledFuture<?>
	 */
	protected final void setSchedule(ScheduledFuture<?> schedule)
	{
		this.schedule = schedule;
	}
	
	/**
	 * Method setTarget.
	 * @param target Object
	 * @see tera.gameserver.model.actions.Action#setTarget(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final void setTarget(Object target)
	{
		this.target = (T) target;
	}
	
	/**
	 * Method test.
	 * @return boolean
	 * @see tera.gameserver.model.actions.Action#test()
	 */
	@Override
	public final boolean test()
	{
		return test(actor, target);
	}
	
	/**
	 * Method test.
	 * @param actor Player
	 * @param target T
	 * @return boolean
	 */
	protected abstract boolean test(Player actor, T target);
}
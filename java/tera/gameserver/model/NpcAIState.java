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
package tera.gameserver.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Node;

import tera.gameserver.model.ai.npc.taskfactory.DefaultBattleTaskFactory;
import tera.gameserver.model.ai.npc.taskfactory.DefaultPatrolTaskFactory;
import tera.gameserver.model.ai.npc.taskfactory.DefaultRageTaskFactory;
import tera.gameserver.model.ai.npc.taskfactory.DefaultReturnTaskFactory;
import tera.gameserver.model.ai.npc.taskfactory.DefaultRunAwayTaskFactory;
import tera.gameserver.model.ai.npc.taskfactory.DefaultWaitTaskFactory;
import tera.gameserver.model.ai.npc.taskfactory.TaskFactory;
import tera.gameserver.model.ai.npc.thinkaction.DefaultBattleAction;
import tera.gameserver.model.ai.npc.thinkaction.DefaultPatrolAction;
import tera.gameserver.model.ai.npc.thinkaction.DefaultRageAction;
import tera.gameserver.model.ai.npc.thinkaction.DefaultReturnAction;
import tera.gameserver.model.ai.npc.thinkaction.DefaultRunAwayAction;
import tera.gameserver.model.ai.npc.thinkaction.DefaultWaitAction;
import tera.gameserver.model.ai.npc.thinkaction.ThinkAction;

import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public enum NpcAIState
{
	WAIT(DefaultWaitAction.class, DefaultWaitTaskFactory.class),
	PATROL(DefaultPatrolAction.class, DefaultPatrolTaskFactory.class),
	RETURN_TO_HOME(DefaultReturnAction.class, DefaultReturnTaskFactory.class),
	IN_BATTLE(DefaultBattleAction.class, DefaultBattleTaskFactory.class),
	IN_RAGE(DefaultRageAction.class, DefaultRageTaskFactory.class),
	IN_RUN_AWAY(DefaultRunAwayAction.class, DefaultRunAwayTaskFactory.class);
	private Constructor<? extends ThinkAction> think;
	private Constructor<? extends TaskFactory> factory;
	
	/**
	 * Constructor for NpcAIState.
	 * @param think Class<? extends ThinkAction>
	 * @param factory Class<? extends TaskFactory>
	 */
	private NpcAIState(Class<? extends ThinkAction> think, Class<? extends TaskFactory> factory)
	{
		try
		{
			this.think = think.getConstructor(Node.class);
			this.factory = factory.getConstructor(Node.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Method getFactory.
	 * @return TaskFactory
	 */
	public final TaskFactory getFactory()
	{
		try
		{
			return factory.newInstance((Node) null);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, e);
		}
		
		return null;
	}
	
	/**
	 * Method getThink.
	 * @return ThinkAction
	 */
	public final ThinkAction getThink()
	{
		try
		{
			return think.newInstance((Node) null);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, e);
		}
		
		return null;
	}
}
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
package tera.gameserver.model.ai.npc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Node;

import tera.gameserver.model.NpcAIState;
import tera.gameserver.model.ai.npc.taskfactory.TaskFactory;
import tera.gameserver.model.ai.npc.thinkaction.ThinkAction;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public final class ConfigAI
{
	private static final Logger log = Loggers.getLogger(ConfigAI.class);
	public static final String TASK_FACTORY_PACKAGE = TaskFactory.class.getPackage().getName();
	public static final String THINK_ACTION_PACKAGE = ThinkAction.class.getPackage().getName();
	public static final String DEFAULT_NOTICE_MESSAGES = "DefaultNoticeMessages";
	public static final String DEFAULT_WALK_MESSAGES = "DefaultWalkMessages";
	public static final String DEFAULT_UPDATE_NOTICE_MESSAGES = "DefaultUpdateNoticeMessages";
	public static final String DEFAULT_SWITCH_TARGET_MESSAGES = "DefaultSwitchTargetMessages";
	public static final String DEFAULT_JUMP_SIDE_MESSAGES = "DefaultJumpSideMessages";
	public static final String DEFAULT_JUMP_BEHIND_MESSAGES = "DefaultJumpBehindMessages";
	public static final String DEFAULT_SHORT_ATTACK_MESSAGES = "DefaultShortAttackMessages";
	public static final String DEFAULT_LONG_ATTACK_MESSAGES = "DefaultLongAttackMessages";
	public static final String DEFAULT_RUN_AWAY_MESSAGES = "DefaultRunAwayMessages";
	public static final int DEFAULT_BATTLE_MAX_RANGE = 2000;
	public static final int DEFAULT_REACTION_MAX_RANGE = 1500;
	public static final int DEFAULT_RANDOM_MIN_WALK_RANGE = 50;
	public static final int DEFAULT_RANDOM_MAX_WALK_RANGE = 150;
	public static final int DEFAULT_RANDOM_MIN_WALK_DELAY = 5000;
	public static final int DEFAULT_RANDOM_MAX_WALK_DELAY = 25000;
	public static final int DEFAULT_NOTICE_RANGE = 150;
	public static final int DEFAULT_SHORT_RATE = 150;
	public static final int DEFAULT_AI_TASK_DELAY = 2000;
	public static final int DEFAULT_DISTANCE_TO_SPAWN_LOC = 40;
	public static final int DEFAULT_DISTANCE_TO_TELEPORT = 10000;
	public static final int DEFAULT_GROUP_CHANCE = 25;
	public static final int DEFAULT_LAST_ATTACKED_TIME = 60000;
	public static final int DEFAULT_CRITICAL_HP = 30;
	public static final int DEFAULT_REAR_RATE = 300;
	public static final int DEFAULT_RUN_AWAY_RATE = 300;
	public static final int DEFAULT_RUN_AWAY_OFFSET = 750;
	public static final int DEFAULT_MAX_MOST_HATED = 3;
	private final ThinkAction[] thinks;
	private final TaskFactory[] factory;
	private final String name;
	private final int[] intervals;
	private final boolean global;
	private final boolean runnable;
	
	/**
	 * Constructor for ConfigAI.
	 * @param node Node
	 */
	@SuppressWarnings("unchecked")
	public ConfigAI(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		name = vars.getString("name");
		final NpcAIState[] states = NpcAIState.values();
		thinks = new ThinkAction[states.length];
		factory = new TaskFactory[states.length];
		intervals = new int[states.length];
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("intervals".equals(child.getNodeName()))
			{
				vars.parse(child);
				final int def = vars.getInteger("default", DEFAULT_AI_TASK_DELAY);
				
				for (Node interval = child.getFirstChild(); interval != null; interval = interval.getNextSibling())
				{
					if ((interval.getNodeType() != Node.ELEMENT_NODE) || !"interval".equals(interval.getNodeName()))
					{
						continue;
					}
					
					vars.parse(interval);
					intervals[vars.getEnum("state", NpcAIState.class).ordinal()] = vars.getInteger("val");
				}
				
				for (int i = 0, length = intervals.length; i < length; i++)
				{
					if (intervals[i] < 1)
					{
						intervals[i] = def;
					}
				}
			}
			else if ("tasks".equals(child.getNodeName()))
			{
				for (Node task = child.getFirstChild(); task != null; task = task.getNextSibling())
				{
					if ((task.getNodeType() != Node.ELEMENT_NODE) || !"task".equals(task.getNodeName()))
					{
						continue;
					}
					
					vars.parse(task);
					
					try
					{
						final Class<TaskFactory> type = (Class<TaskFactory>) Class.forName(TASK_FACTORY_PACKAGE + "." + vars.getString("factory"));
						final Constructor<TaskFactory> constructor = type.getConstructor(Node.class);
						factory[vars.getEnum("state", NpcAIState.class).ordinal()] = constructor.newInstance(task);
					}
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e)
					{
						log.warning("name " + name);
						log.warning(e);
					}
				}
			}
			else if ("thinks".equals(child.getNodeName()))
			{
				for (Node think = child.getFirstChild(); think != null; think = think.getNextSibling())
				{
					if ((think.getNodeType() != Node.ELEMENT_NODE) || !"think".equals(think.getNodeName()))
					{
						continue;
					}
					
					vars.parse(think);
					
					try
					{
						final Class<ThinkAction> type = (Class<ThinkAction>) Class.forName(THINK_ACTION_PACKAGE + "." + vars.getString("action"));
						final Constructor<ThinkAction> constructor = type.getConstructor(Node.class);
						thinks[vars.getEnum("state", NpcAIState.class).ordinal()] = constructor.newInstance(think);
					}
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e)
					{
						log.warning("name " + name);
						log.warning(e);
					}
				}
			}
		}
		
		vars.parse(node, "set", "name", "val");
		global = vars.getBoolean("global", false);
		runnable = vars.getBoolean("runnable", true);
		
		for (int i = 0, length = states.length; i < length; i++)
		{
			if (thinks[i] == null)
			{
				thinks[i] = states[i].getThink();
			}
			
			if (factory[i] == null)
			{
				factory[i] = states[i].getFactory();
			}
		}
	}
	
	/**
	 * Method getFactory.
	 * @param state NpcAIState
	 * @return TaskFactory
	 */
	public final TaskFactory getFactory(NpcAIState state)
	{
		return factory[state.ordinal()];
	}
	
	/**
	 * Method getInterval.
	 * @param state NpcAIState
	 * @return int
	 */
	public final int getInterval(NpcAIState state)
	{
		return intervals[state.ordinal()];
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getThink.
	 * @param state NpcAIState
	 * @return ThinkAction
	 */
	public final ThinkAction getThink(NpcAIState state)
	{
		return thinks[state.ordinal()];
	}
	
	/**
	 * Method isGlobal.
	 * @return boolean
	 */
	public boolean isGlobal()
	{
		return global;
	}
	
	/**
	 * Method isRunnable.
	 * @return boolean
	 */
	public boolean isRunnable()
	{
		return runnable;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ConfigAI  name = " + name;
	}
}
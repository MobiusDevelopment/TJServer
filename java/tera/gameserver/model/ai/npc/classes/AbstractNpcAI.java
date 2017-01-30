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
package tera.gameserver.model.ai.npc.classes;

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.GeoManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MoveType;
import tera.gameserver.model.NpcAIState;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.ai.AbstractCharacterAI;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.ai.npc.Task;
import tera.gameserver.model.ai.npc.TaskType;
import tera.gameserver.model.ai.npc.taskfactory.TaskFactory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Minion;
import tera.gameserver.model.npc.MinionLeader;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.TargetType;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.util.Rnd;
import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 * @param <T>
 */
public abstract class AbstractNpcAI<T extends Npc> extends AbstractCharacterAI<T> implements NpcAI<T>, Runnable
{
	private static final Integer[] ATTACK_HEADINGS =
	{
		Integer.valueOf(-12000),
		Integer.valueOf(-9000),
		Integer.valueOf(-6000),
		Integer.valueOf(-3000),
		Integer.valueOf(0),
		Integer.valueOf(3000),
		Integer.valueOf(6000),
		Integer.valueOf(9000),
		Integer.valueOf(12000),
	};
	protected final FoldablePool<Task> taskPool;
	protected final Array<Task> taskList;
	protected Character target;
	protected NpcAIState currentState;
	protected ConfigAI config;
	private volatile ScheduledFuture<? extends AbstractNpcAI<T>> schedule;
	private volatile Integer attackHeading;
	private volatile int running;
	protected long clearAggro;
	protected long nextRandomWalk;
	protected long lastAttacked;
	protected long lastNotifyIcon;
	protected long lastMessage;
	protected long nextRoutePoint;
	protected int attackedCount;
	protected int followCounter;
	protected int routeIndex;
	
	/**
	 * Constructor for AbstractNpcAI.
	 * @param actor T
	 * @param config ConfigAI
	 */
	public AbstractNpcAI(T actor, ConfigAI config)
	{
		super(actor);
		this.config = config;
		this.taskList = Arrays.toConcurrentArray(Task.class);
		this.taskPool = Pools.newFoldablePool(Task.class);
		this.currentState = NpcAIState.WAIT;
		
		if (config == null)
		{
			log.warning(this, new IllegalArgumentException("not found config."));
			throw new IllegalArgumentException("not found config.");
		}
	}
	
	/**
	 * Method getNextRoutePoint.
	 * @return long
	 * @see tera.gameserver.model.ai.npc.NpcAI#getNextRoutePoint()
	 */
	@Override
	public long getNextRoutePoint()
	{
		return nextRoutePoint;
	}
	
	/**
	 * Method setNextRoutePoint.
	 * @param nextRoutePoint long
	 * @see tera.gameserver.model.ai.npc.NpcAI#setNextRoutePoint(long)
	 */
	@Override
	public void setNextRoutePoint(long nextRoutePoint)
	{
		this.nextRoutePoint = nextRoutePoint;
	}
	
	/**
	 * Method getRouteIndex.
	 * @return int
	 * @see tera.gameserver.model.ai.npc.NpcAI#getRouteIndex()
	 */
	@Override
	public int getRouteIndex()
	{
		return routeIndex;
	}
	
	/**
	 * Method setRouteIndex.
	 * @param routeIndex int
	 * @see tera.gameserver.model.ai.npc.NpcAI#setRouteIndex(int)
	 */
	@Override
	public void setRouteIndex(int routeIndex)
	{
		this.routeIndex = routeIndex;
	}
	
	/**
	 * Method setLastMessage.
	 * @param time long
	 * @see tera.gameserver.model.ai.npc.NpcAI#setLastMessage(long)
	 */
	@Override
	public void setLastMessage(long time)
	{
		this.lastMessage = time;
	}
	
	/**
	 * Method getLastMessage.
	 * @return long
	 * @see tera.gameserver.model.ai.npc.NpcAI#getLastMessage()
	 */
	@Override
	public long getLastMessage()
	{
		return lastMessage;
	}
	
	/**
	 * Method getTaskList.
	 * @return Array<Task>
	 */
	public Array<Task> getTaskList()
	{
		return taskList;
	}
	
	/**
	 * Method addCastMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param skill Skill
	 * @param target Character
	 * @see tera.gameserver.model.ai.npc.NpcAI#addCastMoveTask(float, float, float, Skill, Character)
	 */
	@Override
	public void addCastMoveTask(float x, float y, float z, Skill skill, Character target)
	{
		final Array<Task> taskList = getTaskList();
		taskList.writeLock();
		
		try
		{
			Task newTask = taskPool.take();
			
			if (newTask == null)
			{
				newTask = new Task();
			}
			
			newTask.setType(TaskType.MOVE_ON_CAST);
			newTask.setSkill(skill);
			newTask.setTarget(target);
			newTask.setX(x).setY(y).setZ(z);
			taskList.add(newTask);
		}
		
		finally
		{
			taskList.writeUnlock();
		}
	}
	
	/**
	 * Method addCastTask.
	 * @param skill Skill
	 * @param target Character
	 * @see tera.gameserver.model.ai.npc.NpcAI#addCastTask(Skill, Character)
	 */
	@Override
	public void addCastTask(Skill skill, Character target)
	{
		addCastTask(skill, target, Strings.EMPTY);
	}
	
	/**
	 * Method addCastTask.
	 * @param skill Skill
	 * @param target Character
	 * @param heading int
	 * @see tera.gameserver.model.ai.npc.NpcAI#addCastTask(Skill, Character, int)
	 */
	@Override
	public void addCastTask(Skill skill, Character target, int heading)
	{
		addCastTask(skill, target, heading, Strings.EMPTY);
	}
	
	/**
	 * Method addCastTask.
	 * @param skill Skill
	 * @param target Character
	 * @param heading int
	 * @param message String
	 * @see tera.gameserver.model.ai.npc.NpcAI#addCastTask(Skill, Character, int, String)
	 */
	@Override
	public void addCastTask(Skill skill, Character target, int heading, String message)
	{
		final Array<Task> taskList = getTaskList();
		taskList.writeLock();
		
		try
		{
			Task newTask = taskPool.take();
			
			if (newTask == null)
			{
				newTask = new Task();
			}
			
			newTask.setType(TaskType.CAST_ON_HEADING);
			newTask.setTarget(target);
			newTask.setSkill(skill);
			newTask.setHeading(heading);
			newTask.setMessage(message);
			taskList.add(newTask);
		}
		
		finally
		{
			taskList.writeUnlock();
		}
	}
	
	/**
	 * Method addCastTask.
	 * @param skill Skill
	 * @param target Character
	 * @param message String
	 * @see tera.gameserver.model.ai.npc.NpcAI#addCastTask(Skill, Character, String)
	 */
	@Override
	public void addCastTask(Skill skill, Character target, String message)
	{
		final Array<Task> taskList = getTaskList();
		taskList.writeLock();
		
		try
		{
			Task newTask = taskPool.take();
			
			if (newTask == null)
			{
				newTask = new Task();
			}
			
			newTask.setType(TaskType.CAST);
			newTask.setTarget(target);
			newTask.setSkill(skill);
			newTask.setMessage(message);
			taskList.add(newTask);
		}
		
		finally
		{
			taskList.writeUnlock();
		}
	}
	
	/**
	 * Method addMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param update boolean
	 * @see tera.gameserver.model.ai.npc.NpcAI#addMoveTask(float, float, float, boolean)
	 */
	@Override
	public void addMoveTask(float x, float y, float z, boolean update)
	{
		addMoveTask(x, y, z, update, Strings.EMPTY);
	}
	
	/**
	 * Method addMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param update boolean
	 * @param message String
	 * @see tera.gameserver.model.ai.npc.NpcAI#addMoveTask(float, float, float, boolean, String)
	 */
	@Override
	public void addMoveTask(float x, float y, float z, boolean update, String message)
	{
		final Array<Task> taskList = getTaskList();
		taskList.writeLock();
		
		try
		{
			Task newTask = taskPool.take();
			
			if (newTask == null)
			{
				newTask = new Task();
			}
			
			newTask.setType(update ? TaskType.MOVE_UPDATE_HEADING : TaskType.MOVE_NOT_UPDATE_HEADING);
			newTask.setX(x).setY(y).setZ(z);
			newTask.setMessage(message);
			taskList.add(newTask);
		}
		
		finally
		{
			taskList.writeUnlock();
		}
	}
	
	/**
	 * Method addMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param skill Skill
	 * @param target Character
	 * @see tera.gameserver.model.ai.npc.NpcAI#addMoveTask(float, float, float, Skill, Character)
	 */
	@Override
	public void addMoveTask(float x, float y, float z, Skill skill, Character target)
	{
		addMoveTask(x, y, z, skill, target, Strings.EMPTY);
	}
	
	/**
	 * Method addMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param skill Skill
	 * @param target Character
	 * @param message String
	 * @see tera.gameserver.model.ai.npc.NpcAI#addMoveTask(float, float, float, Skill, Character, String)
	 */
	@Override
	public void addMoveTask(float x, float y, float z, Skill skill, Character target, String message)
	{
		final Array<Task> taskList = getTaskList();
		taskList.writeLock();
		
		try
		{
			Task newTask = taskPool.take();
			
			if (newTask == null)
			{
				newTask = new Task();
			}
			
			newTask.setType(TaskType.MOVE_ON_CAST);
			newTask.setX(x).setY(y).setZ(z);
			newTask.setTarget(target);
			newTask.setSkill(skill);
			newTask.setMessage(message);
			taskList.add(newTask);
		}
		
		finally
		{
			taskList.writeUnlock();
		}
	}
	
	/**
	 * Method addMoveTask.
	 * @param loc Location
	 * @param update boolean
	 * @see tera.gameserver.model.ai.npc.NpcAI#addMoveTask(Location, boolean)
	 */
	@Override
	public void addMoveTask(Location loc, boolean update)
	{
		addMoveTask(loc, update, Strings.EMPTY);
	}
	
	/**
	 * Method addMoveTask.
	 * @param loc Location
	 * @param update boolean
	 * @param message String
	 * @see tera.gameserver.model.ai.npc.NpcAI#addMoveTask(Location, boolean, String)
	 */
	@Override
	public void addMoveTask(Location loc, boolean update, String message)
	{
		addMoveTask(loc.getX(), loc.getY(), loc.getZ(), update, message);
	}
	
	/**
	 * Method addNoticeTask.
	 * @param target Character
	 * @param fast boolean
	 * @see tera.gameserver.model.ai.npc.NpcAI#addNoticeTask(Character, boolean)
	 */
	@Override
	public void addNoticeTask(Character target, boolean fast)
	{
		addNoticeTask(target, fast, Strings.EMPTY);
	}
	
	/**
	 * Method addNoticeTask.
	 * @param target Character
	 * @param fast boolean
	 * @param message String
	 * @see tera.gameserver.model.ai.npc.NpcAI#addNoticeTask(Character, boolean, String)
	 */
	@Override
	public void addNoticeTask(Character target, boolean fast, String message)
	{
		final Array<Task> taskList = getTaskList();
		taskList.writeLock();
		
		try
		{
			Task newTask = taskPool.take();
			
			if (newTask == null)
			{
				newTask = new Task();
			}
			
			newTask.setType(fast ? TaskType.NOTICE_FAST : TaskType.NOTICE);
			newTask.setTarget(target);
			newTask.setMessage(message);
			taskList.add(newTask);
		}
		
		finally
		{
			taskList.writeUnlock();
		}
	}
	
	/**
	 * Method addTask.
	 * @param task Task
	 * @see tera.gameserver.model.ai.npc.NpcAI#addTask(Task)
	 */
	@Override
	public void addTask(Task task)
	{
		taskList.add(task);
	}
	
	/**
	 * Method checkAggression.
	 * @param target Character
	 * @return boolean
	 * @see tera.gameserver.model.ai.npc.NpcAI#checkAggression(Character)
	 */
	@Override
	public boolean checkAggression(Character target)
	{
		final T actor = getActor();
		
		if ((actor == null) || (target == null) || !target.isInRange(actor, actor.getAggroRange()))
		{
			return false;
		}
		
		if (actor.checkTarget(target))
		{
			if (!target.isPlayer())
			{
				actor.addAggro(target, 1, true);
				return true;
			}
			else if (!target.getPlayer().isGM())
			{
				actor.addAggro(target, 1, true);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Method getTaskPool.
	 * @return FoldablePool<Task>
	 */
	public FoldablePool<Task> getTaskPool()
	{
		return taskPool;
	}
	
	/**
	 * Method clearTaskList.
	 * @see tera.gameserver.model.ai.CharacterAI#clearTaskList()
	 */
	@Override
	public void clearTaskList()
	{
		final Array<Task> taskList = getTaskList();
		
		if (taskList.isEmpty())
		{
			return;
		}
		
		final FoldablePool<Task> taskPool = getTaskPool();
		taskList.writeLock();
		
		try
		{
			final Task[] array = taskList.array();
			
			for (int i = 0, length = taskList.size(); i < length; i++)
			{
				taskPool.put(array[i]);
			}
			
			taskList.clear();
		}
		
		finally
		{
			taskList.writeUnlock();
		}
	}
	
	protected void forceClearTaskList()
	{
		taskList.clear();
	}
	
	/**
	 * Method doTask.
	 * @param actor T
	 * @param currentTime long
	 * @param local LocalObjects
	 * @return boolean
	 */
	@Override
	public boolean doTask(T actor, long currentTime, LocalObjects local)
	{
		if (actor.isDead())
		{
			return false;
		}
		
		final Array<Task> taskList = getTaskList();
		
		if (taskList.isEmpty())
		{
			return true;
		}
		
		final Task currentTask = taskList.poll();
		final TaskType type = currentTask.getType();
		
		if (type == null)
		{
			log.warning(this, "not found type for task " + currentTask);
			forceClearTaskList();
			return false;
		}
		
		switch (type)
		{
			case MOVE_UPDATE_HEADING:
			{
				if (actor.isMovementDisabled())
				{
					return maybeNextTask(currentTask, false, true);
				}
				
				final float distance = actor.getDistance(currentTask.getX(), currentTask.getY(), currentTask.getZ());
				
				if (distance < 30F)
				{
					return maybeNextTask(currentTask, false, true);
				}
				
				actor.setHeading(actor.calcHeading(currentTask.getX(), currentTask.getY()));
				
				if (currentTask.getMessage() != Strings.EMPTY)
				{
					actor.sayMessage(currentTask.getMessage());
				}
				
				startMove(actor.getHeading(), MoveType.RUN, currentTask.getX(), currentTask.getY(), currentTask.getZ(), true, false);
				return maybeNextTask(currentTask, false, true);
			}
			
			case MOVE_NOT_UPDATE_HEADING:
			{
				if (actor.isMovementDisabled())
				{
					return maybeNextTask(currentTask, false, true);
				}
				
				final float distance = actor.getDistance(currentTask.getX(), currentTask.getY(), currentTask.getZ());
				
				if (distance < 30F)
				{
					return maybeNextTask(currentTask, false, true);
				}
				
				startMove(actor.getHeading(), MoveType.RUN, currentTask.getX(), currentTask.getY(), currentTask.getZ(), true, false);
				return maybeNextTask(currentTask, false, true);
			}
			
			case NOTICE_FAST:
			case NOTICE:
			{
				final Character target = currentTask.getTarget();
				
				if (target == actor)
				{
					forceClearTaskList();
					return false;
				}
				
				final boolean fast = type == TaskType.NOTICE_FAST;
				
				if ((target != null) && !actor.isInFront(target))
				{
					if (fast)
					{
						actor.setHeading(actor.calcHeading(target.getX(), target.getY()));
					}
					else
					{
						actor.nextTurn(actor.calcHeading(target.getX(), target.getY()));
					}
				}
				
				actor.startBattleStance(target);
				setTarget(target);
				
				if (currentTask.getMessage() != Strings.EMPTY)
				{
					actor.sayMessage(currentTask.getMessage());
				}
				
				return maybeNextTask(currentTask, false, true);
			}
			
			case CAST_ON_HEADING:
			case CAST:
			{
				final Character target = currentTask.getTarget();
				final Skill skill = currentTask.getSkill();
				
				if ((target == null) || (skill == null))
				{
					clearTaskList();
					return false;
				}
				
				final boolean self = (actor == target) || (skill.getTargetType() == TargetType.TARGET_SELF);
				final int maxDist = skill.getCastMaxRange();
				final int minDist = skill.getCastMinRange();
				final int currentDistance = self ? 0 : (int) (target.getDistance(actor.getX(), actor.getY(), target.getZ()));
				
				if (!self && ((currentDistance > maxDist) || (currentDistance < minDist)))
				{
					final int followCounter = getFollowCounter();
					
					if (followCounter > 3)
					{
						setFollowCounter(0);
						setAttackHeading(null);
						return maybeNextTask(currentTask, false, true);
					}
					
					int newDist = target.isRangeClass() ? maxDist / 2 : ((int) (maxDist - actor.getGeomRadius()) * 2) / 3;
					newDist = Math.max(newDist, (int) (actor.getGeomRadius() + target.getGeomRadius()));
					newDist = Math.min(maxDist, newDist);
					Integer attackHeading = getAttackHeading();
					
					if (attackHeading == null)
					{
						attackHeading = getNextAttackHeading();
						setAttackHeading(attackHeading);
					}
					
					final int heading = target.calcHeading(actor.getX(), actor.getY()) + attackHeading.intValue();
					final float newX = Coords.calcX(target.getX(), newDist, heading);
					final float newY = Coords.calcY(target.getY(), newDist, heading);
					final GeoManager geoManager = GeoManager.getInstance();
					final float newZ = geoManager.getHeight(actor.getContinentId(), newX, newY, target.getZ());
					addMoveTask(newX, newY, newZ, true);
					addTask(currentTask);
					setFollowCounter(followCounter + 1);
					return doTask(actor, currentTime, local);
				}
				
				actor.setTarget(getTarget());
				setAttackHeading(null);
				
				if (currentTask.getMessage() != Strings.EMPTY)
				{
					actor.sayMessage(currentTask.getMessage());
				}
				
				float targetX = target.getX();
				float targetY = target.getY();
				final int speed = skill.getSpeed();
				
				if ((target != actor) && target.isMoving())
				{
					final Formulas formulas = Formulas.getInstance();
					float time = 0F;
					
					if (speed > 1)
					{
						time += ((float) currentDistance / speed);
					}
					
					time += (formulas.castTime(skill.getDelay(), actor) / 1000F);
					final int newDist = (int) (target.getRunSpeed() * time) + skill.getRadius();
					final float radians = Angles.headingToRadians(target.getHeading());
					targetX = Coords.calcX(targetX, newDist, radians);
					targetY = Coords.calcY(targetY, newDist, radians);
				}
				
				final float targetZ = target.getZ() + (target.getGeomHeight() / 2);
				
				if (type == TaskType.CAST_ON_HEADING)
				{
					startCast(skill, currentTask.getHeading(), targetX, targetY, targetZ);
				}
				else
				{
					startCast(skill, actor.calcHeading(targetX, targetY), targetX, targetY, targetZ);
				}
				
				return maybeNextTask(currentTask, false, true);
			}
			
			default:
			{
				log.warning(this, "not supported task type " + type);
				break;
			}
		}
		
		return true;
	}
	
	/**
	 * Method setFollowCounter.
	 * @param followCounter int
	 */
	public void setFollowCounter(int followCounter)
	{
		this.followCounter = followCounter;
	}
	
	/**
	 * Method getFollowCounter.
	 * @return int
	 */
	public int getFollowCounter()
	{
		return followCounter;
	}
	
	/**
	 * Method removeTask.
	 * @param task Task
	 * @see tera.gameserver.model.ai.npc.NpcAI#removeTask(Task)
	 */
	@Override
	public void removeTask(Task task)
	{
		taskList.slowRemove(task);
	}
	
	/**
	 * Method getAttackedCount.
	 * @return int
	 * @see tera.gameserver.model.ai.npc.NpcAI#getAttackedCount()
	 */
	@Override
	public int getAttackedCount()
	{
		return attackedCount;
	}
	
	/**
	 * Method getClearAggro.
	 * @return long
	 * @see tera.gameserver.model.ai.npc.NpcAI#getClearAggro()
	 */
	@Override
	public long getClearAggro()
	{
		return clearAggro;
	}
	
	/**
	 * Method getCurrentFactory.
	 * @return TaskFactory
	 * @see tera.gameserver.model.ai.npc.NpcAI#getCurrentFactory()
	 */
	@Override
	public TaskFactory getCurrentFactory()
	{
		return config.getFactory(currentState);
	}
	
	/**
	 * Method getCurrentState.
	 * @return NpcAIState
	 * @see tera.gameserver.model.ai.npc.NpcAI#getCurrentState()
	 */
	@Override
	public NpcAIState getCurrentState()
	{
		return currentState;
	}
	
	/**
	 * Method getLastAttacked.
	 * @return long
	 * @see tera.gameserver.model.ai.npc.NpcAI#getLastAttacked()
	 */
	@Override
	public long getLastAttacked()
	{
		return lastAttacked;
	}
	
	/**
	 * Method getLastNotifyIcon.
	 * @return long
	 * @see tera.gameserver.model.ai.npc.NpcAI#getLastNotifyIcon()
	 */
	@Override
	public long getLastNotifyIcon()
	{
		return lastNotifyIcon;
	}
	
	/**
	 * Method getNextRandomWalk.
	 * @return long
	 * @see tera.gameserver.model.ai.npc.NpcAI#getNextRandomWalk()
	 */
	@Override
	public final long getNextRandomWalk()
	{
		return nextRandomWalk;
	}
	
	/**
	 * Method getTarget.
	 * @return Character
	 * @see tera.gameserver.model.ai.npc.NpcAI#getTarget()
	 */
	@Override
	public Character getTarget()
	{
		return target;
	}
	
	/**
	 * Method isGlobalAI.
	 * @return boolean
	 * @see tera.gameserver.model.ai.CharacterAI#isGlobalAI()
	 */
	@Override
	public boolean isGlobalAI()
	{
		return config.isGlobal();
	}
	
	/**
	 * Method isWaitingTask.
	 * @return boolean
	 * @see tera.gameserver.model.ai.npc.NpcAI#isWaitingTask()
	 */
	@Override
	public boolean isWaitingTask()
	{
		return !taskList.isEmpty();
	}
	
	/**
	 * Method maybeNextTask.
	 * @param task Task
	 * @return boolean
	 */
	protected boolean maybeNextTask(Task task)
	{
		return maybeNextTask(task, true, true);
	}
	
	/**
	 * Method maybeNextTask.
	 * @param task Task
	 * @param remove boolean
	 * @param pool boolean
	 * @return boolean
	 */
	protected boolean maybeNextTask(Task task, boolean remove, boolean pool)
	{
		final Array<Task> taskList = getTaskList();
		
		if (task != null)
		{
			taskList.writeLock();
			
			try
			{
				if (remove)
				{
					taskList.slowRemove(task);
				}
				
				if (pool)
				{
					taskPool.put(task);
				}
			}
			
			finally
			{
				taskList.writeUnlock();
			}
		}
		
		return taskList.isEmpty();
	}
	
	/**
	 * Method notifyAttacked.
	 * @param attacker Character
	 * @param skill Skill
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyAttacked(Character, Skill, int)
	 */
	@Override
	public void notifyAttacked(Character attacker, Skill skill, int damage)
	{
		final T actor = getActor();
		
		if (actor == null)
		{
			return;
		}
		
		if (running < 1)
		{
			startAITask();
		}
		
		final NpcAIState currentState = getCurrentState();
		
		if ((currentState == NpcAIState.WAIT) || (currentState == NpcAIState.PATROL))
		{
			if (actor.isMoving())
			{
				actor.stopMove();
			}
		}
		
		if (attacker == null)
		{
			return;
		}
		
		actor.addAggro(attacker, damage, true);
		
		if (skill != null)
		{
			actor.addAggro(attacker, skill.getAggroPoint(), false);
		}
		
		setAttackedCount(attackedCount + 1);
		setLastAttacked(System.currentTimeMillis());
		final int range = actor.getFractionRange();
		
		if (range > 0)
		{
			final String fraction = actor.getFraction();
			
			if (fraction != Strings.EMPTY)
			{
				final LocalObjects local = LocalObjects.get();
				final Array<Npc> npcs = World.getAround(Npc.class, local.getNextNpcList(), actor, range);
				
				if (!npcs.isEmpty())
				{
					final Npc[] array = npcs.array();
					
					for (int i = 0, length = npcs.size(); i < length; i++)
					{
						final Npc npc = array[i];
						
						if ((npc == null) || (npc == actor) || !fraction.equals(npc.getFraction()))
						{
							continue;
						}
						
						npc.getAI().notifyClanAttacked(actor, attacker, damage);
					}
				}
			}
		}
		
		final MinionLeader leader = actor.getMinionLeader();
		
		if (leader != null)
		{
			final Array<Minion> minions = leader.getMinions();
			
			if (!minions.isEmpty())
			{
				minions.readLock();
				
				try
				{
					final Npc[] array = minions.array();
					
					for (int i = 0, length = minions.size(); i < length; i++)
					{
						final Npc minion = array[i];
						
						if ((minion == null) || (minion == actor))
						{
							continue;
						}
						
						minion.getAI().notifyPartyAttacked(actor, attacker, damage);
					}
				}
				
				finally
				{
					minions.readUnlock();
				}
			}
			
			if (leader != actor)
			{
				leader.getAI().notifyPartyAttacked(actor, attacker, damage);
			}
		}
	}
	
	/**
	 * Method notifyClanAttacked.
	 * @param attackedMember Character
	 * @param attacker Character
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyClanAttacked(Character, Character, int)
	 */
	@Override
	public void notifyClanAttacked(Character attackedMember, Character attacker, int damage)
	{
		final T actor = getActor();
		
		if ((actor == null) || actor.isDead())
		{
			return;
		}
		
		actor.addAggro(attacker, damage, true);
	}
	
	/**
	 * Method notifyPartyAttacked.
	 * @param attackedMember Character
	 * @param attacker Character
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyPartyAttacked(Character, Character, int)
	 */
	@Override
	public void notifyPartyAttacked(Character attackedMember, Character attacker, int damage)
	{
		final T actor = getActor();
		
		if ((actor == null) || actor.isDead())
		{
			return;
		}
		
		actor.addAggro(attacker, damage, true);
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		final T actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		final LocalObjects local = LocalObjects.get();
		final long currentTime = System.currentTimeMillis();
		
		try
		{
			config.getThink(currentState).think(this, actor, local, config, currentTime);
		}
		catch (Exception exc)
		{
			log.warning(this, exc);
		}
	}
	
	/**
	 * Method setAttackedCount.
	 * @param count int
	 * @see tera.gameserver.model.ai.npc.NpcAI#setAttackedCount(int)
	 */
	@Override
	public void setAttackedCount(int count)
	{
		this.attackedCount = count;
	}
	
	/**
	 * Method setClearAggro.
	 * @param clearAggro long
	 * @see tera.gameserver.model.ai.npc.NpcAI#setClearAggro(long)
	 */
	@Override
	public void setClearAggro(long clearAggro)
	{
		this.clearAggro = clearAggro;
	}
	
	/**
	 * Method setConfig.
	 * @param config ConfigAI
	 */
	public void setConfig(ConfigAI config)
	{
		this.config = config;
	}
	
	/**
	 * Method setCurrentState.
	 * @param currentState NpcAIState
	 */
	protected void setCurrentState(NpcAIState currentState)
	{
		this.currentState = currentState;
	}
	
	/**
	 * Method setLastAttacked.
	 * @param lastAttacked long
	 * @see tera.gameserver.model.ai.npc.NpcAI#setLastAttacked(long)
	 */
	@Override
	public void setLastAttacked(long lastAttacked)
	{
		this.lastAttacked = lastAttacked;
	}
	
	/**
	 * Method setLastNotifyIcon.
	 * @param lastNotifyIcon long
	 * @see tera.gameserver.model.ai.npc.NpcAI#setLastNotifyIcon(long)
	 */
	@Override
	public void setLastNotifyIcon(long lastNotifyIcon)
	{
		this.lastNotifyIcon = lastNotifyIcon;
	}
	
	/**
	 * Method setNewState.
	 * @param state NpcAIState
	 * @see tera.gameserver.model.ai.npc.NpcAI#setNewState(NpcAIState)
	 */
	@Override
	public void setNewState(NpcAIState state)
	{
		if (currentState == state)
		{
			return;
		}
		
		synchronized (this)
		{
			if (currentState == state)
			{
				return;
			}
			
			if (schedule != null)
			{
				schedule.cancel(false);
			}
			
			setCurrentState(state);
			config.getThink(currentState).prepareState(this, actor, LocalObjects.get(), config, System.currentTimeMillis());
			final int interval = config.getInterval(state);
			final ExecutorManager executor = ExecutorManager.getInstance();
			schedule = executor.scheduleAiAtFixedRate(this, interval, interval);
		}
	}
	
	/**
	 * Method setNextRandomWalk.
	 * @param nextRandomWalk long
	 * @see tera.gameserver.model.ai.npc.NpcAI#setNextRandomWalk(long)
	 */
	@Override
	public final void setNextRandomWalk(long nextRandomWalk)
	{
		this.nextRandomWalk = nextRandomWalk;
	}
	
	/**
	 * Method setTarget.
	 * @param target Character
	 * @see tera.gameserver.model.ai.npc.NpcAI#setTarget(Character)
	 */
	@Override
	public void setTarget(Character target)
	{
		this.target = target;
	}
	
	/**
	 * Method startAITask.
	 * @see tera.gameserver.model.ai.CharacterAI#startAITask()
	 */
	@Override
	public synchronized void startAITask()
	{
		if (!config.isRunnable())
		{
			return;
		}
		
		if (running > 0)
		{
			return;
		}
		
		running += 1;
		final int interval = config.getInterval(currentState);
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleAiAtFixedRate(this, interval, interval);
	}
	
	/**
	 * Method stopAITask.
	 * @see tera.gameserver.model.ai.CharacterAI#stopAITask()
	 */
	@Override
	public synchronized void stopAITask()
	{
		if (!config.isRunnable())
		{
			return;
		}
		
		if (running < 1)
		{
			return;
		}
		
		running -= 1;
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		setCurrentState(NpcAIState.WAIT);
		final T actor = getActor();
		
		if (actor != null)
		{
			actor.clearAggroList();
		}
		
		clearTaskList();
		schedule = null;
	}
	
	/**
	 * Method getAttackHeading.
	 * @return Integer
	 * @see tera.gameserver.model.ai.npc.NpcAI#getAttackHeading()
	 */
	@Override
	public Integer getAttackHeading()
	{
		return attackHeading;
	}
	
	/**
	 * Method setAttackHeading.
	 * @param attackHeading Integer
	 * @see tera.gameserver.model.ai.npc.NpcAI#setAttackHeading(Integer)
	 */
	@Override
	public void setAttackHeading(Integer attackHeading)
	{
		this.attackHeading = attackHeading;
	}
	
	/**
	 * Method getNextAttackHeading.
	 * @return Integer
	 */
	protected Integer getNextAttackHeading()
	{
		return ATTACK_HEADINGS[Rnd.nextInt(0, ATTACK_HEADINGS.length - 1)];
	}
	
	/**
	 * Method notifySpawn.
	 * @see tera.gameserver.model.ai.AI#notifySpawn()
	 */
	@Override
	public void notifySpawn()
	{
	}
	
	/**
	 * Method notifyStartDialog.
	 * @param player Player
	 * @see tera.gameserver.model.ai.AI#notifyStartDialog(Player)
	 */
	@Override
	public void notifyStartDialog(Player player)
	{
	}
	
	/**
	 * Method notifyStopDialog.
	 * @param player Player
	 * @see tera.gameserver.model.ai.AI#notifyStopDialog(Player)
	 */
	@Override
	public void notifyStopDialog(Player player)
	{
	}
	
	/**
	 * Method notifyArrived.
	 * @see tera.gameserver.model.ai.AI#notifyArrived()
	 */
	@Override
	public void notifyArrived()
	{
	}
	
	/**
	 * Method notifyAgression.
	 * @param attacker Character
	 * @param aggro long
	 * @see tera.gameserver.model.ai.AI#notifyAgression(Character, long)
	 */
	@Override
	public void notifyAgression(Character attacker, long aggro)
	{
	}
	
	/**
	 * Method notifyAttack.
	 * @param attacked Character
	 * @param skill Skill
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyAttack(Character, Skill, int)
	 */
	@Override
	public void notifyAttack(Character attacked, Skill skill, int damage)
	{
	}
	
	/**
	 * Method notifyDead.
	 * @param killer Character
	 * @see tera.gameserver.model.ai.AI#notifyDead(Character)
	 */
	@Override
	public void notifyDead(Character killer)
	{
	}
	
	/**
	 * Method notifyAppliedEffect.
	 * @param effect Effect
	 * @see tera.gameserver.model.ai.AI#notifyAppliedEffect(Effect)
	 */
	@Override
	public void notifyAppliedEffect(Effect effect)
	{
	}
	
	/**
	 * Method notifyArrivedBlocked.
	 * @see tera.gameserver.model.ai.AI#notifyArrivedBlocked()
	 */
	@Override
	public void notifyArrivedBlocked()
	{
	}
	
	/**
	 * Method notifyArrivedTarget.
	 * @param target TObject
	 * @see tera.gameserver.model.ai.AI#notifyArrivedTarget(TObject)
	 */
	@Override
	public void notifyArrivedTarget(TObject target)
	{
	}
	
	/**
	 * Method notifyCollectResourse.
	 * @param resourse ResourseInstance
	 * @see tera.gameserver.model.ai.AI#notifyCollectResourse(ResourseInstance)
	 */
	@Override
	public void notifyCollectResourse(ResourseInstance resourse)
	{
	}
	
	/**
	 * Method notifyFinishCasting.
	 * @param skill Skill
	 * @see tera.gameserver.model.ai.AI#notifyFinishCasting(Skill)
	 */
	@Override
	public void notifyFinishCasting(Skill skill)
	{
	}
	
	/**
	 * Method notifyPickUpItem.
	 * @param item ItemInstance
	 * @see tera.gameserver.model.ai.AI#notifyPickUpItem(ItemInstance)
	 */
	@Override
	public void notifyPickUpItem(ItemInstance item)
	{
	}
	
	/**
	 * Method notifyStartCasting.
	 * @param skill Skill
	 * @see tera.gameserver.model.ai.AI#notifyStartCasting(Skill)
	 */
	@Override
	public void notifyStartCasting(Skill skill)
	{
	}
	
	/**
	 * Method isActiveDialog.
	 * @return boolean
	 * @see tera.gameserver.model.ai.npc.NpcAI#isActiveDialog()
	 */
	@Override
	public boolean isActiveDialog()
	{
		return false;
	}
}
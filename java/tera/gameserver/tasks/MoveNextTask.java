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

import java.util.concurrent.locks.Lock;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MoveType;
import tera.gameserver.taskmanager.MoveTaskManager;

import rlib.concurrent.Locks;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public final class MoveNextTask
{
	public static final int INTERVAL = 100;
	
	private final Lock lock;
	
	private final Character owner;
	
	private MoveType type;
	
	private volatile Array<MoveNextTask> container;
	
	private float alldist;
	
	private float mod;
	
	private float startX;
	private float startY;
	private float startZ;
	
	private float targetX;
	private float targetY;
	private float targetZ;
	
	private int lastSpeed;
	
	private long startTime;
	
	/**
	 * Constructor for MoveNextTask.
	 * @param character Character
	 */
	public MoveNextTask(Character character)
	{
		owner = character;
		lock = Locks.newLock();
	}
	
	public void done()
	{
		final Character owner = getOwner();
		lock.lock();
		
		try
		{
			owner.setXYZ(targetX, targetY, targetZ);
			owner.setMoving(false);
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyArrived(owner);
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method getContainer.
	 * @return Array<MoveNextTask>
	 */
	public Array<MoveNextTask> getContainer()
	{
		return container;
	}
	
	/**
	 * Method getLastSpeed.
	 * @return int
	 */
	private final int getLastSpeed()
	{
		return lastSpeed;
	}
	
	/**
	 * Method getMod.
	 * @return float
	 */
	public float getMod()
	{
		return mod;
	}
	
	/**
	 * Method getOwner.
	 * @return Character
	 */
	public Character getOwner()
	{
		return owner;
	}
	
	/**
	 * Method getStartTime.
	 * @return long
	 */
	public long getStartTime()
	{
		return startTime;
	}
	
	/**
	 * Method getType.
	 * @return MoveType
	 */
	public MoveType getType()
	{
		return type;
	}
	
	/**
	 * Method move.
	 * @param owner Character
	 * @param currentTime long
	 * @param currentSpeed int
	 * @return boolean
	 */
	private boolean move(Character owner, long currentTime, int currentSpeed)
	{
		final float donedist = (((currentTime - getStartTime()) * currentSpeed) / 1000F) * getMod();
		final float done = donedist / alldist;
		
		if ((!owner.isPlayer() || type.isFall()) && (done >= 1))
		{
			return true;
		}
		
		final float newX = startX + ((targetX - startX) * done);
		final float newY = startY + ((targetY - startY) * done);
		final float newZ = startZ + ((targetZ - startZ) * done);
		owner.setXYZ(newX, newY, newZ);
		return false;
	}
	
	/**
	 * Method nextTask.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param type MoveType
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	public void nextTask(float startX, float startY, float startZ, MoveType type, float targetX, float targetY, float targetZ)
	{
		final Character owner = getOwner();
		final int currentSpeed = owner.getRunSpeed();
		lock.lock();
		
		try
		{
			setType(type);
			setLastSpeed(currentSpeed);
			
			if (currentSpeed < 1)
			{
				return;
			}
			
			float dist = 0;
			
			if (type == MoveType.STOP)
			{
				dist = owner.getDistance(targetX, targetY, targetZ);
				
				if (dist < (currentSpeed / 3))
				{
					owner.setXYZ(targetX, targetY, targetZ);
				}
				
				owner.setMoving(false);
				return;
			}
			
			float modiff = 1;
			
			if (!owner.isPlayer())
			{
				modiff = 1F;
			}
			else if (type.isFall())
			{
				modiff = 2F;
			}
			
			setMod(modiff);
			this.startX = startX;
			this.startY = startY;
			this.startZ = startZ;
			this.targetX = targetX;
			this.targetY = targetY;
			this.targetZ = targetZ;
			final float dx = startX - targetX;
			final float dy = startY - targetY;
			final float dz = startZ - targetZ;
			final float alldist = (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
			
			if (alldist < 0.001F)
			{
				return;
			}
			
			setStartTime(System.currentTimeMillis());
			setAlldist(alldist);
			owner.setMoving(true);
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method setAlldist.
	 * @param alldist float
	 */
	public void setAlldist(float alldist)
	{
		this.alldist = alldist;
	}
	
	/**
	 * Method setContainer.
	 * @param container Array<MoveNextTask>
	 */
	public void setContainer(Array<MoveNextTask> container)
	{
		this.container = container;
	}
	
	/**
	 * Method setLastSpeed.
	 * @param last int
	 */
	private final void setLastSpeed(int last)
	{
		lastSpeed = last;
	}
	
	/**
	 * Method setMod.
	 * @param mod float
	 */
	public void setMod(float mod)
	{
		this.mod = mod;
	}
	
	/**
	 * Method setStartTime.
	 * @param startTime long
	 */
	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}
	
	/**
	 * Method setType.
	 * @param type MoveType
	 */
	public void setType(MoveType type)
	{
		this.type = type;
	}
	
	public void startTask()
	{
		if (container != null)
		{
			return;
		}
		
		lock.lock();
		
		try
		{
			if (container != null)
			{
				return;
			}
			
			final MoveTaskManager taskManager = MoveTaskManager.getInstance();
			taskManager.addMoveTask(this);
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	public void stopMove()
	{
		final Character owner = getOwner();
		lock.lock();
		
		try
		{
			owner.setMoving(false);
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyArrived(owner);
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	public void stopTask()
	{
		if (container == null)
		{
			return;
		}
		
		final MoveTaskManager taskManager = MoveTaskManager.getInstance();
		taskManager.removeTask(this);
	}
	
	public void update()
	{
		final Character owner = getOwner();
		final MoveType type = getType();
		
		if (owner.isMoving() && (type != null))
		{
			owner.broadcastMove(owner.getX(), owner.getY(), owner.getZ(), owner.getHeading(), type, targetX, targetY, targetZ, false);
		}
	}
	
	/**
	 * Method update.
	 * @param target Character
	 */
	public void update(Character target)
	{
		final Character owner = getOwner();
		final MoveType type = getType();
		
		if (owner.isMoving() && (type != null))
		{
			target.sendPacket(owner.getMovePacket(type, targetX, targetY, targetZ), true);
		}
	}
	
	/**
	 * Method update.
	 * @param currentTime long
	 */
	public void update(long currentTime)
	{
		final Character owner = getOwner();
		
		if (!owner.isMoving())
		{
			return;
		}
		
		int result = 0;
		lock.lock();
		
		try
		{
			final int currentSpeed = owner.getRunSpeed();
			
			if (currentSpeed < 1)
			{
				result = 1;
			}
			else
			{
				if (getLastSpeed() != currentSpeed)
				{
					setLastSpeed(currentSpeed);
					result = 2;
				}
				
				if (move(owner, currentTime, currentSpeed))
				{
					result = 3;
				}
			}
		}
		
		finally
		{
			lock.unlock();
		}
		
		switch (result)
		{
			case 1:
				owner.stopMove();
				break;
			
			case 2:
				update();
				break;
			
			case 3:
				done();
		}
	}
}

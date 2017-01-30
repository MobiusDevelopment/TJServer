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
package tera.gameserver.model.skillengine.shots;

import java.util.concurrent.ScheduledFuture;

import tera.Config;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;

import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public abstract class AbstractAutoShot implements Shot
{
	protected static final Logger log = Loggers.getLogger(Shot.class);
	protected Character caster;
	protected Character target;
	protected Skill skill;
	protected int speed;
	protected int radius;
	protected long startTime;
	protected float startX;
	protected float startY;
	protected float startZ;
	protected ScheduledFuture<?> task;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		caster = null;
		target = null;
		skill = null;
	}
	
	/**
	 * Method getCaster.
	 * @return Character
	 */
	protected final Character getCaster()
	{
		return caster;
	}
	
	/**
	 * Method getObjectId.
	 * @return int
	 * @see tera.gameserver.model.skillengine.shots.Shot#getObjectId()
	 */
	@Override
	public int getObjectId()
	{
		return 0;
	}
	
	/**
	 * Method getRadius.
	 * @return int
	 */
	protected final int getRadius()
	{
		return radius;
	}
	
	/**
	 * Method getSkill.
	 * @return Skill
	 */
	public Skill getSkill()
	{
		return skill;
	}
	
	/**
	 * Method getSpeed.
	 * @return int
	 */
	protected final int getSpeed()
	{
		return speed;
	}
	
	/**
	 * Method getStartTime.
	 * @return long
	 */
	protected final long getStartTime()
	{
		return startTime;
	}
	
	/**
	 * Method getStartX.
	 * @return float
	 */
	protected final float getStartX()
	{
		return startX;
	}
	
	/**
	 * Method getStartY.
	 * @return float
	 */
	protected final float getStartY()
	{
		return startY;
	}
	
	/**
	 * Method getStartZ.
	 * @return float
	 */
	protected final float getStartZ()
	{
		return startZ;
	}
	
	/**
	 * Method getSubId.
	 * @return int
	 * @see tera.gameserver.model.skillengine.shots.Shot#getSubId()
	 */
	@Override
	public int getSubId()
	{
		return Config.SERVER_SHOT_SUB_ID;
	}
	
	/**
	 * Method getTarget.
	 * @return Character
	 * @see tera.gameserver.model.skillengine.shots.Shot#getTarget()
	 */
	@Override
	public Character getTarget()
	{
		return target;
	}
	
	/**
	 * Method getTargetX.
	 * @return float
	 * @see tera.gameserver.model.skillengine.shots.Shot#getTargetX()
	 */
	@Override
	public float getTargetX()
	{
		return target == null ? 0 : target.getX();
	}
	
	/**
	 * Method getTargetY.
	 * @return float
	 * @see tera.gameserver.model.skillengine.shots.Shot#getTargetY()
	 */
	@Override
	public float getTargetY()
	{
		return target == null ? 0 : target.getY();
	}
	
	/**
	 * Method getTargetZ.
	 * @return float
	 * @see tera.gameserver.model.skillengine.shots.Shot#getTargetZ()
	 */
	@Override
	public float getTargetZ()
	{
		return target == null ? 0 : target.getZ();
	}
	
	/**
	 * Method getTask.
	 * @return ScheduledFuture<?>
	 */
	protected final ScheduledFuture<?> getTask()
	{
		return task;
	}
	
	/**
	 * Method isAuto.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.shots.Shot#isAuto()
	 */
	@Override
	public boolean isAuto()
	{
		return true;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public synchronized void run()
	{
		try
		{
			final Character target = getTarget();
			final Skill skill = getSkill();
			
			if ((target == null) || (skill == null))
			{
				log.warning(this, new Exception("not found target or skill"));
				stop();
				return;
			}
			
			final long now = System.currentTimeMillis();
			final float donedist = ((now - getStartTime()) * getSpeed()) / 1000F;
			final float startX = getStartX();
			final float startY = getStartY();
			final float startZ = getStartZ();
			final float alldist = target.getDistance(startX, startY, startZ);
			final float radius = getRadius();
			
			if (target.getDistance(startX, startY, startZ) <= (donedist + radius))
			{
				if (!target.isDead() && !target.isInvul() && !target.isEvasioned() && caster.checkTarget(target))
				{
					skill.applySkill(caster, target);
				}
				
				stop();
				return;
			}
			
			final float done = donedist / alldist;
			
			if (done >= 1F)
			{
				stop();
			}
		}
		catch (Exception e)
		{
			log.warning(this, e);
		}
	}
	
	/**
	 * Method setCaster.
	 * @param caster Character
	 */
	protected final void setCaster(Character caster)
	{
		this.caster = caster;
	}
	
	/**
	 * Method setRadius.
	 * @param radius int
	 */
	protected final void setRadius(int radius)
	{
		this.radius = radius;
	}
	
	/**
	 * Method setSkill.
	 * @param skill Skill
	 */
	protected final void setSkill(Skill skill)
	{
		this.skill = skill;
	}
	
	/**
	 * Method setSpeed.
	 * @param speed int
	 */
	protected final void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	/**
	 * Method setStartTime.
	 * @param startTime long
	 */
	protected final void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}
	
	/**
	 * Method setStartX.
	 * @param startX float
	 */
	protected final void setStartX(float startX)
	{
		this.startX = startX;
	}
	
	/**
	 * Method setStartY.
	 * @param startY float
	 */
	protected final void setStartY(float startY)
	{
		this.startY = startY;
	}
	
	/**
	 * Method setStartZ.
	 * @param startZ float
	 */
	protected final void setStartZ(float startZ)
	{
		this.startZ = startZ;
	}
	
	/**
	 * Method setTarget.
	 * @param target Character
	 */
	protected final void setTarget(Character target)
	{
		this.target = target;
	}
	
	/**
	 * Method setTask.
	 * @param task ScheduledFuture<?>
	 */
	protected final void setTask(ScheduledFuture<?> task)
	{
		this.task = task;
	}
	
	/**
	 * Method start.
	 * @see tera.gameserver.model.skillengine.shots.Shot#start()
	 */
	@Override
	public synchronized void start()
	{
		final Character caster = getCaster();
		
		if (caster == null)
		{
			log.warning(this, new Exception("not found caster"));
			return;
		}
		
		setStartX(caster.getX());
		setStartY(caster.getY());
		setStartZ((caster.getZ() + caster.getGeom().getHeight()) - 5F);
		final Skill skill = getSkill();
		
		if (skill == null)
		{
			log.warning(this, new Exception("not found skill"));
			return;
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		setSpeed(skill.getSpeed());
		setRadius(skill.getRadius());
		setStartTime(System.currentTimeMillis());
		setTask(executor.scheduleMoveAtFixedRate(this, 0, 100));
	}
	
	/**
	 * Method stop.
	 * @see tera.gameserver.model.skillengine.shots.Shot#stop()
	 */
	@Override
	public synchronized void stop()
	{
		if (task != null)
		{
			task.cancel(false);
			task = null;
		}
	}
}
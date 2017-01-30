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
import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.skillengine.Skill;

import rlib.geom.Geometry;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class AbstractShot implements Shot
{
	protected static final Logger log = Loggers.getLogger(Shot.class);
	
	protected final Array<Character> targets;
	
	protected ShotType type;
	
	protected Character caster;
	
	protected Skill skill;
	
	protected int speed;
	
	protected int radius;
	
	protected int count;
	
	protected long startTime;
	
	protected float startX;
	protected float startY;
	protected float startZ;
	
	protected float targetX;
	protected float targetY;
	protected float targetZ;
	
	protected float alldist;
	
	protected volatile ScheduledFuture<?> task;
	
	public AbstractShot()
	{
		targets = Arrays.toArray(Character.class);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		targets.clear();
		setCaster(null);
		setSkill(null);
	}
	
	/**
	 * Method getAlldist.
	 * @return float
	 */
	protected final float getAlldist()
	{
		return alldist;
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
	 * Method getCount.
	 * @return int
	 */
	protected final int getCount()
	{
		return count;
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
	protected final Skill getSkill()
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
		return null;
	}
	
	/**
	 * Method getTargets.
	 * @return Array<Character>
	 */
	protected final Array<Character> getTargets()
	{
		return targets;
	}
	
	/**
	 * Method getTargetX.
	 * @return float
	 * @see tera.gameserver.model.skillengine.shots.Shot#getTargetX()
	 */
	@Override
	public float getTargetX()
	{
		return targetX;
	}
	
	/**
	 * Method getTargetY.
	 * @return float
	 * @see tera.gameserver.model.skillengine.shots.Shot#getTargetY()
	 */
	@Override
	public float getTargetY()
	{
		return targetY;
	}
	
	/**
	 * Method getTargetZ.
	 * @return float
	 * @see tera.gameserver.model.skillengine.shots.Shot#getTargetZ()
	 */
	@Override
	public float getTargetZ()
	{
		return targetZ;
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
	 * Method getType.
	 * @return ShotType
	 * @see tera.gameserver.model.skillengine.shots.Shot#getType()
	 */
	@Override
	public ShotType getType()
	{
		return type;
	}
	
	/**
	 * Method isAuto.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.shots.Shot#isAuto()
	 */
	@Override
	public boolean isAuto()
	{
		return false;
	}
	
	/**
	 * Method prepare.
	 * @param caster Character
	 * @param skill Skill
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	protected void prepare(Character caster, Skill skill, float targetX, float targetY, float targetZ)
	{
		final float startX = caster.getX();
		final float startY = caster.getY();
		final float startZ = (caster.getZ() + caster.getGeomHeight()) - 5F;
		final float alldist = Geometry.getDistance(startX, startY, startZ, targetX, targetY, targetZ);
		final float range = skill.getRange();
		final float diff = range / alldist;
		
		if (diff > 1F)
		{
			targetX = startX + ((targetX - startX) * diff);
			targetY = startY + ((targetY - startY) * diff);
			targetZ = startZ + ((targetZ - startZ) * diff);
		}
		
		setAlldist(range);
		
		if (Config.DEVELOPER_MAIN_DEBUG)
		{
			caster.sendMessage("Shot: dist " + getAlldist());
		}
		
		setStartX(startX);
		setStartY(startY);
		setStartZ(startZ);
		setTargetX(targetX);
		setTargetY(targetY);
		setTargetZ(targetZ);
		final Array<Character> targets = getTargets();
		World.getAround(Character.class, targets, caster, skill.getRange() * 2);
		
		if (!targets.isEmpty())
		{
			final Character[] array = targets.array();
			
			for (int i = 0, length = targets.size(); i < length; i++)
			{
				final Character target = array[i];
				
				if (!caster.checkTarget(target))
				{
					targets.fastRemove(i--);
					length--;
					continue;
				}
			}
		}
		
		setCaster(caster);
		setSkill(skill);
		setSpeed(skill.getSpeed());
		setRadius(skill.getRadius());
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		count = 0;
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public synchronized void run()
	{
		final long now = System.currentTimeMillis();
		final float donedist = ((now - getStartTime()) * getSpeed()) / 1000F;
		final Array<Character> targets = getTargets();
		final float startX = getStartX();
		final float startY = getStartY();
		final float startZ = getStartZ();
		final float targetX = getTargetX();
		final float targetY = getTargetY();
		final float targetZ = getTargetZ();
		
		if (!targets.isEmpty())
		{
			final Character[] array = targets.array();
			
			for (int i = 0, length = targets.size(); i < length; i++)
			{
				final Character target = array[i];
				
				if ((target == null) || (target.getDistance(startX, startY, startZ) > (donedist + 100)))
				{
					continue;
				}
				
				targets.fastRemove(i--);
				length--;
				
				if (target.isDead() || target.isInvul() || target.isEvasioned())
				{
					continue;
				}
				
				if (target.isHit(startX, startY, startZ, targetX, targetY, targetZ, radius))
				{
					final Skill skill = getSkill();
					
					if (skill == null)
					{
						stop();
						return;
					}
					
					final AttackInfo info = skill.applySkill(getCaster(), target);
					count++;
					
					if (info.isBlocked() || (count >= skill.getMaxTargets()))
					{
						stop();
						return;
					}
				}
			}
		}
		
		if (donedist >= getAlldist())
		{
			stop();
		}
	}
	
	/**
	 * Method setAlldist.
	 * @param alldist float
	 */
	protected final void setAlldist(float alldist)
	{
		this.alldist = alldist;
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
	 * Method setCount.
	 * @param count int
	 */
	protected final void setCount(int count)
	{
		this.count = count;
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
	 * Method setTargetX.
	 * @param targetX float
	 */
	protected final void setTargetX(float targetX)
	{
		this.targetX = targetX;
	}
	
	/**
	 * Method setTargetY.
	 * @param targetY float
	 */
	protected final void setTargetY(float targetY)
	{
		this.targetY = targetY;
	}
	
	/**
	 * Method setTargetZ.
	 * @param targetZ float
	 */
	protected final void setTargetZ(float targetZ)
	{
		this.targetZ = targetZ;
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
	 * Method setType.
	 * @param type ShotType
	 */
	protected final void setType(ShotType type)
	{
		this.type = type;
	}
	
	/**
	 * Method start.
	 * @see tera.gameserver.model.skillengine.shots.Shot#start()
	 */
	@Override
	public synchronized void start()
	{
		final ExecutorManager executor = ExecutorManager.getInstance();
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

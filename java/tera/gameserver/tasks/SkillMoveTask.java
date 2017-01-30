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
import java.util.concurrent.locks.Lock;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.GeoManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.MoveSkill;
import tera.gameserver.network.serverpackets.SkillEnd;

import rlib.concurrent.Locks;
import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 12.03.2012
 */
public final class SkillMoveTask extends SafeTask
{
	private final Lock lock;
	private final Character caster;
	private final Array<Character> barriers;
	private Skill skill;
	private int counter;
	private float alldist;
	private float done;
	private float step;
	private float startX;
	private float startY;
	private float startZ;
	private float targetX;
	private float targetY;
	private float targetZ;
	private float radians;
	private boolean ignore;
	private volatile ScheduledFuture<SkillMoveTask> schedule;
	
	/**
	 * Constructor for SkillMoveTask.
	 * @param caster Character
	 */
	public SkillMoveTask(Character caster)
	{
		this.caster = caster;
		lock = Locks.newLock();
		barriers = Arrays.toArray(Character.class);
	}
	
	public void applySkill()
	{
		lock.lock();
		
		try
		{
			final Skill skill = getSkill();
			
			if (skill == null)
			{
				return;
			}
			
			caster.nextUse(skill);
			setSkill(null);
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method cancel.
	 * @param force boolean
	 */
	public void cancel(boolean force)
	{
		lock.lock();
		
		try
		{
			setSkill(null);
			
			if (schedule != null)
			{
				schedule.cancel(false);
				schedule = null;
			}
			
			caster.setSkillMoved(false);
			
			if (!barriers.isEmpty())
			{
				barriers.clear();
			}
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	public void done()
	{
		lock.lock();
		
		try
		{
			final Skill skill = getSkill();
			
			if ((skill != null) && skill.isCastToMove())
			{
				applySkill();
			}
			
			caster.setSkillMoved(false);
			
			if (schedule != null)
			{
				schedule.cancel(false);
				schedule = null;
			}
			
			if (!barriers.isEmpty())
			{
				barriers.clear();
			}
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method getAlldist.
	 * @return float
	 */
	public final float getAlldist()
	{
		return alldist;
	}
	
	/**
	 * Method getDone.
	 * @return float
	 */
	public final float getDone()
	{
		return done;
	}
	
	/**
	 * Method getRadians.
	 * @return float
	 */
	public final float getRadians()
	{
		return radians;
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
	 * Method getStartX.
	 * @return float
	 */
	public final float getStartX()
	{
		return startX;
	}
	
	/**
	 * Method getStartY.
	 * @return float
	 */
	public final float getStartY()
	{
		return startY;
	}
	
	/**
	 * Method getStartZ.
	 * @return float
	 */
	public final float getStartZ()
	{
		return startZ;
	}
	
	/**
	 * Method getTargetX.
	 * @return float
	 */
	public final float getTargetX()
	{
		return targetX;
	}
	
	/**
	 * Method getTargetY.
	 * @return float
	 */
	public final float getTargetY()
	{
		return targetY;
	}
	
	/**
	 * Method getTargetZ.
	 * @return float
	 */
	public final float getTargetZ()
	{
		return targetZ;
	}
	
	/**
	 * Method nextTask.
	 * @param skill Skill
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	public void nextTask(Skill skill, float targetX, float targetY, float targetZ)
	{
		cancel(false);
		final Formulas formulas = Formulas.getInstance();
		final int time = skill.isStaticCast() ? skill.getMoveTime() : formulas.castTime(skill.getMoveTime(), caster);
		final int moveDistance = skill.getMoveDistance();
		
		if ((moveDistance == 0) && !skill.isRush())
		{
			return;
		}
		
		lock.lock();
		
		try
		{
			setSkill(skill);
			setDone(0);
			setStartX(caster.getX());
			setStartY(caster.getY());
			setStartZ(caster.getZ());
			final GeoManager geoManager = GeoManager.getInstance();
			
			if (skill.isRush())
			{
				setAlldist(caster.getDistance(targetX, targetY, targetZ));
				
				if (getAlldist() > moveDistance)
				{
					setRadians(Angles.headingToRadians(caster.getHeading() + skill.getMoveHeading()));
					setAlldist(moveDistance);
					setTargetX(Coords.calcX(startX, moveDistance, radians));
					setTargetY(Coords.calcY(startY, moveDistance, radians));
					setTargetZ(geoManager.getHeight(caster.getContinentId(), getTargetX(), getTargetY(), getStartZ()));
				}
				else
				{
					setTargetX(targetX);
					setTargetY(targetY);
					setTargetZ(targetZ);
					setRadians(Angles.headingToRadians(caster.calcHeading(targetX, targetY) + skill.getMoveHeading()));
				}
			}
			else
			{
				setRadians(Angles.headingToRadians(caster.getHeading() + skill.getMoveHeading()));
				setAlldist(moveDistance);
				setTargetX(Coords.calcX(getStartX(), moveDistance, getRadians()));
				setTargetY(Coords.calcY(getStartY(), moveDistance, getRadians()));
				setTargetZ(geoManager.getHeight(caster.getContinentId(), getTargetX(), getTargetY(), getStartZ()));
			}
			
			ignore = skill.isIgnoreBarrier();
			counter = Math.round(Math.max(1, (int) Math.sqrt(Math.abs(alldist))));
			step = alldist / counter;
			caster.setSkillMoved(true);
			
			if (!ignore || skill.isCastToMove())
			{
				World.getAroundBarriers(barriers, caster, skill.getMoveDistance() * 3);
			}
			
			final ExecutorManager executor = ExecutorManager.getInstance();
			schedule = executor.scheduleSkillMoveAtFixedRate(this, skill.isStaticCast() ? skill.getMoveDelay() : formulas.castTime(skill.getMoveDelay(), caster), time / counter);
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method getCaster.
	 * @return Character
	 */
	public Character getCaster()
	{
		return caster;
	}
	
	@Override
	protected void runImpl()
	{
		if (counter < 1)
		{
			done();
			return;
		}
		
		final Character caster = getCaster();
		counter -= 1;
		final Skill skill = getSkill();
		
		if ((!ignore || ((skill != null) && skill.isCastToMove())) && !caster.checkBarriers(barriers, (int) step + 1, radians))
		{
			if (skill != null)
			{
				if (skill.isCastToMove())
				{
					applySkill();
				}
				
				if (skill.isRush())
				{
					caster.broadcastPacket(MoveSkill.getInstance(caster, caster));
				}
				
				if (caster.isBroadcastEndSkillForCollision())
				{
					caster.broadcastPacket(SkillEnd.getInstance(caster, 0, skill.getIconId()));
				}
				
				if (skill.isRush() || caster.isBroadcastEndSkillForCollision())
				{
					done();
				}
			}
			
			return;
		}
		
		done += step;
		final float result = done / alldist;
		
		if (result >= 1F)
		{
			done();
			return;
		}
		
		final float newX = startX + ((targetX - startX) * result);
		final float newY = startY + ((targetY - startY) * result);
		final float newZ = startZ + ((targetZ - startZ) * result);
		caster.setXYZ(newX, newY, newZ);
	}
	
	/**
	 * Method setAlldist.
	 * @param alldist float
	 */
	public final void setAlldist(float alldist)
	{
		this.alldist = alldist;
	}
	
	/**
	 * Method setDone.
	 * @param done float
	 */
	public final void setDone(float done)
	{
		this.done = done;
	}
	
	/**
	 * Method setRadians.
	 * @param radians float
	 */
	public final void setRadians(float radians)
	{
		this.radians = radians;
	}
	
	/**
	 * Method setSkill.
	 * @param skill Skill
	 */
	public void setSkill(Skill skill)
	{
		this.skill = skill;
	}
	
	/**
	 * Method setStartX.
	 * @param startX float
	 */
	public final void setStartX(float startX)
	{
		this.startX = startX;
	}
	
	/**
	 * Method setStartY.
	 * @param startY float
	 */
	public final void setStartY(float startY)
	{
		this.startY = startY;
	}
	
	/**
	 * Method setStartZ.
	 * @param startZ float
	 */
	public final void setStartZ(float startZ)
	{
		this.startZ = startZ;
	}
	
	/**
	 * Method setTargetX.
	 * @param targetX float
	 */
	public final void setTargetX(float targetX)
	{
		this.targetX = targetX;
	}
	
	/**
	 * Method setTargetY.
	 * @param targetY float
	 */
	public final void setTargetY(float targetY)
	{
		this.targetY = targetY;
	}
	
	/**
	 * Method setTargetZ.
	 * @param targetZ float
	 */
	public final void setTargetZ(float targetZ)
	{
		this.targetZ = targetZ;
	}
}
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
import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.Skill;

import rlib.concurrent.Locks;
import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public final class SkillUseTask extends SafeTask
{
	
	private final Lock lock;
	
	private final Character caster;
	
	private Skill skill;
	
	private float targetX;
	
	private float targetY;
	
	private float targetZ;
	
	private int count;
	
	private volatile ScheduledFuture<SkillUseTask> schedule;
	
	/**
	 * Constructor for SkillUseTask.
	 * @param caster Character
	 */
	public SkillUseTask(Character caster)
	{
		lock = Locks.newLock();
		this.caster = caster;
	}
	
	/**
	 * Method cancel.
	 * @param force boolean
	 */
	public void cancel(boolean force)
	{
		if (schedule != null)
		{
			lock.lock();
			
			try
			{
				if (schedule != null)
				{
					schedule.cancel(false);
					schedule = null;
				}
			}
			
			finally
			{
				lock.unlock();
			}
		}
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
	 * Method nextUse.
	 * @param skill Skill
	 */
	public void nextUse(Skill skill)
	{
		cancel(true);
		final Formulas formulas = Formulas.getInstance();
		lock.lock();
		
		try
		{
			this.skill = skill;
			count = skill.getCastCount();
			final ExecutorManager executor = ExecutorManager.getInstance();
			schedule = executor.scheduleSkillCast(this, skill.isStaticCast() ? skill.getDelay() : formulas.castTime(skill.getDelay(), caster));
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method nextUse.
	 * @param skill Skill
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	public void nextUse(Skill skill, float targetX, float targetY, float targetZ)
	{
		cancel(true);
		final Formulas formulas = Formulas.getInstance();
		lock.lock();
		
		try
		{
			this.skill = skill;
			count = skill.getCastCount();
			this.targetX = targetX;
			this.targetY = targetY;
			this.targetZ = targetZ;
			final ExecutorManager executor = ExecutorManager.getInstance();
			schedule = executor.scheduleSkillUse(this, skill.isStaticCast() ? skill.getDelay() : formulas.castTime(skill.getDelay(), caster));
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	@Override
	protected void runImpl()
	{
		count -= 1;
		final Skill skill = getSkill();
		
		if (skill == null)
		{
			return;
		}
		
		caster.setHeading(caster.getHeading() + skill.getCastHeading());
		skill.useSkill(caster, targetX, targetY, targetZ);
		
		if (count > 0)
		{
			final ExecutorManager executor = ExecutorManager.getInstance();
			final Formulas formulas = Formulas.getInstance();
			schedule = executor.scheduleSkillCast(this, skill.isStaticInterval() ? skill.getInterval() : formulas.castTime(skill.getInterval(), caster));
		}
	}
	
	/**
	 * Method setTarget.
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	public void setTarget(float targetX, float targetY, float targetZ)
	{
		cancel(true);
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
	}
}

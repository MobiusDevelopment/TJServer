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

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.Skill;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public final class SkillCastTask extends SafeTask
{
	private static final Logger log = Loggers.getLogger(SkillCastTask.class);
	
	private Skill skill;
	
	private final Character caster;
	
	private float targetX;
	
	private float targetY;
	
	private float targetZ;
	
	private volatile ScheduledFuture<SkillCastTask> schedule;
	
	/**
	 * Constructor for SkillCastTask.
	 * @param caster Character
	 */
	public SkillCastTask(Character caster)
	{
		this.caster = caster;
	}
	
	/**
	 * Method cancel.
	 * @param force boolean
	 */
	public synchronized void cancel(boolean force)
	{
		final Skill skill = getSkill();
		synchronized (this)
		{
			if (schedule != null)
			{
				schedule.cancel(false);
				schedule = null;
			}
			
			if (skill != null)
			{
				setSkill(null);
				caster.setCastingSkill(null);
			}
		}
		
		if (skill != null)
		{
			skill.endSkill(caster, targetX, targetY, targetZ, force);
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyFinishCasting(caster, skill);
		}
	}
	
	/**
	 * Method getSkill.
	 * @return Skill
	 */
	private final Skill getSkill()
	{
		return skill;
	}
	
	/**
	 * Method isRunning.
	 * @return boolean
	 */
	public boolean isRunning()
	{
		return schedule != null;
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
		cancel(true);
		synchronized (this)
		{
			caster.setCastingSkill(skill);
			this.skill = skill;
			this.targetX = targetX;
			this.targetY = targetY;
			this.targetZ = targetZ;
			final ExecutorManager executor = ExecutorManager.getInstance();
			final Formulas formulas = Formulas.getInstance();
			schedule = executor.scheduleSkillCast(this, skill.isStaticCast() ? skill.getHitTime() : formulas.castTime(skill.getHitTime(), caster));
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Skill skill = getSkill();
		
		if (skill == null)
		{
			log.warning(this, new Exception("not found skill"));
			return;
		}
		
		synchronized (this)
		{
			schedule = null;
			setSkill(null);
			caster.setCastingSkill(null);
		}
		skill.endSkill(caster, targetX, targetY, targetZ, false);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyFinishCasting(caster, skill);
	}
	
	/**
	 * Method setSkill.
	 * @param skill Skill
	 */
	private final void setSkill(Skill skill)
	{
		this.skill = skill;
	}
}

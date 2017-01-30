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
package tera.gameserver.model.skillengine.effects;

import tera.gameserver.model.Character;
import tera.gameserver.model.EffectList;
import tera.gameserver.model.Party;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.EffectState;
import tera.gameserver.model.skillengine.EffectType;
import tera.gameserver.model.skillengine.ResistType;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.network.serverpackets.AppledEffect;
import tera.gameserver.network.serverpackets.CancelEffect;
import tera.gameserver.taskmanager.EffectTaskManager;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class AbstractEffect implements Effect
{
	
	protected static final Logger LOGGER = Loggers.getLogger(Effect.class);
	
	protected final EffectTemplate template;
	
	protected final SkillTemplate skillTemplate;
	
	protected Func[] funcs;
	
	protected Character effector;
	
	protected Character effected;
	
	protected EffectList effectList;
	
	protected long startTime;
	
	protected int period;
	
	protected int count;
	
	protected boolean inUse;
	
	protected volatile EffectState state;
	
	/**
	 * Constructor for AbstractEffect.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skillTemplate SkillTemplate
	 */
	public AbstractEffect(EffectTemplate template, Character effector, Character effected, SkillTemplate skillTemplate)
	{
		this.effector = effector;
		this.effected = effected;
		this.skillTemplate = skillTemplate;
		state = EffectState.CREATED;
		funcs = template.getFuncs();
		this.template = template;
		period = template.getTime();
		count = template.getCount();
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Method exit.
	 * @see tera.gameserver.model.skillengine.Effect#exit()
	 */
	@Override
	public void exit()
	{
		final EffectList effectList = getEffectList();
		
		if (effectList == null)
		{
			LOGGER.warning(this, new Exception("not found effect list."));
			return;
		}
		
		effectList.lock();
		
		try
		{
			if (getState() == EffectState.FINISHED)
			{
				return;
			}
			
			setState(EffectState.FINISHING);
			scheduleEffect();
		}
		
		finally
		{
			effectList.unlock();
		}
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		effector = null;
		effected = null;
		effectList = null;
	}
	
	/**
	 * Method fold.
	 * @see tera.gameserver.model.skillengine.Effect#fold()
	 */
	@Override
	public void fold()
	{
		template.put(this);
	}
	
	/**
	 * Method getChance.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getChance()
	 */
	@Override
	public int getChance()
	{
		return template.getChance();
	}
	
	/**
	 * Method getCount.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getCount()
	 */
	@Override
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Method getEffected.
	 * @return Character
	 * @see tera.gameserver.model.skillengine.Effect#getEffected()
	 */
	@Override
	public Character getEffected()
	{
		return effected;
	}
	
	/**
	 * Method getEffectId.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getEffectId()
	 */
	@Override
	public int getEffectId()
	{
		return template.getId() < 0 ? skillTemplate.getId() : template.getId();
	}
	
	/**
	 * Method getEffectList.
	 * @return EffectList
	 * @see tera.gameserver.model.skillengine.Effect#getEffectList()
	 */
	@Override
	public EffectList getEffectList()
	{
		return effectList;
	}
	
	/**
	 * Method getEffector.
	 * @return Character
	 * @see tera.gameserver.model.skillengine.Effect#getEffector()
	 */
	@Override
	public Character getEffector()
	{
		return effector;
	}
	
	/**
	 * Method getEffectType.
	 * @return EffectType
	 * @see tera.gameserver.model.skillengine.Effect#getEffectType()
	 */
	@Override
	public EffectType getEffectType()
	{
		return template.getConstructor();
	}
	
	/**
	 * Method getFuncs.
	 * @return Func[]
	 * @see tera.gameserver.model.skillengine.Effect#getFuncs()
	 */
	@Override
	public Func[] getFuncs()
	{
		return funcs;
	}
	
	/**
	 * Method getOrder.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getOrder()
	 */
	@Override
	public int getOrder()
	{
		return Arrays.indexOf(skillTemplate.getEffectTemplates(), template);
	}
	
	/**
	 * Method getPeriod.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getPeriod()
	 */
	@Override
	public int getPeriod()
	{
		return period;
	}
	
	/**
	 * Method getResistType.
	 * @return ResistType
	 * @see tera.gameserver.model.skillengine.Effect#getResistType()
	 */
	@Override
	public ResistType getResistType()
	{
		return template.getResistType();
	}
	
	/**
	 * Method getSkillClassId.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getSkillClassId()
	 */
	@Override
	public int getSkillClassId()
	{
		return skillTemplate.getClassId();
	}
	
	/**
	 * Method getSkillId.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getSkillId()
	 */
	@Override
	public int getSkillId()
	{
		return skillTemplate.getId();
	}
	
	/**
	 * Method getSkillTemplate.
	 * @return SkillTemplate
	 * @see tera.gameserver.model.skillengine.Effect#getSkillTemplate()
	 */
	@Override
	public SkillTemplate getSkillTemplate()
	{
		return skillTemplate;
	}
	
	/**
	 * Method getStackType.
	 * @return String
	 * @see tera.gameserver.model.skillengine.Effect#getStackType()
	 */
	@Override
	public String getStackType()
	{
		return template.getStackType();
	}
	
	/**
	 * Method getStartTime.
	 * @return long
	 * @see tera.gameserver.model.skillengine.Effect#getStartTime()
	 */
	@Override
	public long getStartTime()
	{
		return startTime;
	}
	
	/**
	 * Method getState.
	 * @return EffectState
	 * @see tera.gameserver.model.skillengine.Effect#getState()
	 */
	@Override
	public EffectState getState()
	{
		return state;
	}
	
	/**
	 * Method getTemplate.
	 * @return EffectTemplate
	 * @see tera.gameserver.model.skillengine.Effect#getTemplate()
	 */
	@Override
	public EffectTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Method getTime.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getTime()
	 */
	@Override
	public int getTime()
	{
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}
	
	/**
	 * Method getTimeEnd.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getTimeEnd()
	 */
	@Override
	public int getTimeEnd()
	{
		return getTotalTime() - getTime();
	}
	
	/**
	 * Method getTimeForPacket.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getTimeForPacket()
	 */
	@Override
	public int getTimeForPacket()
	{
		return getTotalTime() * 1000;
	}
	
	/**
	 * Method getTotalTime.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getTotalTime()
	 */
	@Override
	public int getTotalTime()
	{
		return period * count;
	}
	
	/**
	 * Method getUsingCount.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Effect#getUsingCount()
	 */
	@Override
	public int getUsingCount()
	{
		return template.getCount() - count;
	}
	
	/**
	 * Method hasStackType.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#hasStackType()
	 */
	@Override
	public boolean hasStackType()
	{
		return !template.getStackType().isEmpty();
	}
	
	/**
	 * Method isAura.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isAura()
	 */
	@Override
	public boolean isAura()
	{
		return false;
	}
	
	/**
	 * Method isDebuff.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isDebuff()
	 */
	@Override
	public boolean isDebuff()
	{
		return template.isDebuff();
	}
	
	/**
	 * Method isEffect.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isEffect()
	 */
	@Override
	public boolean isEffect()
	{
		return true;
	}
	
	/**
	 * Method isEnded.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isEnded()
	 */
	@Override
	public boolean isEnded()
	{
		return (state == EffectState.FINISHED) || (state == EffectState.FINISHING);
	}
	
	/**
	 * Method isFinished.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isFinished()
	 */
	@Override
	public boolean isFinished()
	{
		return state == EffectState.FINISHED;
	}
	
	/**
	 * Method isInUse.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isInUse()
	 */
	@Override
	public boolean isInUse()
	{
		return inUse;
	}
	
	/**
	 * Method isNoAttack.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isNoAttack()
	 */
	@Override
	public boolean isNoAttack()
	{
		return template.isNoAttack();
	}
	
	/**
	 * Method isNoAttacked.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isNoAttacked()
	 */
	@Override
	public boolean isNoAttacked()
	{
		return template.isNoAttacked();
	}
	
	/**
	 * Method isNoOwerturn.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isNoOwerturn()
	 */
	@Override
	public boolean isNoOwerturn()
	{
		return template.isNoOwerturn();
	}
	
	/**
	 * Method onActionTime.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	/**
	 * Method onExit.
	 * @see tera.gameserver.model.skillengine.Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		final Character effected = getEffected();
		
		if (effected == null)
		{
			LOGGER.warning(this, new Exception("not found effected"));
			return;
		}
		
		final Func[] funcs = template.getFuncs();
		
		if (funcs.length < 1)
		{
			return;
		}
		
		for (Func func : funcs)
		{
			func.removeFuncTo(effected);
		}
		
		effected.updateInfo();
	}
	
	/**
	 * Method onStart.
	 * @see tera.gameserver.model.skillengine.Effect#onStart()
	 */
	@Override
	public void onStart()
	{
		final Character effected = getEffected();
		
		if (effected == null)
		{
			LOGGER.warning(this, new Exception("not found effected"));
			return;
		}
		
		final Func[] funcs = template.getFuncs();
		
		if (funcs.length < 1)
		{
			return;
		}
		
		for (Func func : funcs)
		{
			func.addFuncTo(effected);
		}
		
		effected.updateInfo();
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		state = EffectState.CREATED;
		period = template.getTime();
		count = template.getCount();
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Method scheduleEffect.
	 * @see tera.gameserver.model.skillengine.Effect#scheduleEffect()
	 */
	@Override
	public void scheduleEffect()
	{
		final Character effected = getEffected();
		final Character effector = getEffector();
		
		if (effected == null)
		{
			LOGGER.warning(this, new Exception("not found effected"));
			return;
		}
		
		if (effector == null)
		{
			LOGGER.warning(this, new Exception("not found effector"));
			return;
		}
		
		final EffectList effectList = getEffectList();
		
		if (effectList == null)
		{
			LOGGER.warning(this, new Exception("not found effect list."));
			return;
		}
		
		effectList.lock();
		
		try
		{
			switch (getState())
			{
				case CREATED:
				{
					onStart();
					setState(EffectState.ACTING);
					effected.broadcastPacket(AppledEffect.getInstance(effector, effected, this));
					final Party party = effected.getParty();
					
					if (party != null)
					{
						party.updateEffects(effected.getPlayer());
					}
					
					final EffectTaskManager effectManager = EffectTaskManager.getInstance();
					effectManager.addTask(this, period);
					break;
				}
				
				case ACTING:
				{
					if (count > 0)
					{
						count--;
						
						if (onActionTime() && (count > 0))
						{
							break;
						}
					}
					
					setState(EffectState.FINISHING);
				}
				
				case FINISHING:
				{
					setState(EffectState.FINISHED);
					setInUse(false);
					onExit();
					effected.removeEffect(this);
					effected.broadcastPacket(CancelEffect.getInstance(effected, getEffectId()));
					final Party party = effected.getParty();
					
					if (party != null)
					{
						party.updateEffects(effected.getPlayer());
					}
					
					break;
				}
				
				default:
					LOGGER.warning(this, new Exception("incorrect effect state " + state));
			}
		}
		
		finally
		{
			effectList.unlock();
		}
	}
	
	/**
	 * Method setCount.
	 * @param count int
	 * @see tera.gameserver.model.skillengine.Effect#setCount(int)
	 */
	@Override
	public void setCount(int count)
	{
		this.count = count;
	}
	
	/**
	 * Method setEffected.
	 * @param effected Character
	 * @see tera.gameserver.model.skillengine.Effect#setEffected(Character)
	 */
	@Override
	public void setEffected(Character effected)
	{
		this.effected = effected;
	}
	
	/**
	 * Method setEffectList.
	 * @param effectList EffectList
	 * @see tera.gameserver.model.skillengine.Effect#setEffectList(EffectList)
	 */
	@Override
	public void setEffectList(EffectList effectList)
	{
		this.effectList = effectList;
	}
	
	/**
	 * Method setEffector.
	 * @param effector Character
	 * @see tera.gameserver.model.skillengine.Effect#setEffector(Character)
	 */
	@Override
	public void setEffector(Character effector)
	{
		this.effector = effector;
	}
	
	/**
	 * Method setInUse.
	 * @param inUse boolean
	 * @see tera.gameserver.model.skillengine.Effect#setInUse(boolean)
	 */
	@Override
	public void setInUse(boolean inUse)
	{
		this.inUse = inUse;
	}
	
	/**
	 * Method setPeriod.
	 * @param period int
	 * @see tera.gameserver.model.skillengine.Effect#setPeriod(int)
	 */
	@Override
	public void setPeriod(int period)
	{
		this.period = period;
	}
	
	/**
	 * Method setStartTime.
	 * @param startTime long
	 * @see tera.gameserver.model.skillengine.Effect#setStartTime(long)
	 */
	@Override
	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}
	
	/**
	 * Method setState.
	 * @param state EffectState
	 * @see tera.gameserver.model.skillengine.Effect#setState(EffectState)
	 */
	@Override
	public void setState(EffectState state)
	{
		this.state = state;
	}
	
	/**
	 * Method isDynamicCount.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isDynamicCount()
	 */
	@Override
	public boolean isDynamicCount()
	{
		return template.isDynamicCount();
	}
	
	/**
	 * Method isDynamicTime.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Effect#isDynamicTime()
	 */
	@Override
	public boolean isDynamicTime()
	{
		return template.isDynamicTime();
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}

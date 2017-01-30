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

import java.util.concurrent.locks.Lock;

import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.templates.EffectTemplate;

import rlib.concurrent.Locks;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Synchronized;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.array.Search;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class EffectList implements Foldable, Synchronized
{
	private static final Logger log = Loggers.getLogger(EffectList.class);
	
	private static final FoldablePool<EffectList> pool = Pools.newConcurrentFoldablePool(EffectList.class);
	
	private static final Search<Effect> SEARCH_STACK_TYPE = (required, target) ->
	{
		if ((target == null) || (required == target))
		{
			return false;
		}
		
		return required.getStackType().equals(target.getStackType());
	};
	
	private static final Search<Effect> SEARCH_EQUALS_EFFECT = (required, target) ->
	{
		if (target == null)
		{
			return false;
		}
		
		return target.getTemplate() == required.getTemplate();
	};
	
	private static final Search<Effect> SEARCH_EQUALS_SKILL = (required, target) ->
	{
		if ((target == null) || (required == target))
		{
			return false;
		}
		
		return target.getSkillTemplate() == required.getSkillTemplate();
	};
	
	/**
	 * Method newInstance.
	 * @param owner Character
	 * @return EffectList
	 */
	public static EffectList newInstance(Character owner)
	{
		EffectList list = pool.take();
		
		if (list == null)
		{
			list = new EffectList(owner);
		}
		else
		{
			list.owner = owner;
		}
		
		return list;
	}
	
	private final Lock lock;
	
	private Character owner;
	
	private final Array<Effect> effects;
	
	private volatile int size;
	
	/**
	 * Constructor for EffectList.
	 * @param owner Character
	 */
	private EffectList(Character owner)
	{
		this.owner = owner;
		lock = Locks.newLock();
		effects = Arrays.toArray(Effect.class, 2);
	}
	
	/**
	 * Method addEffect.
	 * @param newEffect Effect
	 * @return boolean
	 */
	public boolean addEffect(Effect newEffect)
	{
		if (newEffect == null)
		{
			log.warning(new Exception("not found effect."));
			return false;
		}
		
		if (newEffect.getEffectList() != null)
		{
			log.warning("found effect list to " + newEffect);
			return false;
		}
		
		final Array<Effect> effects = getEffects();
		lock();
		
		try
		{
			if (effects.contains(newEffect))
			{
				return false;
			}
			
			Effect old = effects.search(newEffect, SEARCH_EQUALS_EFFECT);
			
			if (old != null)
			{
				if (newEffect.isAura())
				{
					old.exit();
					return false;
				}
				else if (newEffect.getTimeEnd() < old.getTimeEnd())
				{
					return false;
				}
				
				old.exit();
			}
			else if (newEffect.hasStackType())
			{
				old = effects.search(newEffect, SEARCH_STACK_TYPE);
				
				if (old != null)
				{
					if (newEffect.isAura() && (newEffect.getSkillTemplate() == old.getSkillTemplate()))
					{
						old.exit();
						return false;
					}
					else if (newEffect.getTimeEnd() < old.getTimeEnd())
					{
						return false;
					}
					
					old.exit();
				}
			}
			
			old = effects.search(newEffect, SEARCH_EQUALS_SKILL);
			
			if (old == null)
			{
				increaseSize();
			}
			
			effects.add(newEffect);
			newEffect.setEffectList(this);
			newEffect.setInUse(true);
			newEffect.scheduleEffect();
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	public void clear()
	{
		if (effects.isEmpty())
		{
			return;
		}
		
		lock();
		
		try
		{
			final Effect[] array = effects.array();
			
			for (int i = 0, length = effects.size(); i < length; i++)
			{
				final Effect effect = array[i];
				
				if (effect == null)
				{
					continue;
				}
				
				effect.exit();
				i--;
				length--;
			}
			
			effects.clear();
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method contains.
	 * @param effect Effect
	 * @return boolean
	 */
	public boolean contains(Effect effect)
	{
		if (effect == null)
		{
			log.warning(new Exception("check contains null effect"));
			return false;
		}
		
		lock();
		
		try
		{
			return effects.search(effect, SEARCH_EQUALS_EFFECT) != null;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method contains.
	 * @param skill Skill
	 * @return boolean
	 */
	public boolean contains(Skill skill)
	{
		final EffectTemplate[] temps = skill.getEffectTemplates();
		final Array<Effect> effects = getEffects();
		lock();
		
		try
		{
			final Effect[] array = effects.array();
			
			for (int i = 0, length = effects.size(); i < length; i++)
			{
				if (Arrays.contains(temps, array[i].getTemplate()))
				{
					return true;
				}
			}
			
			return false;
		}
		
		finally
		{
			unlock();
		}
	}
	
	protected void decreaseSize()
	{
		size -= 1;
	}
	
	public void exitNoAttackedEffects()
	{
		if (effects.isEmpty())
		{
			return;
		}
		
		lock();
		
		try
		{
			final Effect[] array = effects.array();
			
			for (int i = 0, length = effects.size(); i < length; i++)
			{
				final Effect effect = array[i];
				
				if ((effect != null) && effect.isNoAttacked())
				{
					effect.exit();
					i--;
					length--;
				}
			}
		}
		
		finally
		{
			unlock();
		}
	}
	
	public void exitNoAttackEffects()
	{
		if (effects.isEmpty())
		{
			return;
		}
		
		lock();
		
		try
		{
			final Effect[] array = effects.array();
			
			for (int i = 0, length = effects.size(); i < length; i++)
			{
				final Effect effect = array[i];
				
				if ((effect != null) && effect.isNoAttack())
				{
					effect.exit();
					i--;
					length--;
				}
			}
		}
		
		finally
		{
			unlock();
		}
	}
	
	public void exitNoOwerturnEffects()
	{
		if (effects.isEmpty())
		{
			return;
		}
		
		lock();
		
		try
		{
			final Effect[] array = effects.array();
			
			for (int i = 0, length = effects.size(); i < length; i++)
			{
				final Effect effect = array[i];
				
				if ((effect != null) && effect.isNoOwerturn())
				{
					effect.exit();
					i--;
					length--;
				}
			}
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		owner = null;
	}
	
	public void fold()
	{
		clear();
		pool.put(this);
	}
	
	/**
	 * Method getArrayEffects.
	 * @return Effect[]
	 */
	public Effect[] getArrayEffects()
	{
		return effects.array();
	}
	
	/**
	 * Method getEffects.
	 * @return Array<Effect>
	 */
	public Array<Effect> getEffects()
	{
		return effects;
	}
	
	/**
	 * Method getOwner.
	 * @return Character
	 */
	public Character getOwner()
	{
		return owner;
	}
	
	protected void increaseSize()
	{
		size += 1;
	}
	
	/**
	 * Method lock.
	 * @see rlib.util.Synchronized#lock()
	 */
	@Override
	public void lock()
	{
		lock.lock();
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		size = 0;
	}
	
	/**
	 * Method removeEffect.
	 * @param effect Effect
	 */
	public void removeEffect(Effect effect)
	{
		lock.lock();
		
		try
		{
			if ((effect == null) || !effects.fastRemove(effect))
			{
				log.warning(new Exception("incorrect remove effect"));
				return;
			}
			
			final Effect equals = effects.search(effect, SEARCH_EQUALS_SKILL);
			
			if (equals == null)
			{
				decreaseSize();
			}
			
			final Character owner = getOwner();
			
			if (owner == null)
			{
				log.warning(new Exception("not found owner"));
				return;
			}
			
			owner.updateInfo();
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	public int size()
	{
		return size;
	}
	
	/**
	 * Method unlock.
	 * @see rlib.util.Synchronized#unlock()
	 */
	@Override
	public void unlock()
	{
		lock.unlock();
	}
}

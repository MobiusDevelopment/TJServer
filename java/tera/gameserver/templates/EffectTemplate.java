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
package tera.gameserver.templates;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.EffectType;
import tera.gameserver.model.skillengine.ResistType;
import tera.gameserver.model.skillengine.funcs.Func;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Strings;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class EffectTemplate
{
	private static final Logger log = Loggers.getLogger(EffectTemplate.class);
	
	public static final EffectTemplate[] EMPTY_TEMPLATES = new EffectTemplate[0];
	
	/**
	 * Method getEmptyTemplates.
	 * @return EffectTemplate[]
	 */
	public static final EffectTemplate[] getEmptyTemplates()
	{
		return EMPTY_TEMPLATES;
	}
	
	protected final FoldablePool<Effect> effectPool;
	
	protected final Array<Effect> activeEffect;
	
	private final int count;
	
	private final int time;
	
	private final int power;
	
	private final int id;
	
	private final int chance;
	
	private final int limit;
	
	private final float value;
	
	private boolean debuff;
	
	private final boolean onCaster;
	
	private final boolean noAttack;
	
	private final boolean noAttacked;
	
	private final boolean noOwerturn;
	
	private final boolean dynamicTime;
	
	private final boolean dynamicCount;
	
	private final EffectType constructor;
	
	private final ResistType resistType;
	
	private final Func[] funcs;
	
	private final String stackType;
	
	private final String options;
	
	/**
	 * Constructor for EffectTemplate.
	 * @param vars VarTable
	 * @param funcs Func[]
	 */
	public EffectTemplate(VarTable vars, Func[] funcs)
	{
		count = vars.getInteger("count", 1);
		time = vars.getInteger("time", 0);
		power = vars.getInteger("power", 0);
		id = vars.getInteger("id", -1);
		chance = vars.getInteger("chance", -1);
		limit = vars.getInteger("limit", -1);
		value = vars.getFloat("value", 0F);
		debuff = vars.getBoolean("debuff", false);
		onCaster = vars.getBoolean("onCaster", false);
		noAttack = vars.getBoolean("noAttack", false);
		noAttacked = vars.getBoolean("noAttacked", false);
		noOwerturn = vars.getBoolean("noOwerturn", false);
		dynamicCount = vars.getBoolean("dynamicCount", false);
		dynamicTime = vars.getBoolean("dynamicTime", false);
		stackType = vars.getString("stackType", Strings.EMPTY);
		options = vars.getString("options", Strings.EMPTY);
		effectPool = Pools.newConcurrentFoldablePool(Effect.class);
		activeEffect = Arrays.toConcurrentArray(Effect.class);
		constructor = vars.getEnum("type", EffectType.class);
		resistType = vars.getEnum("resistType", ResistType.class, ResistType.noneResist);
		this.funcs = funcs;
	}
	
	/**
	 * Method getChance.
	 * @return int
	 */
	public final int getChance()
	{
		return chance;
	}
	
	/**
	 * Method getConstructor.
	 * @return EffectType
	 */
	public final EffectType getConstructor()
	{
		return constructor;
	}
	
	/**
	 * Method getCount.
	 * @return int
	 */
	public final int getCount()
	{
		return count;
	}
	
	/**
	 * Method getFuncs.
	 * @return Func[]
	 */
	public final Func[] getFuncs()
	{
		return funcs;
	}
	
	/**
	 * Method getId.
	 * @return int
	 */
	public final int getId()
	{
		return id;
	}
	
	/**
	 * Method getLimit.
	 * @return int
	 */
	public final int getLimit()
	{
		return limit;
	}
	
	/**
	 * Method getOptions.
	 * @return String
	 */
	public final String getOptions()
	{
		return options;
	}
	
	/**
	 * Method getPower.
	 * @return int
	 */
	public final int getPower()
	{
		return power;
	}
	
	/**
	 * Method getResistType.
	 * @return ResistType
	 */
	public final ResistType getResistType()
	{
		return resistType;
	}
	
	/**
	 * Method getStackType.
	 * @return String
	 */
	public final String getStackType()
	{
		return stackType;
	}
	
	/**
	 * Method getTime.
	 * @return int
	 */
	public final int getTime()
	{
		return time;
	}
	
	/**
	 * Method getValue.
	 * @return float
	 */
	public final float getValue()
	{
		return value;
	}
	
	/**
	 * Method isDebuff.
	 * @return boolean
	 */
	public final boolean isDebuff()
	{
		return debuff;
	}
	
	/**
	 * Method isNoAttack.
	 * @return boolean
	 */
	public boolean isNoAttack()
	{
		return noAttack;
	}
	
	/**
	 * Method isNoAttacked.
	 * @return boolean
	 */
	public boolean isNoAttacked()
	{
		return noAttacked;
	}
	
	/**
	 * Method isNoOwerturn.
	 * @return boolean
	 */
	public boolean isNoOwerturn()
	{
		return noOwerturn;
	}
	
	/**
	 * Method isOnCaster.
	 * @return boolean
	 */
	public final boolean isOnCaster()
	{
		return onCaster;
	}
	
	/**
	 * Method newInstance.
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 * @return Effect
	 */
	public final Effect newInstance(Character effector, Character effected, SkillTemplate skill)
	{
		final Effect effect = effectPool.take();
		
		if (effect == null)
		{
			return constructor.newInstance(this, effector, effected, skill);
		}
		
		if (activeEffect.contains(effect))
		{
			log.warning(new Exception("found duplicate active effect"));
			return constructor.newInstance(this, effector, effected, skill);
		}
		
		activeEffect.add(effect);
		effect.setEffector(effector);
		effect.setEffected(effected);
		return effect;
	}
	
	/**
	 * Method put.
	 * @param effect Effect
	 */
	public void put(Effect effect)
	{
		activeEffect.fastRemove(effect);
		effectPool.put(effect);
	}
	
	/**
	 * Method setDebuff.
	 * @param isDebuff boolean
	 */
	public void setDebuff(boolean isDebuff)
	{
		debuff = isDebuff;
	}
	
	/**
	 * Method isDynamicCount.
	 * @return boolean
	 */
	public boolean isDynamicCount()
	{
		return dynamicCount;
	}
	
	/**
	 * Method isDynamicTime.
	 * @return boolean
	 */
	public boolean isDynamicTime()
	{
		return dynamicTime;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "EffectTemplate effectPool = " + effectPool + ", count = " + count + ", time = " + time + ", power = " + power + ", id = " + id + ", chance = " + chance + ", debuff = " + debuff + ", constructor = " + constructor + ", funcs = " + Arrays.toString(funcs) + ", stackType = " + stackType;
	}
}

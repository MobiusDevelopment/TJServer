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
import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.OperateType;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillGroup;
import tera.gameserver.model.skillengine.SkillName;
import tera.gameserver.model.skillengine.SkillRangeType;
import tera.gameserver.model.skillengine.SkillType;
import tera.gameserver.model.skillengine.TargetType;
import tera.gameserver.model.skillengine.funcs.Func;

import rlib.util.Objects;
import rlib.util.Reloadable;
import rlib.util.Strings;
import rlib.util.VarTable;
import rlib.util.array.Arrays;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class SkillTemplate implements Reloadable<SkillTemplate>
{
	public static final Skill[] EMPTY_SKILLS = new Skill[0];
	
	private final int id;
	
	private final int iconId;
	
	private final int damageId;
	
	private final int offsetId;
	
	private final int reuseDelay;
	
	private final int reuseId;
	
	private final int delay;
	
	private final int moveDelay;
	
	private final int itemId;
	
	private final int itemCount;
	
	private final int chance;
	
	private final int transformId;
	
	private final int mpConsume;
	
	private final int hpConsume;
	
	private final int itemIdConsume;
	
	private final int itemCountConsume;
	
	private final int minRange;
	
	private final int hitTime;
	
	private final int moveTime;
	
	private final int speed;
	
	private final int speedOffset;
	
	private final int moveDistance;
	
	private final int level;
	
	private final int classId;
	
	private final int castCount;
	
	private final int maxTargets;
	
	private final int lifeTime;
	
	private final int startState;
	
	private final int endState;
	
	private final int summonId;
	
	private final int summonType;
	
	private final int stateMod;
	
	private final int mountId;
	
	private final int aggroPoint;
	
	private final int castMaxRange;
	
	private final int castMinRange;
	
	private final int moveHeding;
	
	private final float owerturnMod;
	
	private final float regenPower;
	
	private final float startPower;
	
	private final float chargeMod;
	
	private final boolean shieldIgnore;
	
	private final boolean canOwerturn;
	
	private final boolean visibleOnSkillList;
	
	private final boolean ignoreBarrier;
	
	private final boolean castToMove;
	
	private final boolean implemented;
	
	private boolean trigger;
	
	private final boolean toggle;
	
	private final boolean blockingMove;
	
	private final boolean altCast;
	
	private final boolean forceCast;
	
	private final boolean evasion;
	
	private final boolean noCaster;
	
	private final boolean rush;
	
	private final boolean hasFast;
	
	private boolean shortSkill;
	
	private final boolean staticCast;
	
	private final boolean staticInterval;
	
	private final boolean staticReuseDelay;
	
	private final boolean correctableTarget;
	
	private final String name;
	
	private final String group;
	
	private final SkillName skillName;
	
	private final SkillGroup skillGroup;
	
	private final SkillType skillType;
	
	private OperateType operateType = null;
	
	private final SkillRangeType rangeType;
	
	private final TargetType[] targetType;
	
	private final SkillName[] prevSkillNames;
	
	private final EffectTemplate[] effectTemplates;
	
	private final Condition condition;
	
	private final Func[] passiveFuncs;
	private final Func[] castFuncs;
	
	private final int[] interval;
	
	private final int[] power;
	
	private final int[] range;
	
	private final int[] width;
	
	private final int[] degree;
	
	private final int[] radius;
	
	private int[] reuseIds;
	
	private final int[] heading;
	
	private final int[] castHeading;
	
	private final int[] stage;
	
	private final boolean[] apply;
	
	private final VarTable vars;
	
	private final FoldablePool<Skill> pool;
	
	/**
	 * Constructor for SkillTemplate.
	 * @param vars VarTable
	 * @param effectTemplates EffectTemplate[]
	 * @param condition Condition
	 * @param passiveFuncs Func[]
	 * @param castFuncs Func[]
	 */
	public SkillTemplate(VarTable vars, EffectTemplate[] effectTemplates, Condition condition, Func[] passiveFuncs, Func[] castFuncs)
	{
		id = vars.getInteger("id", 1);
		iconId = vars.getInteger("iconId", id);
		level = vars.getInteger("level", 1);
		mpConsume = vars.getInteger("mpConsume", 0);
		hpConsume = vars.getInteger("hpConsume", 0);
		itemIdConsume = vars.getInteger("itemIdConsume", 0);
		itemCountConsume = vars.getInteger("itemCountConsume", 1);
		hitTime = vars.getInteger("hitTime", 0);
		reuseDelay = vars.getInteger("reuseDelay", 1);
		classId = vars.getInteger("classId");
		moveDistance = vars.getInteger("moveDistance", 0);
		delay = vars.getInteger("delay", 0);
		castCount = vars.getInteger("castCount", 1);
		speed = vars.getInteger("speed", 1);
		speedOffset = vars.getInteger("speedOffset", 0);
		itemId = vars.getInteger("itemId", 0);
		itemCount = vars.getInteger("itemCount", 0);
		damageId = vars.getInteger("damageId", iconId);
		chance = vars.getInteger("chance", 0);
		moveDelay = vars.getInteger("moveDelay", 0);
		moveTime = vars.getInteger("moveTime", hitTime);
		transformId = vars.getInteger("transformId", id);
		minRange = vars.getInteger("minRange", 0);
		maxTargets = vars.getInteger("maxTargets", -1);
		lifeTime = vars.getInteger("lifeTime", 0);
		reuseId = vars.getInteger("reuseId", id);
		offsetId = vars.getInteger("offsetId", 0);
		startState = vars.getInteger("startState", 0);
		endState = vars.getInteger("endState", startState);
		summonId = vars.getInteger("summonId", 0);
		summonType = vars.getInteger("summonType", 0);
		stateMod = vars.getInteger("stateMod", 1);
		mountId = vars.getInteger("mountId", 0);
		aggroPoint = vars.getInteger("aggroPoint", 0);
		castMaxRange = vars.getInteger("castMaxRange", getMoveDistance());
		castMinRange = vars.getInteger("castMinRange", 0);
		regenPower = vars.getFloat("regenPower", 1F);
		owerturnMod = vars.getFloat("owerturnMod", 1F);
		startPower = vars.getFloat("startPower", 1F);
		chargeMod = vars.getFloat("chargeMod", 0.1F);
		name = vars.getString("name", "none");
		group = vars.getString("group", Strings.EMPTY);
		shieldIgnore = vars.getBoolean("shieldIgnore", false);
		canOwerturn = vars.getBoolean("canOwerturn", true);
		visibleOnSkillList = vars.getBoolean("visibleOnSkillList", true);
		ignoreBarrier = vars.getBoolean("ignoreBarrier", false);
		implemented = vars.getBoolean("implemented", false);
		castToMove = vars.getBoolean("castToMove", false);
		blockingMove = vars.getBoolean("blockingMove", true);
		altCast = vars.getBoolean("altCast", false);
		forceCast = vars.getBoolean("forceCast", false);
		evasion = vars.getBoolean("evasion", false);
		noCaster = vars.getBoolean("noCaster", false);
		rush = vars.getBoolean("rush", false);
		hasFast = vars.getBoolean("hasFast", false);
		correctableTarget = vars.getBoolean("correctableTarget", false);
		shortSkill = vars.getBoolean("shortSkill", false);
		staticCast = vars.getBoolean("staticCast", false);
		staticInterval = vars.getBoolean("staticInterval", staticCast);
		staticReuseDelay = vars.getBoolean("staticReuseDelay", (operateType == OperateType.CAST_ITEM) || (operateType == OperateType.NO_CAST_ITEM));
		skillType = SkillType.valueOf(vars.getString("skillType", "DEFAULT"));
		skillName = SkillName.valueOf(vars.getString("skillName", "UNKNOWN"));
		operateType = OperateType.valueOf(vars.getString("operateType", "ACTIVE"));
		skillGroup = vars.getEnum("skillGroup", SkillGroup.class, SkillGroup.NONE);
		rangeType = vars.getEnum("rangeType", SkillRangeType.class, SkillRangeType.SHORT_SKILL);
		power = vars.getIntegerArray("power", ";", 0);
		interval = vars.getIntegerArray("interval", ";", 0);
		range = vars.getIntegerArray("range", ";", 0);
		radius = vars.getIntegerArray("radius", ";", 0);
		width = vars.getIntegerArray("width", ";", 0);
		degree = vars.getIntegerArray("degree", ";", 0);
		heading = vars.getIntegerArray("heading", ";", 0);
		stage = vars.getIntegerArray("stage", ";", -1);
		castHeading = vars.getIntegerArray("castHeading", ";", 0);
		moveHeding = vars.getInteger("moveHeading", heading[0]);
		apply = vars.getBooleanArray("apply", ";", true);
		targetType = vars.getEnumArray("targetType", TargetType.class, ";", TargetType.TARGET_NONE);
		prevSkillNames = vars.getEnumArray("prevSkillNames", SkillName.class, ";", SkillName.UNKNOWN);
		this.effectTemplates = effectTemplates;
		this.condition = condition;
		this.passiveFuncs = passiveFuncs;
		this.castFuncs = castFuncs;
		final String line = vars.getString("reuseIds", Strings.EMPTY);
		
		if ((line != Strings.EMPTY) && (line.length() > 1))
		{
			final String[] ids = line.split(",");
			reuseIds = new int[ids.length];
			
			for (int i = 0; i < ids.length; i++)
			{
				reuseIds[i] = Integer.parseInt(ids[i]);
			}
		}
		
		toggle = (skillType == SkillType.DEFENSE) || (skillType == SkillType.MOUNT);
		pool = Pools.newConcurrentFoldablePool(Skill.class);
		this.vars = vars;
		
		if (!shortSkill)
		{
			shortSkill = range[0] < 200;
		}
	}
	
	/**
	 * Method addCastFuncs.
	 * @param owner Character
	 */
	public void addCastFuncs(Character owner)
	{
		if (castFuncs.length < 1)
		{
			return;
		}
		
		for (Func castFunc : castFuncs)
		{
			castFunc.addFuncTo(owner);
		}
		
		owner.updateInfo();
	}
	
	/**
	 * Method addPassiveFuncs.
	 * @param owner Character
	 */
	public void addPassiveFuncs(Character owner)
	{
		if (passiveFuncs.length < 1)
		{
			return;
		}
		
		for (Func passiveFunc : passiveFuncs)
		{
			passiveFunc.addFuncTo(owner);
		}
	}
	
	/**
	 * Method getAggroPoint.
	 * @return int
	 */
	public final int getAggroPoint()
	{
		return aggroPoint;
	}
	
	/**
	 * Method getCastCount.
	 * @return int
	 */
	public final int getCastCount()
	{
		return castCount;
	}
	
	/**
	 * Method getCastHeading.
	 * @return int[]
	 */
	public final int[] getCastHeading()
	{
		return castHeading;
	}
	
	/**
	 * Method getCastMaxRange.
	 * @return int
	 */
	public final int getCastMaxRange()
	{
		return castMaxRange;
	}
	
	/**
	 * Method getCastMinRange.
	 * @return int
	 */
	public final int getCastMinRange()
	{
		return castMinRange;
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
	 * Method getChargeMod.
	 * @return float
	 */
	public final float getChargeMod()
	{
		return chargeMod;
	}
	
	/**
	 * Method getClassId.
	 * @return int
	 */
	public final int getClassId()
	{
		return classId;
	}
	
	/**
	 * Method getCondition.
	 * @return Condition
	 */
	public final Condition getCondition()
	{
		return condition;
	}
	
	/**
	 * Method getDamageId.
	 * @return int
	 */
	public final int getDamageId()
	{
		return damageId;
	}
	
	/**
	 * Method getDegree.
	 * @return int[]
	 */
	public final int[] getDegree()
	{
		return degree;
	}
	
	/**
	 * Method getDelay.
	 * @return int
	 */
	public final int getDelay()
	{
		return delay;
	}
	
	/**
	 * Method getEffectTemplates.
	 * @return EffectTemplate[]
	 */
	public final EffectTemplate[] getEffectTemplates()
	{
		return effectTemplates;
	}
	
	/**
	 * Method getEndState.
	 * @return int
	 */
	public final int getEndState()
	{
		return endState;
	}
	
	/**
	 * Method getGroup.
	 * @return String
	 */
	public final String getGroup()
	{
		return group;
	}
	
	/**
	 * Method getHeading.
	 * @return int[]
	 */
	public final int[] getHeading()
	{
		return heading;
	}
	
	/**
	 * Method getHitTime.
	 * @return int
	 */
	public final int getHitTime()
	{
		return hitTime;
	}
	
	/**
	 * Method getHpConsume.
	 * @return int
	 */
	public final int getHpConsume()
	{
		return hpConsume;
	}
	
	/**
	 * Method getIconId.
	 * @return int
	 */
	public final int getIconId()
	{
		return iconId;
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
	 * Method getInterval.
	 * @return int[]
	 */
	public final int[] getInterval()
	{
		return interval;
	}
	
	/**
	 * Method getItemCount.
	 * @return int
	 */
	public final int getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * Method getItemCountConsume.
	 * @return int
	 */
	public final int getItemCountConsume()
	{
		return itemCountConsume;
	}
	
	/**
	 * Method getItemId.
	 * @return int
	 */
	public final int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Method getItemIdConsume.
	 * @return int
	 */
	public final int getItemIdConsume()
	{
		return itemIdConsume;
	}
	
	/**
	 * Method getLevel.
	 * @return int
	 */
	public final int getLevel()
	{
		return level;
	}
	
	/**
	 * Method getLifeTime.
	 * @return int
	 */
	public final int getLifeTime()
	{
		return lifeTime;
	}
	
	/**
	 * Method getMaxTargets.
	 * @return int
	 */
	public final int getMaxTargets()
	{
		return maxTargets;
	}
	
	/**
	 * Method getMinRange.
	 * @return int
	 */
	public final int getMinRange()
	{
		return minRange;
	}
	
	/**
	 * Method getMountId.
	 * @return int
	 */
	public final int getMountId()
	{
		return mountId;
	}
	
	/**
	 * Method getMoveDelay.
	 * @return int
	 */
	public final int getMoveDelay()
	{
		return moveDelay;
	}
	
	/**
	 * Method getMoveDistance.
	 * @return int
	 */
	public final int getMoveDistance()
	{
		return moveDistance;
	}
	
	/**
	 * Method getMoveHeding.
	 * @return int
	 */
	public final int getMoveHeding()
	{
		return moveHeding;
	}
	
	/**
	 * Method getMoveTime.
	 * @return int
	 */
	public final int getMoveTime()
	{
		return moveTime;
	}
	
	/**
	 * Method getMpConsume.
	 * @return int
	 */
	public final int getMpConsume()
	{
		return mpConsume;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getOffsetId.
	 * @return int
	 */
	public final int getOffsetId()
	{
		return offsetId;
	}
	
	/**
	 * Method getOperateType.
	 * @return OperateType
	 */
	public final OperateType getOperateType()
	{
		return operateType;
	}
	
	/**
	 * Method getOwerturnMod.
	 * @return float
	 */
	public final float getOwerturnMod()
	{
		return owerturnMod;
	}
	
	/**
	 * Method getPower.
	 * @return int[]
	 */
	public final int[] getPower()
	{
		return power;
	}
	
	/**
	 * Method getPrevSkillNames.
	 * @return SkillName[]
	 */
	public final SkillName[] getPrevSkillNames()
	{
		return prevSkillNames;
	}
	
	/**
	 * Method getRadius.
	 * @return int[]
	 */
	public final int[] getRadius()
	{
		return radius;
	}
	
	/**
	 * Method getRange.
	 * @return int[]
	 */
	public final int[] getRange()
	{
		return range;
	}
	
	/**
	 * Method getRangeType.
	 * @return SkillRangeType
	 */
	public final SkillRangeType getRangeType()
	{
		return rangeType;
	}
	
	/**
	 * Method getRegenPower.
	 * @return float
	 */
	public final float getRegenPower()
	{
		return regenPower;
	}
	
	/**
	 * Method getReuseDelay.
	 * @return int
	 */
	public final int getReuseDelay()
	{
		return reuseDelay;
	}
	
	/**
	 * Method getReuseId.
	 * @return int
	 */
	public final int getReuseId()
	{
		return reuseId;
	}
	
	/**
	 * Method getReuseIds.
	 * @return int[]
	 */
	public final int[] getReuseIds()
	{
		return reuseIds;
	}
	
	/**
	 * Method getSkillGroup.
	 * @return SkillGroup
	 */
	public final SkillGroup getSkillGroup()
	{
		return skillGroup;
	}
	
	/**
	 * Method getSkillName.
	 * @return SkillName
	 */
	public final SkillName getSkillName()
	{
		return skillName;
	}
	
	/**
	 * Method getSkillType.
	 * @return SkillType
	 */
	public final SkillType getSkillType()
	{
		return skillType;
	}
	
	/**
	 * Method getSpeed.
	 * @return int
	 */
	public final int getSpeed()
	{
		return speed;
	}
	
	/**
	 * Method getStage.
	 * @return int[]
	 */
	public final int[] getStage()
	{
		return stage;
	}
	
	/**
	 * Method getStartPower.
	 * @return float
	 */
	public final float getStartPower()
	{
		return startPower;
	}
	
	/**
	 * Method getStartState.
	 * @return int
	 */
	public final int getStartState()
	{
		return startState;
	}
	
	/**
	 * Method getStateMod.
	 * @return int
	 */
	public int getStateMod()
	{
		return stateMod;
	}
	
	/**
	 * Method getSummonId.
	 * @return int
	 */
	public int getSummonId()
	{
		return summonId;
	}
	
	/**
	 * Method getSummonType.
	 * @return int
	 */
	public int getSummonType()
	{
		return summonType;
	}
	
	/**
	 * Method getTargetType.
	 * @return TargetType[]
	 */
	public final TargetType[] getTargetType()
	{
		return targetType;
	}
	
	/**
	 * Method getTransformId.
	 * @return int
	 */
	public final int getTransformId()
	{
		return transformId;
	}
	
	/**
	 * Method getVars.
	 * @return VarTable
	 */
	public final VarTable getVars()
	{
		return vars;
	}
	
	/**
	 * Method getWidth.
	 * @return int[]
	 */
	public final int[] getWidth()
	{
		return width;
	}
	
	/**
	 * Method hashCode.
	 * @return int
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + classId;
		result = (prime * result) + id;
		return result;
	}
	
	/**
	 * Method isAltCast.
	 * @return boolean
	 */
	public final boolean isAltCast()
	{
		return altCast;
	}
	
	/**
	 * Method isApply.
	 * @return boolean[]
	 */
	public final boolean[] isApply()
	{
		return apply;
	}
	
	/**
	 * Method isBlockingMove.
	 * @return boolean
	 */
	public final boolean isBlockingMove()
	{
		return blockingMove;
	}
	
	/**
	 * Method isCanOwerturn.
	 * @return boolean
	 */
	public final boolean isCanOwerturn()
	{
		return canOwerturn;
	}
	
	/**
	 * Method isCastToMove.
	 * @return boolean
	 */
	public final boolean isCastToMove()
	{
		return castToMove;
	}
	
	/**
	 * Method isEvasion.
	 * @return boolean
	 */
	public final boolean isEvasion()
	{
		return evasion;
	}
	
	/**
	 * Method isForceCast.
	 * @return boolean
	 */
	public final boolean isForceCast()
	{
		return forceCast;
	}
	
	/**
	 * Method isHasFast.
	 * @return boolean
	 */
	public final boolean isHasFast()
	{
		return hasFast;
	}
	
	/**
	 * Method isIgnoreBarrier.
	 * @return boolean
	 */
	public final boolean isIgnoreBarrier()
	{
		return ignoreBarrier;
	}
	
	/**
	 * Method isImplemented.
	 * @return boolean
	 */
	public final boolean isImplemented()
	{
		return implemented;
	}
	
	/**
	 * Method isNoCaster.
	 * @return boolean
	 */
	public final boolean isNoCaster()
	{
		return noCaster;
	}
	
	/**
	 * Method isRush.
	 * @return boolean
	 */
	public boolean isRush()
	{
		return rush;
	}
	
	/**
	 * Method isShieldIgnore.
	 * @return boolean
	 */
	public final boolean isShieldIgnore()
	{
		return shieldIgnore;
	}
	
	/**
	 * Method isShortSkill.
	 * @return boolean
	 */
	public final boolean isShortSkill()
	{
		return shortSkill;
	}
	
	/**
	 * Method isStaticCast.
	 * @return boolean
	 */
	public final boolean isStaticCast()
	{
		return staticCast;
	}
	
	/**
	 * Method isStaticInterval.
	 * @return boolean
	 */
	public final boolean isStaticInterval()
	{
		return staticInterval;
	}
	
	/**
	 * Method isToggle.
	 * @return boolean
	 */
	public final boolean isToggle()
	{
		return toggle;
	}
	
	/**
	 * Method getSpeedOffset.
	 * @return int
	 */
	public int getSpeedOffset()
	{
		return speedOffset;
	}
	
	/**
	 * Method isTrigger.
	 * @return boolean
	 */
	public final boolean isTrigger()
	{
		return trigger;
	}
	
	/**
	 * Method isVisibleOnSkillList.
	 * @return boolean
	 */
	public final boolean isVisibleOnSkillList()
	{
		return visibleOnSkillList;
	}
	
	/**
	 * Method newInstance.
	 * @return Skill
	 */
	public Skill newInstance()
	{
		final Skill skill = pool.take();
		
		if (skill == null)
		{
			return skillType.newInstance(this);
		}
		
		return skill;
	}
	
	/**
	 * Method put.
	 * @param skill Skill
	 */
	public void put(Skill skill)
	{
		pool.put(skill);
	}
	
	/**
	 * Method reload.
	 * @param update SkillTemplate
	 */
	@Override
	public void reload(SkillTemplate update)
	{
		Objects.reload(this, update);
	}
	
	/**
	 * Method isStaticReuseDelay.
	 * @return boolean
	 */
	public boolean isStaticReuseDelay()
	{
		return staticReuseDelay;
	}
	
	/**
	 * Method removeCastFuncs.
	 * @param owner Character
	 */
	public void removeCastFuncs(Character owner)
	{
		if (castFuncs.length < 1)
		{
			return;
		}
		
		for (Func castFunc : castFuncs)
		{
			castFunc.removeFuncTo(owner);
		}
		
		owner.updateInfo();
	}
	
	/**
	 * Method removePassiveFuncs.
	 * @param owner Character
	 */
	public void removePassiveFuncs(Character owner)
	{
		if (passiveFuncs.length < 1)
		{
			return;
		}
		
		for (Func passiveFunc : passiveFuncs)
		{
			passiveFunc.removeFuncTo(owner);
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "id = " + id + ", classId = " + classId + ", level = " + level + ", name = " + name + ", funcs = " + Arrays.toString(passiveFuncs);
	}
	
	/**
	 * Method isCorrectableTarget.
	 * @return boolean
	 */
	public boolean isCorrectableTarget()
	{
		return correctableTarget;
	}
}

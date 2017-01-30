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
package tera.gameserver.model.skillengine;

import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.array.Array;
import rlib.util.pools.Foldable;

/**
 * @author Ronn
 * @created 12.04.2012
 */
public interface Skill extends Foldable
{
	
	public static final int PVP_SKILL_ID = 67308865;
	
	public static final int PVP_SKILL_CLASS = -1;
	
	/**
	 * Method addTargets.
	 * @param targets Array<Character>
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	public void addTargets(Array<Character> targets, Character attacker, float targetX, float targetY, float targetZ);
	
	/**
	 * Method applySkill.
	 * @param attacker Character
	 * @param target Character
	 * @return AttackInfo
	 */
	public AttackInfo applySkill(Character attacker, Character target);
	
	/**
	 * Method blockMpConsume.
	 * @param damage int
	 * @return int
	 */
	public int blockMpConsume(int damage);
	
	/**
	 * Method checkCondition.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return boolean
	 */
	public boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ);
	
	/**
	 * Method endSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param force boolean
	 */
	public void endSkill(Character attacker, float targetX, float targetY, float targetZ, boolean force);
	
	public void fold();
	
	/**
	 * Method getAggroPoint.
	 * @return int
	 */
	public int getAggroPoint();
	
	/**
	 * Method getCastCount.
	 * @return int
	 */
	public int getCastCount();
	
	/**
	 * Method getCastHeading.
	 * @return int
	 */
	public int getCastHeading();
	
	/**
	 * Method getCastId.
	 * @return int
	 */
	public int getCastId();
	
	/**
	 * Method getCastMaxRange.
	 * @return int
	 */
	public int getCastMaxRange();
	
	/**
	 * Method getCastMinRange.
	 * @return int
	 */
	public int getCastMinRange();
	
	/**
	 * Method getChance.
	 * @return int
	 */
	public int getChance();
	
	/**
	 * Method getClassId.
	 * @return int
	 */
	public int getClassId();
	
	/**
	 * Method getCondition.
	 * @return Condition
	 */
	public Condition getCondition();
	
	/**
	 * Method getDamageId.
	 * @return int
	 */
	public int getDamageId();
	
	/**
	 * Method getDegree.
	 * @return int
	 */
	public int getDegree();
	
	/**
	 * Method getDelay.
	 * @return int
	 */
	public int getDelay();
	
	/**
	 * Method getEffectTemplates.
	 * @return EffectTemplate[]
	 */
	public EffectTemplate[] getEffectTemplates();
	
	/**
	 * Method getGroup.
	 * @return String
	 */
	public String getGroup();
	
	/**
	 * Method getHeading.
	 * @return int
	 */
	public int getHeading();
	
	/**
	 * Method getHitTime.
	 * @return int
	 */
	public int getHitTime();
	
	/**
	 * Method getHpConsume.
	 * @return int
	 */
	public int getHpConsume();
	
	/**
	 * Method getIconId.
	 * @return int
	 */
	public int getIconId();
	
	/**
	 * Method getId.
	 * @return int
	 */
	public int getId();
	
	/**
	 * Method getImpactX.
	 * @return float
	 */
	public float getImpactX();
	
	/**
	 * Method getImpactY.
	 * @return float
	 */
	public float getImpactY();
	
	/**
	 * Method getImpactZ.
	 * @return float
	 */
	public float getImpactZ();
	
	/**
	 * Method getInterval.
	 * @return int
	 */
	public int getInterval();
	
	/**
	 * Method getItemCount.
	 * @return int
	 */
	public int getItemCount();
	
	/**
	 * Method getItemCountConsume.
	 * @return long
	 */
	public long getItemCountConsume();
	
	/**
	 * Method getItemId.
	 * @return int
	 */
	public int getItemId();
	
	/**
	 * Method getItemIdConsume.
	 * @return int
	 */
	public int getItemIdConsume();
	
	/**
	 * Method getLevel.
	 * @return int
	 */
	public int getLevel();
	
	/**
	 * Method getMaxTargets.
	 * @return int
	 */
	public int getMaxTargets();
	
	/**
	 * Method getMinRange.
	 * @return int
	 */
	public int getMinRange();
	
	/**
	 * Method getMoveDelay.
	 * @return int
	 */
	public int getMoveDelay();
	
	/**
	 * Method getMoveDistance.
	 * @return int
	 */
	public int getMoveDistance();
	
	/**
	 * Method getMoveHeading.
	 * @return int
	 */
	public int getMoveHeading();
	
	/**
	 * Method getMoveTime.
	 * @return int
	 */
	public int getMoveTime();
	
	/**
	 * Method getMpConsume.
	 * @return int
	 */
	public int getMpConsume();
	
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName();
	
	/**
	 * Method getOperateType.
	 * @return OperateType
	 */
	public OperateType getOperateType();
	
	/**
	 * Method getOwerturnMod.
	 * @return float
	 */
	public float getOwerturnMod();
	
	/**
	 * Method getPower.
	 * @return int
	 */
	public int getPower();
	
	/**
	 * Method getRadius.
	 * @return int
	 */
	public int getRadius();
	
	/**
	 * Method getRange.
	 * @return int
	 */
	public int getRange();
	
	/**
	 * Method getRangeType.
	 * @return SkillRangeType
	 */
	public SkillRangeType getRangeType();
	
	/**
	 * Method getReuseDelay.
	 * @param caster Character
	 * @return int
	 */
	public int getReuseDelay(Character caster);
	
	/**
	 * Method getReuseId.
	 * @return int
	 */
	public int getReuseId();
	
	/**
	 * Method getReuseIds.
	 * @return int[]
	 */
	public int[] getReuseIds();
	
	/**
	 * Method getSkillName.
	 * @return SkillName
	 */
	public SkillName getSkillName();
	
	/**
	 * Method getSkillType.
	 * @return SkillType
	 */
	public SkillType getSkillType();
	
	/**
	 * Method getSpeed.
	 * @return int
	 */
	public int getSpeed();
	
	/**
	 * Method getStage.
	 * @return int
	 */
	public int getStage();
	
	/**
	 * Method getTargetType.
	 * @return TargetType
	 */
	public TargetType getTargetType();
	
	/**
	 * Method getTemplate.
	 * @return SkillTemplate
	 */
	public SkillTemplate getTemplate();
	
	/**
	 * Method getTransformId.
	 * @return int
	 */
	public int getTransformId();
	
	/**
	 * Method getWidth.
	 * @return int
	 */
	public int getWidth();
	
	/**
	 * Method hasPrevSkillName.
	 * @param skillName SkillName
	 * @return boolean
	 */
	public boolean hasPrevSkillName(SkillName skillName);
	
	/**
	 * Method isActive.
	 * @return boolean
	 */
	public boolean isActive();
	
	/**
	 * Method isAltCast.
	 * @return boolean
	 */
	public boolean isAltCast();
	
	/**
	 * Method isApply.
	 * @return boolean
	 */
	public boolean isApply();
	
	/**
	 * Method isBlockingMove.
	 * @return boolean
	 */
	public boolean isBlockingMove();
	
	/**
	 * Method isCanceable.
	 * @return boolean
	 */
	public boolean isCanceable();
	
	/**
	 * Method isCanOwerturn.
	 * @return boolean
	 */
	public boolean isCanOwerturn();
	
	/**
	 * Method isCastToMove.
	 * @return boolean
	 */
	public boolean isCastToMove();
	
	/**
	 * Method isEvasion.
	 * @return boolean
	 */
	public boolean isEvasion();
	
	/**
	 * Method isForceCast.
	 * @return boolean
	 */
	public boolean isForceCast();
	
	/**
	 * Method isHasFast.
	 * @return boolean
	 */
	public boolean isHasFast();
	
	/**
	 * Method isIgnoreBarrier.
	 * @return boolean
	 */
	public boolean isIgnoreBarrier();
	
	/**
	 * Method isImplemented.
	 * @return boolean
	 */
	public boolean isImplemented();
	
	/**
	 * Method isNoCaster.
	 * @return boolean
	 */
	public boolean isNoCaster();
	
	/**
	 * Method isOffensive.
	 * @return boolean
	 */
	public boolean isOffensive();
	
	/**
	 * Method isOneTarget.
	 * @return boolean
	 */
	public boolean isOneTarget();
	
	/**
	 * Method isPassive.
	 * @return boolean
	 */
	public boolean isPassive();
	
	/**
	 * Method isRush.
	 * @return boolean
	 */
	public boolean isRush();
	
	/**
	 * Method isShieldIgnore.
	 * @return boolean
	 */
	public boolean isShieldIgnore();
	
	/**
	 * Method isShortSkill.
	 * @return boolean
	 */
	public boolean isShortSkill();
	
	/**
	 * Method isStaticCast.
	 * @return boolean
	 */
	public boolean isStaticCast();
	
	/**
	 * Method isStaticInterval.
	 * @return boolean
	 */
	public boolean isStaticInterval();
	
	/**
	 * Method isToggle.
	 * @return boolean
	 */
	public boolean isToggle();
	
	/**
	 * Method isTrigger.
	 * @return boolean
	 */
	public boolean isTrigger();
	
	/**
	 * Method isVisibleOnSkillList.
	 * @return boolean
	 */
	public boolean isVisibleOnSkillList();
	
	/**
	 * Method isWaitable.
	 * @return boolean
	 */
	public boolean isWaitable();
	
	/**
	 * Method setImpactX.
	 * @param targetX float
	 */
	public void setImpactX(float targetX);
	
	/**
	 * Method setImpactY.
	 * @param targetY float
	 */
	public void setImpactY(float targetY);
	
	/**
	 * Method setImpactZ.
	 * @param targetZ float
	 */
	public void setImpactZ(float targetZ);
	
	/**
	 * Method startSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	public void startSkill(Character attacker, float targetX, float targetY, float targetZ);
	
	/**
	 * Method useSkill.
	 * @param character Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	public void useSkill(Character character, float targetX, float targetY, float targetZ);
	
	/**
	 * Method isStaticReuseDelay.
	 * @return boolean
	 */
	public boolean isStaticReuseDelay();
	
	/**
	 * Method getSpeedOffset.
	 * @return int
	 */
	public int getSpeedOffset();
	
	/**
	 * Method isCorrectableTarget.
	 * @return boolean
	 */
	public boolean isCorrectableTarget();
	
	/**
	 * Method isTargetSelf.
	 * @return boolean
	 */
	public boolean isTargetSelf();
}

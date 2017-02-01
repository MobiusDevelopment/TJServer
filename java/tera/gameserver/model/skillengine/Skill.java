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
	
	int PVP_SKILL_ID = 67308865;
	
	int PVP_SKILL_CLASS = -1;
	
	/**
	 * Method addTargets.
	 * @param targets Array<Character>
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	void addTargets(Array<Character> targets, Character attacker, float targetX, float targetY, float targetZ);
	
	/**
	 * Method applySkill.
	 * @param attacker Character
	 * @param target Character
	 * @return AttackInfo
	 */
	AttackInfo applySkill(Character attacker, Character target);
	
	/**
	 * Method blockMpConsume.
	 * @param damage int
	 * @return int
	 */
	int blockMpConsume(int damage);
	
	/**
	 * Method checkCondition.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return boolean
	 */
	boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ);
	
	/**
	 * Method endSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param force boolean
	 */
	void endSkill(Character attacker, float targetX, float targetY, float targetZ, boolean force);
	
	void fold();
	
	/**
	 * Method getAggroPoint.
	 * @return int
	 */
	int getAggroPoint();
	
	/**
	 * Method getCastCount.
	 * @return int
	 */
	int getCastCount();
	
	/**
	 * Method getCastHeading.
	 * @return int
	 */
	int getCastHeading();
	
	/**
	 * Method getCastId.
	 * @return int
	 */
	int getCastId();
	
	/**
	 * Method getCastMaxRange.
	 * @return int
	 */
	int getCastMaxRange();
	
	/**
	 * Method getCastMinRange.
	 * @return int
	 */
	int getCastMinRange();
	
	/**
	 * Method getChance.
	 * @return int
	 */
	int getChance();
	
	/**
	 * Method getClassId.
	 * @return int
	 */
	int getClassId();
	
	/**
	 * Method getCondition.
	 * @return Condition
	 */
	Condition getCondition();
	
	/**
	 * Method getDamageId.
	 * @return int
	 */
	int getDamageId();
	
	/**
	 * Method getDegree.
	 * @return int
	 */
	int getDegree();
	
	/**
	 * Method getDelay.
	 * @return int
	 */
	int getDelay();
	
	/**
	 * Method getEffectTemplates.
	 * @return EffectTemplate[]
	 */
	EffectTemplate[] getEffectTemplates();
	
	/**
	 * Method getGroup.
	 * @return String
	 */
	String getGroup();
	
	/**
	 * Method getHeading.
	 * @return int
	 */
	int getHeading();
	
	/**
	 * Method getHitTime.
	 * @return int
	 */
	int getHitTime();
	
	/**
	 * Method getHpConsume.
	 * @return int
	 */
	int getHpConsume();
	
	/**
	 * Method getIconId.
	 * @return int
	 */
	int getIconId();
	
	/**
	 * Method getId.
	 * @return int
	 */
	int getId();
	
	/**
	 * Method getImpactX.
	 * @return float
	 */
	float getImpactX();
	
	/**
	 * Method getImpactY.
	 * @return float
	 */
	float getImpactY();
	
	/**
	 * Method getImpactZ.
	 * @return float
	 */
	float getImpactZ();
	
	/**
	 * Method getInterval.
	 * @return int
	 */
	int getInterval();
	
	/**
	 * Method getItemCount.
	 * @return int
	 */
	int getItemCount();
	
	/**
	 * Method getItemCountConsume.
	 * @return long
	 */
	long getItemCountConsume();
	
	/**
	 * Method getItemId.
	 * @return int
	 */
	int getItemId();
	
	/**
	 * Method getItemIdConsume.
	 * @return int
	 */
	int getItemIdConsume();
	
	/**
	 * Method getLevel.
	 * @return int
	 */
	int getLevel();
	
	/**
	 * Method getMaxTargets.
	 * @return int
	 */
	int getMaxTargets();
	
	/**
	 * Method getMinRange.
	 * @return int
	 */
	int getMinRange();
	
	/**
	 * Method getMoveDelay.
	 * @return int
	 */
	int getMoveDelay();
	
	/**
	 * Method getMoveDistance.
	 * @return int
	 */
	int getMoveDistance();
	
	/**
	 * Method getMoveHeading.
	 * @return int
	 */
	int getMoveHeading();
	
	/**
	 * Method getMoveTime.
	 * @return int
	 */
	int getMoveTime();
	
	/**
	 * Method getMpConsume.
	 * @return int
	 */
	int getMpConsume();
	
	/**
	 * Method getName.
	 * @return String
	 */
	String getName();
	
	/**
	 * Method getOperateType.
	 * @return OperateType
	 */
	OperateType getOperateType();
	
	/**
	 * Method getOwerturnMod.
	 * @return float
	 */
	float getOwerturnMod();
	
	/**
	 * Method getPower.
	 * @return int
	 */
	int getPower();
	
	/**
	 * Method getRadius.
	 * @return int
	 */
	int getRadius();
	
	/**
	 * Method getRange.
	 * @return int
	 */
	int getRange();
	
	/**
	 * Method getRangeType.
	 * @return SkillRangeType
	 */
	SkillRangeType getRangeType();
	
	/**
	 * Method getReuseDelay.
	 * @param caster Character
	 * @return int
	 */
	int getReuseDelay(Character caster);
	
	/**
	 * Method getReuseId.
	 * @return int
	 */
	int getReuseId();
	
	/**
	 * Method getReuseIds.
	 * @return int[]
	 */
	int[] getReuseIds();
	
	/**
	 * Method getSkillName.
	 * @return SkillName
	 */
	SkillName getSkillName();
	
	/**
	 * Method getSkillType.
	 * @return SkillType
	 */
	SkillType getSkillType();
	
	/**
	 * Method getSpeed.
	 * @return int
	 */
	int getSpeed();
	
	/**
	 * Method getStage.
	 * @return int
	 */
	int getStage();
	
	/**
	 * Method getTargetType.
	 * @return TargetType
	 */
	TargetType getTargetType();
	
	/**
	 * Method getTemplate.
	 * @return SkillTemplate
	 */
	SkillTemplate getTemplate();
	
	/**
	 * Method getTransformId.
	 * @return int
	 */
	int getTransformId();
	
	/**
	 * Method getWidth.
	 * @return int
	 */
	int getWidth();
	
	/**
	 * Method hasPrevSkillName.
	 * @param skillName SkillName
	 * @return boolean
	 */
	boolean hasPrevSkillName(SkillName skillName);
	
	/**
	 * Method isActive.
	 * @return boolean
	 */
	boolean isActive();
	
	/**
	 * Method isAltCast.
	 * @return boolean
	 */
	boolean isAltCast();
	
	/**
	 * Method isApply.
	 * @return boolean
	 */
	boolean isApply();
	
	/**
	 * Method isBlockingMove.
	 * @return boolean
	 */
	boolean isBlockingMove();
	
	/**
	 * Method isCanceable.
	 * @return boolean
	 */
	boolean isCanceable();
	
	/**
	 * Method isCanOwerturn.
	 * @return boolean
	 */
	boolean isCanOwerturn();
	
	/**
	 * Method isCastToMove.
	 * @return boolean
	 */
	boolean isCastToMove();
	
	/**
	 * Method isEvasion.
	 * @return boolean
	 */
	boolean isEvasion();
	
	/**
	 * Method isForceCast.
	 * @return boolean
	 */
	boolean isForceCast();
	
	/**
	 * Method isHasFast.
	 * @return boolean
	 */
	boolean isHasFast();
	
	/**
	 * Method isIgnoreBarrier.
	 * @return boolean
	 */
	boolean isIgnoreBarrier();
	
	/**
	 * Method isImplemented.
	 * @return boolean
	 */
	boolean isImplemented();
	
	/**
	 * Method isNoCaster.
	 * @return boolean
	 */
	boolean isNoCaster();
	
	/**
	 * Method isOffensive.
	 * @return boolean
	 */
	boolean isOffensive();
	
	/**
	 * Method isOneTarget.
	 * @return boolean
	 */
	boolean isOneTarget();
	
	/**
	 * Method isPassive.
	 * @return boolean
	 */
	boolean isPassive();
	
	/**
	 * Method isRush.
	 * @return boolean
	 */
	boolean isRush();
	
	/**
	 * Method isShieldIgnore.
	 * @return boolean
	 */
	boolean isShieldIgnore();
	
	/**
	 * Method isShortSkill.
	 * @return boolean
	 */
	boolean isShortSkill();
	
	/**
	 * Method isStaticCast.
	 * @return boolean
	 */
	boolean isStaticCast();
	
	/**
	 * Method isStaticInterval.
	 * @return boolean
	 */
	boolean isStaticInterval();
	
	/**
	 * Method isToggle.
	 * @return boolean
	 */
	boolean isToggle();
	
	/**
	 * Method isTrigger.
	 * @return boolean
	 */
	boolean isTrigger();
	
	/**
	 * Method isVisibleOnSkillList.
	 * @return boolean
	 */
	boolean isVisibleOnSkillList();
	
	/**
	 * Method isWaitable.
	 * @return boolean
	 */
	boolean isWaitable();
	
	/**
	 * Method setImpactX.
	 * @param targetX float
	 */
	void setImpactX(float targetX);
	
	/**
	 * Method setImpactY.
	 * @param targetY float
	 */
	void setImpactY(float targetY);
	
	/**
	 * Method setImpactZ.
	 * @param targetZ float
	 */
	void setImpactZ(float targetZ);
	
	/**
	 * Method startSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	void startSkill(Character attacker, float targetX, float targetY, float targetZ);
	
	/**
	 * Method useSkill.
	 * @param character Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	void useSkill(Character character, float targetX, float targetY, float targetZ);
	
	/**
	 * Method isStaticReuseDelay.
	 * @return boolean
	 */
	boolean isStaticReuseDelay();
	
	/**
	 * Method getSpeedOffset.
	 * @return int
	 */
	int getSpeedOffset();
	
	/**
	 * Method isCorrectableTarget.
	 * @return boolean
	 */
	boolean isCorrectableTarget();
	
	/**
	 * Method isTargetSelf.
	 * @return boolean
	 */
	boolean isTargetSelf();
}

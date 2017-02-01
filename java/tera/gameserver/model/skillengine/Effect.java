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

import tera.gameserver.model.Character;
import tera.gameserver.model.EffectList;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.pools.Foldable;

/**
 * @author Ronn
 * @created 13.04.2012
 */
public interface Effect extends Foldable
{
	
	void exit();
	
	void fold();
	
	/**
	 * Method getChance.
	 * @return int
	 */
	int getChance();
	
	/**
	 * Method getCount.
	 * @return int
	 */
	int getCount();
	
	/**
	 * Method getEffected.
	 * @return Character
	 */
	Character getEffected();
	
	/**
	 * Method getEffectId.
	 * @return int
	 */
	int getEffectId();
	
	/**
	 * Method getEffectList.
	 * @return EffectList
	 */
	EffectList getEffectList();
	
	/**
	 * Method getEffector.
	 * @return Character
	 */
	Character getEffector();
	
	/**
	 * Method getEffectType.
	 * @return EffectType
	 */
	EffectType getEffectType();
	
	/**
	 * Method getFuncs.
	 * @return Func[]
	 */
	Func[] getFuncs();
	
	/**
	 * Method getOrder.
	 * @return int
	 */
	int getOrder();
	
	/**
	 * Method getPeriod.
	 * @return int
	 */
	int getPeriod();
	
	/**
	 * Method getResistType.
	 * @return ResistType
	 */
	ResistType getResistType();
	
	/**
	 * Method getSkillClassId.
	 * @return int
	 */
	int getSkillClassId();
	
	/**
	 * Method getSkillId.
	 * @return int
	 */
	int getSkillId();
	
	/**
	 * Method getSkillTemplate.
	 * @return SkillTemplate
	 */
	SkillTemplate getSkillTemplate();
	
	/**
	 * Method getStackType.
	 * @return String
	 */
	String getStackType();
	
	/**
	 * Method getStartTime.
	 * @return long
	 */
	long getStartTime();
	
	/**
	 * Method getState.
	 * @return EffectState
	 */
	EffectState getState();
	
	/**
	 * Method getTemplate.
	 * @return EffectTemplate
	 */
	EffectTemplate getTemplate();
	
	/**
	 * Method getTime.
	 * @return int
	 */
	int getTime();
	
	/**
	 * Method getTimeEnd.
	 * @return int
	 */
	int getTimeEnd();
	
	/**
	 * Method getTimeForPacket.
	 * @return int
	 */
	int getTimeForPacket();
	
	/**
	 * Method getTotalTime.
	 * @return int
	 */
	int getTotalTime();
	
	/**
	 * Method getUsingCount.
	 * @return int
	 */
	int getUsingCount();
	
	/**
	 * Method hasStackType.
	 * @return boolean
	 */
	boolean hasStackType();
	
	/**
	 * Method isAura.
	 * @return boolean
	 */
	boolean isAura();
	
	/**
	 * Method isDebuff.
	 * @return boolean
	 */
	boolean isDebuff();
	
	/**
	 * Method isEffect.
	 * @return boolean
	 */
	boolean isEffect();
	
	/**
	 * Method isEnded.
	 * @return boolean
	 */
	boolean isEnded();
	
	/**
	 * Method isFinished.
	 * @return boolean
	 */
	boolean isFinished();
	
	/**
	 * Method isInUse.
	 * @return boolean
	 */
	boolean isInUse();
	
	/**
	 * Method isNoAttack.
	 * @return boolean
	 */
	boolean isNoAttack();
	
	/**
	 * Method isNoAttacked.
	 * @return boolean
	 */
	boolean isNoAttacked();
	
	/**
	 * Method isNoOwerturn.
	 * @return boolean
	 */
	boolean isNoOwerturn();
	
	/**
	 * Method onActionTime.
	 * @return boolean
	 */
	boolean onActionTime();
	
	void onExit();
	
	void onStart();
	
	void scheduleEffect();
	
	/**
	 * Method setCount.
	 * @param count int
	 */
	void setCount(int count);
	
	/**
	 * Method setEffected.
	 * @param effected Character
	 */
	void setEffected(Character effected);
	
	/**
	 * Method setEffectList.
	 * @param effectList EffectList
	 */
	void setEffectList(EffectList effectList);
	
	/**
	 * Method setEffector.
	 * @param effector Character
	 */
	void setEffector(Character effector);
	
	/**
	 * Method setInUse.
	 * @param value boolean
	 */
	void setInUse(boolean value);
	
	/**
	 * Method setPeriod.
	 * @param period int
	 */
	void setPeriod(int period);
	
	/**
	 * Method setStartTime.
	 * @param startTime long
	 */
	void setStartTime(long startTime);
	
	/**
	 * Method setState.
	 * @param state EffectState
	 */
	void setState(EffectState state);
	
	/**
	 * Method isDynamicCount.
	 * @return boolean
	 */
	boolean isDynamicCount();
	
	/**
	 * Method isDynamicTime.
	 * @return boolean
	 */
	boolean isDynamicTime();
}

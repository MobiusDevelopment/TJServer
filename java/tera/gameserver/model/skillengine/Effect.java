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
	
	public void exit();
	
	public void fold();
	
	/**
	 * Method getChance.
	 * @return int
	 */
	public int getChance();
	
	/**
	 * Method getCount.
	 * @return int
	 */
	public int getCount();
	
	/**
	 * Method getEffected.
	 * @return Character
	 */
	public Character getEffected();
	
	/**
	 * Method getEffectId.
	 * @return int
	 */
	public int getEffectId();
	
	/**
	 * Method getEffectList.
	 * @return EffectList
	 */
	public EffectList getEffectList();
	
	/**
	 * Method getEffector.
	 * @return Character
	 */
	public Character getEffector();
	
	/**
	 * Method getEffectType.
	 * @return EffectType
	 */
	public EffectType getEffectType();
	
	/**
	 * Method getFuncs.
	 * @return Func[]
	 */
	public Func[] getFuncs();
	
	/**
	 * Method getOrder.
	 * @return int
	 */
	public int getOrder();
	
	/**
	 * Method getPeriod.
	 * @return int
	 */
	public int getPeriod();
	
	/**
	 * Method getResistType.
	 * @return ResistType
	 */
	public ResistType getResistType();
	
	/**
	 * Method getSkillClassId.
	 * @return int
	 */
	public int getSkillClassId();
	
	/**
	 * Method getSkillId.
	 * @return int
	 */
	public int getSkillId();
	
	/**
	 * Method getSkillTemplate.
	 * @return SkillTemplate
	 */
	public SkillTemplate getSkillTemplate();
	
	/**
	 * Method getStackType.
	 * @return String
	 */
	public String getStackType();
	
	/**
	 * Method getStartTime.
	 * @return long
	 */
	public long getStartTime();
	
	/**
	 * Method getState.
	 * @return EffectState
	 */
	public EffectState getState();
	
	/**
	 * Method getTemplate.
	 * @return EffectTemplate
	 */
	public EffectTemplate getTemplate();
	
	/**
	 * Method getTime.
	 * @return int
	 */
	public int getTime();
	
	/**
	 * Method getTimeEnd.
	 * @return int
	 */
	public int getTimeEnd();
	
	/**
	 * Method getTimeForPacket.
	 * @return int
	 */
	public int getTimeForPacket();
	
	/**
	 * Method getTotalTime.
	 * @return int
	 */
	public int getTotalTime();
	
	/**
	 * Method getUsingCount.
	 * @return int
	 */
	public int getUsingCount();
	
	/**
	 * Method hasStackType.
	 * @return boolean
	 */
	public boolean hasStackType();
	
	/**
	 * Method isAura.
	 * @return boolean
	 */
	public boolean isAura();
	
	/**
	 * Method isDebuff.
	 * @return boolean
	 */
	public boolean isDebuff();
	
	/**
	 * Method isEffect.
	 * @return boolean
	 */
	public boolean isEffect();
	
	/**
	 * Method isEnded.
	 * @return boolean
	 */
	public boolean isEnded();
	
	/**
	 * Method isFinished.
	 * @return boolean
	 */
	public boolean isFinished();
	
	/**
	 * Method isInUse.
	 * @return boolean
	 */
	public boolean isInUse();
	
	/**
	 * Method isNoAttack.
	 * @return boolean
	 */
	public boolean isNoAttack();
	
	/**
	 * Method isNoAttacked.
	 * @return boolean
	 */
	public boolean isNoAttacked();
	
	/**
	 * Method isNoOwerturn.
	 * @return boolean
	 */
	public boolean isNoOwerturn();
	
	/**
	 * Method onActionTime.
	 * @return boolean
	 */
	public boolean onActionTime();
	
	public void onExit();
	
	public void onStart();
	
	public void scheduleEffect();
	
	/**
	 * Method setCount.
	 * @param count int
	 */
	public void setCount(int count);
	
	/**
	 * Method setEffected.
	 * @param effected Character
	 */
	public void setEffected(Character effected);
	
	/**
	 * Method setEffectList.
	 * @param effectList EffectList
	 */
	public void setEffectList(EffectList effectList);
	
	/**
	 * Method setEffector.
	 * @param effector Character
	 */
	public void setEffector(Character effector);
	
	/**
	 * Method setInUse.
	 * @param value boolean
	 */
	public void setInUse(boolean value);
	
	/**
	 * Method setPeriod.
	 * @param period int
	 */
	public void setPeriod(int period);
	
	/**
	 * Method setStartTime.
	 * @param startTime long
	 */
	public void setStartTime(long startTime);
	
	/**
	 * Method setState.
	 * @param state EffectState
	 */
	public void setState(EffectState state);
	
	/**
	 * Method isDynamicCount.
	 * @return boolean
	 */
	public boolean isDynamicCount();
	
	/**
	 * Method isDynamicTime.
	 * @return boolean
	 */
	public boolean isDynamicTime();
}

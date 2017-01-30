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

/**
 * @author Ronn
 */
public enum ResistType
{
	noneResist(null, null),
	owerturnResist(StatType.OWERTURN_POWER, StatType.OWERTURN_RECEPTIVE),
	stunResist(StatType.STUN_POWER, StatType.STUN_RECEPTIVE),
	damageResist(StatType.DAMAGE_POWER, StatType.DAMAGE_RECEPTIVE),
	weakResist(StatType.WEAK_POWER, StatType.WEAK_RECEPTIVE);
	
	private StatType powerStat;
	
	private StatType rcptStat;
	
	/**
	 * Constructor for ResistType.
	 * @param powerStat StatType
	 * @param rcptStat StatType
	 */
	private ResistType(StatType powerStat, StatType rcptStat)
	{
		this.powerStat = powerStat;
		this.rcptStat = rcptStat;
	}
	
	/**
	 * Method checkCondition.
	 * @param attacker Character
	 * @param attacked Character
	 * @return boolean
	 */
	public boolean checkCondition(Character attacker, Character attacked)
	{
		switch (this)
		{
			case stunResist:
				return !attacked.isStunImmunity();
			
			case owerturnResist:
				return !(attacked.isOwerturnImmunity() || attacked.isOwerturned());
			
			default:
				break;
		}
		
		return true;
	}
	
	/**
	 * Method getPowerStat.
	 * @return StatType
	 */
	public final StatType getPowerStat()
	{
		return powerStat;
	}
	
	/**
	 * Method getRcptStat.
	 * @return StatType
	 */
	public final StatType getRcptStat()
	{
		return rcptStat;
	}
}

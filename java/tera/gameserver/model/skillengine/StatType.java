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

import java.util.NoSuchElementException;

/**
 * @author Ronn
 */
public enum StatType
{
	
	MAX_HP("maxHp"),
	MAX_MP("maxMp"),
	
	ATTACK("atk"),
	DEFENSE("def"),
	
	IMPACT("impact"),
	BALANCE("balance"),
	
	CRITICAL_DAMAGE("cAtk"),
	CRITICAL_RATE("rCrit"),
	
	CRIT_CHANCE_RECEPTIVE("critChanceRcpt"),
	
	ATTACK_SPEED("atkSpd"),
	RUN_SPEED("runSpd"),
	
	BASE_HEART("heart"),
	
	POWER_FACTOR("powerFactor"),
	DEFENSE_FACTOR("defenseFactor"),
	
	IMPACT_FACTOR("impactFactor"),
	BALANCE_FACTOR("balanceFactor"),
	
	WEAK_RECEPTIVE("weakRcpt"),
	DAMAGE_RECEPTIVE("dmgRcpt"),
	STUN_RECEPTIVE("stunRcpt"),
	OWERTURN_RECEPTIVE("owerturnRcpt"),
	
	WEAK_POWER("weakPower"),
	DAMAGE_POWER("dmgPower"),
	STUN_POWER("stunPower"),
	OWERTURN_POWER("owerturnPower"),
	
	COLLECTING_ORE("collectingOre"),
	COLLECTING_PLANT("collectingPlant"),
	COLLECTING_OTHER("collectingOther"),
	
	AGGRO_MOD("agrMod"),
	
	SHORT_SKILL_REUSE("shortReuse"),
	RANGE_SKILL_REUSE("rangeReuse"),
	OTHER_SKILL_REUSE("otherReuse"),
	
	SHORT_SKILL_POWER("shortPower"),
	RANGE_SKILL_POWER("rangePower"),
	OTHER_SKILL_POWER("otherPower"),
	
	SHORT_SKILL_RECEPTIVE("shortRcpt"),
	RANGE_SKILL_RECEPTIVE("rangeRcpt"),
	OTHER_SKILL_RECEPTIVE("otherReuse"),
	
	REGEN_HP("regHp"),
	REGEN_MP("regMp"),
	
	MIN_HEART("minHeart"),
	MIN_HEART_PERCENT("minHeartPercent"),
	
	ABSORPTION_HP("absHp"),
	ABSORPTION_MP("absMp"),
	
	GAIN_MP("gainMp"),
	
	ATTACK_ABSORPTION_MP("atkAbsMp"),
	DEFENSE_ABSORPTION_MP("defAbsMp"),
	
	ABSORPTION_HP_POWER("absHpPower"),
	ABSORPTION_MP_POWER("absMpPower"),
	
	ABSORPTION_MP_ON_MAX("absMpOnMax"),
	
	HEAL_POWER_PERCENT("healPowerPercent"),
	HEAL_POWER_STATIC("healPowerStatic"),
	
	MAX_DAMAGE_DEFENSE("maxDamDef"),
	
	FALLING_DAMAGE("fallDam");
	
	public static final int SIZE = values().length;
	
	/**
	 * Method valueOfXml.
	 * @param name String
	 * @return StatType
	 */
	public static StatType valueOfXml(String name)
	{
		for (StatType stat : values())
		{
			if (stat.xmlName.equals(name))
			{
				return stat;
			}
		}
		
		throw new NoSuchElementException("Unknown name '" + name + "' for enum Stats");
	}
	
	private String xmlName;
	
	/**
	 * Constructor for StatType.
	 * @param xmlName String
	 */
	private StatType(String xmlName)
	{
		this.xmlName = xmlName;
	}
	
	/**
	 * Method getValue.
	 * @return String
	 */
	public String getValue()
	{
		return xmlName;
	}
}

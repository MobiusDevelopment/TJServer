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

import tera.Config;

/**
 * @author Ronn
 */
public enum SkillRangeType
{
	SHORT_SKILL(StatType.SHORT_SKILL_POWER, StatType.SHORT_SKILL_RECEPTIVE, StatType.SHORT_SKILL_REUSE, Config.WORLD_SHORT_SKILL_REUSE_MOD),
	RANGE_SKILL(StatType.RANGE_SKILL_POWER, StatType.RANGE_SKILL_RECEPTIVE, StatType.RANGE_SKILL_REUSE, Config.WORLD_RANGE_SKILL_REUSE_MOD),
	OTHER_SKILL(StatType.OTHER_SKILL_POWER, StatType.OTHER_SKILL_RECEPTIVE, StatType.OTHER_SKILL_REUSE, Config.WORLD_OTHER_SKILL_REUSE_MOD);
	private StatType powerStat;
	private StatType rcptStat;
	private StatType reuseStat;
	private float reuseMod;
	
	/**
	 * Constructor for SkillRangeType.
	 * @param powerStat StatType
	 * @param rcptStat StatType
	 * @param reuseStat StatType
	 * @param reuseMod float
	 */
	private SkillRangeType(StatType powerStat, StatType rcptStat, StatType reuseStat, float reuseMod)
	{
		this.powerStat = powerStat;
		this.rcptStat = rcptStat;
		this.reuseStat = reuseStat;
		this.reuseMod = reuseMod;
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
	
	/**
	 * Method getReuseStat.
	 * @return StatType
	 */
	public final StatType getReuseStat()
	{
		return reuseStat;
	}
	
	/**
	 * Method getReuseMod.
	 * @return float
	 */
	public final float getReuseMod()
	{
		return reuseMod;
	}
}
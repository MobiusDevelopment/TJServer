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
package tera.gameserver.model.base;

import tera.Config;

/**
 * @author Ronn
 */
public abstract class Experience
{
	
	public static final int LEVEL[] =
	{
		-1, // level 0
		0,
		840,
		1846,
		3048,
		4470,
		6145,
		8113,
		10410,
		13088,
		16197,
		17795,
		18952,
		20270,
		22820,
		25748,
		28633,
		31859,
		43702,
		52077,
		66490,
		94955,
		106448,
		129397,
		153355,
		164682,
		176358,
		251223,
		283342,
		350584,
		450475,
		592831,
		700643,
		840159,
		900823,
		1056869,
		1210194,
		1420956,
		1750218,
		2250220,
		2742201,
		3104154,
		3560755,
		4524245,
		6424027,
		8942319,
		10815896,
		14351495,
		16135907,
		18594712,
		22440436,
		25893136,
		27707107,
		30765345,
		34158159,
		37921882,
		42096768,
		46727412,
		51863218,
		151558828,
		264049598,
		449513049,
	};
	
	/**
	 * Method getMaxLevel.
	 * @return int
	 */
	public static int getMaxLevel()
	{
		return Config.WORLD_PLAYER_MAX_LEVEL;
	}
	
	/**
	 * Method getNextExperience.
	 * @param currentLevel int
	 * @return int
	 */
	public static int getNextExperience(int currentLevel)
	{
		return LEVEL[currentLevel + 1];
	}
}
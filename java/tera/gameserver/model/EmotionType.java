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
package tera.gameserver.model;

/**
 * @author Ronn
 */
public enum EmotionType
{
	NULL,
	
	INSPECTION,
	
	FAST_INSPECTION,
	NONE3,
	NULL4,
	
	NONE5,
	NULL6,
	NULL7,
	NULL8,
	NULL9,
	NULL10,
	CHEMISTRY,
	
	SMITH,
	
	SEWS,
	BOW,
	NULL15,
	
	HELLO,
	
	BUW,
	
	LAUGHTER,
	
	CRYING,
	
	BOASTING,
	
	DANCE,
	
	DULL,
	
	SLAM,
	
	I_LOVE_YOU,
	
	MUSE,
	
	HEART,
	
	SHOW_ON_TERGET,
	
	POINT_FINGER,
	
	DISORDER,
	
	FAIL,
	
	INSPECT,
	
	KNEAD_FISTS,
	
	BUMPING,
	
	TALK,
	
	EXPLANATION,
	
	EXPLANATION_2,
	
	CAST,
	
	SIT_DOWN,
	
	STAND_UP,
	NONE11,
	NONE12,
	
	NONE13,
	NONE14,
	NONE15,
	NONE16,
	NONE17,
	NONE18,
	NONE19,
	NONE20,
	NONE21,
	NONE22,
	NONE23,
	NONE24;
	
	public static EmotionType[] VALUES = values();
	
	public static int SIZE = VALUES.length;
	
	/**
	 * Method valueOf.
	 * @param index int
	 * @return EmotionType
	 */
	public static EmotionType valueOf(int index)
	{
		if ((index < 0) || (index >= SIZE))
		{
			return EmotionType.INSPECTION;
		}
		
		return VALUES[index];
	}
}

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

/**
 * @author Ronn
 * @created 20.03.2012
 */
public abstract class PlayerGeomTable
{
	private static final float[][] tableHeight =
	{
		{
			45F,
			45F
		},
		{
			45F,
			45F
		},
		{
			50F,
			50F
		},
		{
			45F,
			45F
		},
		{
			33F,
			33F
		},
		{
			55F,
			55F
		},
	};
	private static final float[][] tableRadius =
	{
		{
			10F,
			10F
		},
		{
			10F,
			10F
		},
		{
			15F,
			10F
		},
		{
			10F,
			10F
		},
		{
			13F,
			8F
		},
		{
			20F,
			20F
		},
	};
	
	/**
	 * Method getHeight.
	 * @param race int
	 * @param sex int
	 * @return float
	 */
	public static float getHeight(int race, int sex)
	{
		return tableHeight[race][sex];
	}
	
	/**
	 * Method getRadius.
	 * @param race int
	 * @param sex int
	 * @return float
	 */
	public static float getRadius(int race, int sex)
	{
		return tableRadius[race][sex];
	}
}
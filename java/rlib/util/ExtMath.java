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
package rlib.util;

public final class ExtMath
{
	public static final float PI = 3.1415927f;
	
	public static float acos(float fValue)
	{
		if (-1.0f < fValue)
		{
			if (fValue < 1.0f)
			{
				return (float) Math.acos(fValue);
			}
			return 0.0f;
		}
		return 3.1415927f;
	}
	
	public static float invSqrt(float fValue)
	{
		return (float) (1.0 / Math.sqrt(fValue));
	}
	
	public static float sin(float v)
	{
		return (float) Math.sin(v);
	}
	
	public static float cos(float v)
	{
		return (float) Math.cos(v);
	}
	
	public static float sqrt(float fValue)
	{
		return (float) Math.sqrt(fValue);
	}
	
	private ExtMath() throws Exception
	{
		throw new Exception("ololo");
	}
}

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

import java.util.Random;

public abstract class Rnd
{
	private static final Random rnd = new Random();
	
	public static byte[] byteArray(int size)
	{
		byte[] result = new byte[size];
		int i = 0;
		while (i < size)
		{
			result[i] = (byte) Rnd.nextInt(128);
			++i;
		}
		return result;
	}
	
	public static boolean chance(float chance)
	{
		if (chance < 0.0f)
		{
			return false;
		}
		if (chance > 100.0f)
		{
			return true;
		}
		if ((Rnd.nextFloat() * Rnd.nextInt(100)) <= chance)
		{
			return true;
		}
		return false;
	}
	
	public static boolean chance(int chance)
	{
		if (chance < 1)
		{
			return false;
		}
		if (chance > 99)
		{
			return true;
		}
		if (Rnd.nextInt(99) <= chance)
		{
			return true;
		}
		return false;
	}
	
	public static float nextFloat()
	{
		return rnd.nextFloat();
	}
	
	public static int nextInt()
	{
		return rnd.nextInt();
	}
	
	public static int nextInt(int max)
	{
		return rnd.nextInt(max);
	}
	
	public static int nextInt(int min, int max)
	{
		return min + Rnd.nextInt(Math.abs(max - min) + 1);
	}
	
	public static long nextLong(long min, long max)
	{
		return min + Math.round((Rnd.nextFloat() * Math.abs(max - min)) + 1.0f);
	}
}

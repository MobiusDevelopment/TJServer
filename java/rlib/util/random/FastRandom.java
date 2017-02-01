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
package rlib.util.random;

public final class FastRandom implements Random
{
	private final java.util.Random random = new java.util.Random();
	
	@Override
	public void byteArray(byte[] array, int offset, int length)
	{
		int i = offset;
		while (i < (length += offset))
		{
			array[i] = (byte) nextInt(256);
			++i;
		}
	}
	
	@Override
	public boolean chance(float chance)
	{
		if (chance < 0.0f)
		{
			return false;
		}
		if (chance > 100.0f)
		{
			return true;
		}
		if ((nextFloat() * nextInt(100)) <= chance)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean chance(int chance)
	{
		if (chance < 1)
		{
			return false;
		}
		if (chance > 99)
		{
			return true;
		}
		if (nextInt(99) <= chance)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public float nextFloat()
	{
		return random.nextFloat();
	}
	
	@Override
	public int nextInt()
	{
		return random.nextInt();
	}
	
	@Override
	public int nextInt(int max)
	{
		return random.nextInt(max);
	}
	
	@Override
	public int nextInt(int min, int max)
	{
		return min + nextInt(Math.abs(max - min) + 1);
	}
	
	@Override
	public long nextLong(long min, long max)
	{
		return min + Math.round((nextFloat() * Math.abs(max - min)) + 1.0f);
	}
}

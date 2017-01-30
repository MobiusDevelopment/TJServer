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

public final class NumberUtils
{
	private NumberUtils()
	{
		throw new IllegalArgumentException();
	}
	
	public static long bytesToUInt(byte[] array, int offset, boolean bigEndian)
	{
		long value = 0;
		value = bigEndian ? (long) NumberUtils.makeInt(array[offset], array[offset + 1], array[offset + 2], array[offset + 3]) : (long) NumberUtils.makeInt(array[offset + 3], array[offset + 2], array[offset + 1], array[offset]);
		return value & 0xFFFFFFFFL;
	}
	
	public static int bytesToInt(byte[] array, int offset, boolean bigEndian)
	{
		if (bigEndian)
		{
			return NumberUtils.makeInt(array[offset], array[offset + 1], array[offset + 2], array[offset + 3]);
		}
		return NumberUtils.makeInt(array[offset + 3], array[offset + 2], array[offset + 1], array[offset]);
	}
	
	private static int makeInt(byte byte1, byte byte2, byte byte3, byte byte4)
	{
		return ((byte4 & 255) << 24) | ((byte3 & 255) << 16) | ((byte2 & 255) << 8) | (byte1 & 255);
	}
}

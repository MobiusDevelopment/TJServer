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
package rlib.util.wraps;

public final class Wraps
{
	public static Wrap newByteWrap(byte value, boolean usePool)
	{
		Wrap wrap = null;
		if (usePool)
		{
			wrap = WrapType.BYTE.take();
		}
		if (wrap == null)
		{
			wrap = new ByteWrap();
		}
		wrap.setByte(value);
		return wrap;
	}
	
	public static Wrap newCharWrap(char value, boolean usePool)
	{
		Wrap wrap = null;
		if (usePool)
		{
			wrap = WrapType.CHAR.take();
		}
		if (wrap == null)
		{
			wrap = new CharWrap();
		}
		wrap.setChar(value);
		return wrap;
	}
	
	public static Wrap newDoubleWrap(double value, boolean usePool)
	{
		Wrap wrap = null;
		if (usePool)
		{
			wrap = WrapType.DOUBLE.take();
		}
		if (wrap == null)
		{
			wrap = new DoubleWrap();
		}
		wrap.setDouble(value);
		return wrap;
	}
	
	public static Wrap newFloatWrap(float value, boolean usePool)
	{
		Wrap wrap = null;
		if (usePool)
		{
			wrap = WrapType.FLOAT.take();
		}
		if (wrap == null)
		{
			wrap = new FloatWrap();
		}
		wrap.setFloat(value);
		return wrap;
	}
	
	public static Wrap newIntegerWrap(int value, boolean usePool)
	{
		Wrap wrap = null;
		if (usePool)
		{
			wrap = WrapType.INTEGER.take();
		}
		if (wrap == null)
		{
			wrap = new IntegerWrap();
		}
		wrap.setInt(value);
		return wrap;
	}
	
	public static Wrap newLongWrap(long value, boolean usePool)
	{
		Wrap wrap = null;
		if (usePool)
		{
			wrap = WrapType.LONG.take();
		}
		if (wrap == null)
		{
			wrap = new LongWrap();
		}
		wrap.setLong(value);
		return wrap;
	}
	
	public static Wrap newObjectWrap(Object object, boolean usePool)
	{
		Wrap wrap = null;
		if (usePool)
		{
			wrap = WrapType.OBJECT.take();
		}
		if (wrap == null)
		{
			wrap = new ObjectWrap();
		}
		wrap.setObject(object);
		return wrap;
	}
	
	public static Wrap newShortWrap(short value, boolean usePool)
	{
		Wrap wrap = null;
		if (usePool)
		{
			wrap = WrapType.SHORT.take();
		}
		if (wrap == null)
		{
			wrap = new ShortWrap();
		}
		wrap.setShort(value);
		return wrap;
	}
	
	private Wraps()
	{
		throw new IllegalArgumentException();
	}
}

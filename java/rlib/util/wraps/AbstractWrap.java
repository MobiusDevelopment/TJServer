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

public abstract class AbstractWrap implements Wrap
{
	protected AbstractWrap()
	{
	}
	
	@Override
	public void finalyze()
	{
	}
	
	@Override
	public final void fold()
	{
		this.getWrapType().put(this);
	}
	
	@Override
	public byte getByte()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public char getChar()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public double getDouble()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public float getFloat()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public int getInt()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public long getLong()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public Object getObject()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public short getShort()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public void reinit()
	{
	}
	
	@Override
	public void setByte(byte value)
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public void setChar(char value)
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public void setDouble(double value)
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public void setFloat(float value)
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public void setInt(int value)
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public void setLong(long value)
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public void setObject(Object object)
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	@Override
	public void setShort(short value)
	{
		throw new IllegalArgumentException("not supported method.");
	}
}

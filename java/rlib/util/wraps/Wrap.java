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

import rlib.util.pools.Foldable;

public interface Wrap extends Foldable
{
	public void fold();
	
	public byte getByte();
	
	public char getChar();
	
	public double getDouble();
	
	public float getFloat();
	
	public int getInt();
	
	public long getLong();
	
	public Object getObject();
	
	public short getShort();
	
	public WrapType getWrapType();
	
	public void setByte(byte var1);
	
	public void setChar(char var1);
	
	public void setDouble(double var1);
	
	public void setFloat(float var1);
	
	public void setInt(int var1);
	
	public void setLong(long var1);
	
	public void setObject(Object var1);
	
	public void setShort(short var1);
}

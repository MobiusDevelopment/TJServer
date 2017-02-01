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
	void fold();
	
	byte getByte();
	
	char getChar();
	
	double getDouble();
	
	float getFloat();
	
	int getInt();
	
	long getLong();
	
	Object getObject();
	
	short getShort();
	
	WrapType getWrapType();
	
	void setByte(byte var1);
	
	void setChar(char var1);
	
	void setDouble(double var1);
	
	void setFloat(float var1);
	
	void setInt(int var1);
	
	void setLong(long var1);
	
	void setObject(Object var1);
	
	void setShort(short var1);
}

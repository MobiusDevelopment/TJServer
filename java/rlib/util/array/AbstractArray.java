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
package rlib.util.array;

public abstract class AbstractArray<E> implements Array<E>
{
	private static final long serialVersionUID = 2113052245369887690L;
	protected static final int DEFAULT_SIZE = 10;
	
	public AbstractArray(Class<E> type)
	{
		this(type, 10);
	}
	
	@SuppressWarnings("unchecked")
	public AbstractArray(Class<E> type, int size)
	{
		if (size < 0)
		{
			throw new IllegalArgumentException("negative size");
		}
		this.setSize(0);
		this.setArray((E[]) java.lang.reflect.Array.newInstance(type, size));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final boolean containsAll(Array<?> array)
	{
		E[] elements = (E[]) array.array();
		int i = 0;
		int length = array.size();
		while (i < length)
		{
			if (!contains(elements[i]))
			{
				return false;
			}
			++i;
		}
		return true;
	}
	
	@Override
	public final boolean containsAll(Object[] array)
	{
		int i = 0;
		int length = array.length;
		while (i < length)
		{
			if (!contains(array[i]))
			{
				return false;
			}
			++i;
		}
		return true;
	}
	
	@Override
	public boolean fastRemove(Object object)
	{
		if (fastRemove(indexOf(object)) != null)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public void finalyze()
	{
		clear();
	}
	
	@Override
	public void readLock()
	{
	}
	
	@Override
	public void readUnlock()
	{
	}
	
	@Override
	public void reinit()
	{
	}
	
	protected abstract void setArray(E[] var1);
	
	protected abstract void setSize(int var1);
	
	@Override
	public final boolean slowRemove(Object object)
	{
		if (slowRemove(indexOf(object)) != null)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(getClass().getSimpleName()) + " size = " + size() + " : " + Arrays.toString(this);
	}
	
	@Override
	public void writeLock()
	{
	}
	
	@Override
	public void writeUnlock()
	{
	}
}

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

public class FastLongArray implements LongArray
{
	protected long[] array;
	protected int size;
	
	public FastLongArray()
	{
		this(10);
	}
	
	public FastLongArray(int size)
	{
		array = new long[size];
		this.size = 0;
	}
	
	@Override
	public FastLongArray add(long element)
	{
		if (size == array.length)
		{
			array = Arrays.copyOf(array, ((array.length * 3) / 2) + 1);
		}
		array[size++] = element;
		return this;
	}
	
	@Override
	public final FastLongArray addAll(long[] elements)
	{
		if ((elements == null) || (elements.length < 1))
		{
			return this;
		}
		int diff = (size + elements.length) - array.length;
		if (diff > 0)
		{
			array = Arrays.copyOf(array, diff);
		}
		int i = 0;
		int length = elements.length;
		while (i < length)
		{
			add(elements[i]);
			++i;
		}
		return this;
	}
	
	@Override
	public final FastLongArray addAll(LongArray elements)
	{
		if ((elements == null) || elements.isEmpty())
		{
			return this;
		}
		int diff = (size + elements.size()) - array.length;
		if (diff > 0)
		{
			array = Arrays.copyOf(array, diff);
		}
		array = elements.array();
		int i = 0;
		int length = elements.size();
		while (i < length)
		{
			add(array[i]);
			++i;
		}
		return this;
	}
	
	@Override
	public final long[] array()
	{
		return array;
	}
	
	@Override
	public final FastLongArray clear()
	{
		size = 0;
		return this;
	}
	
	@Override
	public final boolean contains(long element)
	{
		long[] array = array();
		int i = 0;
		int length = size;
		while (i < length)
		{
			if (array[i] == element)
			{
				return true;
			}
			++i;
		}
		return false;
	}
	
	@Override
	public final boolean containsAll(long[] array)
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
	public final boolean containsAll(LongArray array)
	{
		long[] elements = array.array();
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
	public final boolean fastRemove(int index)
	{
		if ((index < 0) || (size < 1) || (index >= size))
		{
			return false;
		}
		long[] array = array();
		--size;
		array[index] = array[size];
		array[size] = 0;
		return true;
	}
	
	@Override
	public boolean fastRemove(long element)
	{
		int index = indexOf(element);
		if (index > -1)
		{
			fastRemove(index);
		}
		if (index > -1)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public final long first()
	{
		if (size < 1)
		{
			return 0;
		}
		return array[0];
	}
	
	@Override
	public final long get(int index)
	{
		return array[index];
	}
	
	@Override
	public final int indexOf(long element)
	{
		long[] array = array();
		int i = 0;
		int length = size;
		while (i < length)
		{
			long val = array[i];
			if (element == val)
			{
				return i;
			}
			++i;
		}
		return -1;
	}
	
	@Override
	public final boolean isEmpty()
	{
		if (size < 1)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public final ArrayIterator<Long> iterator()
	{
		return new FastIterator();
	}
	
	@Override
	public final long last()
	{
		if (size < 1)
		{
			return 0;
		}
		return array[size - 1];
	}
	
	@Override
	public final int lastIndexOf(long element)
	{
		long[] array = array();
		int last = -1;
		int i = 0;
		int length = size;
		while (i < length)
		{
			long val = array[i];
			if (element == val)
			{
				last = i;
			}
			++i;
		}
		return last;
	}
	
	@Override
	public final long poll()
	{
		long val = first();
		slowRemove(0);
		return val;
	}
	
	@Override
	public final long pop()
	{
		long last = last();
		fastRemove(size - 1);
		return last;
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
	public final boolean removeAll(LongArray target)
	{
		if (target.isEmpty())
		{
			return true;
		}
		long[] array = target.array();
		int i = 0;
		int length = target.size();
		while (i < length)
		{
			fastRemove(array[i]);
			++i;
		}
		return true;
	}
	
	@Override
	public final boolean retainAll(LongArray target)
	{
		long[] array = array();
		int i = 0;
		int length = size;
		while (i < length)
		{
			if (!target.contains(array[i]))
			{
				fastRemove(i--);
				--length;
			}
			++i;
		}
		return true;
	}
	
	@Override
	public final int size()
	{
		return size;
	}
	
	@Override
	public final boolean slowRemove(int index)
	{
		if ((index < 0) || (size < 1))
		{
			return false;
		}
		long[] array = array();
		int numMoved = size - index - 1;
		if (numMoved > 0)
		{
			System.arraycopy(array, index + 1, array, index, numMoved);
		}
		--size;
		array[size] = 0;
		return true;
	}
	
	@Override
	public boolean slowRemove(long element)
	{
		int index = indexOf(element);
		if (index > -1)
		{
			slowRemove(index);
		}
		if (index > -1)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public final FastLongArray sort()
	{
		Arrays.sort(array, 0, size);
		return this;
	}
	
	@Override
	public final long[] toArray(long[] container)
	{
		long[] array = array();
		if (container.length >= size)
		{
			int i = 0;
			int j = 0;
			int length = array.length;
			int newLength = container.length;
			while ((i < length) && (j < newLength))
			{
				container[j++] = array[i];
				++i;
			}
			return container;
		}
		return array;
	}
	
	@Override
	public String toString()
	{
		return Arrays.toString(this);
	}
	
	@Override
	public final FastLongArray trimToSize()
	{
		long[] array = array();
		if (size == array.length)
		{
			return this;
		}
		array = Arrays.copyOfRange(array, 0, size);
		return this;
	}
	
	@Override
	public void writeLock()
	{
	}
	
	@Override
	public void writeUnlock()
	{
	}
	
	private final class FastIterator implements ArrayIterator<Long>
	{
		private int ordinal;
		
		public FastIterator()
		{
			ordinal = 0;
		}
		
		@Override
		public void fastRemove()
		{
			FastLongArray.this.fastRemove(--ordinal);
		}
		
		@Override
		public boolean hasNext()
		{
			if (ordinal < size)
			{
				return true;
			}
			return false;
		}
		
		@Override
		public int index()
		{
			return ordinal - 1;
		}
		
		@Override
		public Long next()
		{
			return array[ordinal++];
		}
		
		@Override
		public void remove()
		{
			FastLongArray.this.fastRemove(--ordinal);
		}
		
		@Override
		public void slowRemove()
		{
			FastLongArray.this.slowRemove(--ordinal);
		}
	}
	
}

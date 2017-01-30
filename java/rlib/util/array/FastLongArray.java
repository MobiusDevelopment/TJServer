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
		this.array = new long[size];
		this.size = 0;
	}
	
	@Override
	public FastLongArray add(long element)
	{
		if (this.size == this.array.length)
		{
			this.array = Arrays.copyOf(this.array, ((this.array.length * 3) / 2) + 1);
		}
		this.array[this.size++] = element;
		return this;
	}
	
	@Override
	public final FastLongArray addAll(long[] elements)
	{
		if ((elements == null) || (elements.length < 1))
		{
			return this;
		}
		int diff = (this.size + elements.length) - this.array.length;
		if (diff > 0)
		{
			this.array = Arrays.copyOf(this.array, diff);
		}
		int i = 0;
		int length = elements.length;
		while (i < length)
		{
			this.add(elements[i]);
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
		int diff = (this.size + elements.size()) - this.array.length;
		if (diff > 0)
		{
			this.array = Arrays.copyOf(this.array, diff);
		}
		this.array = elements.array();
		int i = 0;
		int length = elements.size();
		while (i < length)
		{
			this.add(this.array[i]);
			++i;
		}
		return this;
	}
	
	@Override
	public final long[] array()
	{
		return this.array;
	}
	
	@Override
	public final FastLongArray clear()
	{
		this.size = 0;
		return this;
	}
	
	@Override
	public final boolean contains(long element)
	{
		long[] array = this.array();
		int i = 0;
		int length = this.size;
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
			if (!this.contains(array[i]))
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
			if (!this.contains(elements[i]))
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
		if ((index < 0) || (this.size < 1) || (index >= this.size))
		{
			return false;
		}
		long[] array = this.array();
		--this.size;
		array[index] = array[this.size];
		array[this.size] = 0;
		return true;
	}
	
	@Override
	public boolean fastRemove(long element)
	{
		int index = this.indexOf(element);
		if (index > -1)
		{
			this.fastRemove(index);
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
		if (this.size < 1)
		{
			return 0;
		}
		return this.array[0];
	}
	
	@Override
	public final long get(int index)
	{
		return this.array[index];
	}
	
	@Override
	public final int indexOf(long element)
	{
		long[] array = this.array();
		int i = 0;
		int length = this.size;
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
		if (this.size < 1)
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
		if (this.size < 1)
		{
			return 0;
		}
		return this.array[this.size - 1];
	}
	
	@Override
	public final int lastIndexOf(long element)
	{
		long[] array = this.array();
		int last = -1;
		int i = 0;
		int length = this.size;
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
		long val = this.first();
		this.slowRemove(0);
		return val;
	}
	
	@Override
	public final long pop()
	{
		long last = this.last();
		this.fastRemove(this.size - 1);
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
			this.fastRemove(array[i]);
			++i;
		}
		return true;
	}
	
	@Override
	public final boolean retainAll(LongArray target)
	{
		long[] array = this.array();
		int i = 0;
		int length = this.size;
		while (i < length)
		{
			if (!target.contains(array[i]))
			{
				this.fastRemove(i--);
				--length;
			}
			++i;
		}
		return true;
	}
	
	@Override
	public final int size()
	{
		return this.size;
	}
	
	@Override
	public final boolean slowRemove(int index)
	{
		if ((index < 0) || (this.size < 1))
		{
			return false;
		}
		long[] array = this.array();
		int numMoved = this.size - index - 1;
		if (numMoved > 0)
		{
			System.arraycopy(array, index + 1, array, index, numMoved);
		}
		--this.size;
		array[this.size] = 0;
		return true;
	}
	
	@Override
	public boolean slowRemove(long element)
	{
		int index = this.indexOf(element);
		if (index > -1)
		{
			this.slowRemove(index);
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
		Arrays.sort(this.array, 0, this.size);
		return this;
	}
	
	@Override
	public final long[] toArray(long[] container)
	{
		long[] array = this.array();
		if (container.length >= this.size)
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
		long[] array = this.array();
		if (this.size == array.length)
		{
			return this;
		}
		array = Arrays.copyOfRange(array, 0, this.size);
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
			this.ordinal = 0;
		}
		
		@Override
		public void fastRemove()
		{
			FastLongArray.this.fastRemove(--this.ordinal);
		}
		
		@Override
		public boolean hasNext()
		{
			if (this.ordinal < FastLongArray.this.size)
			{
				return true;
			}
			return false;
		}
		
		@Override
		public int index()
		{
			return this.ordinal - 1;
		}
		
		@Override
		public Long next()
		{
			return FastLongArray.this.array[this.ordinal++];
		}
		
		@Override
		public void remove()
		{
			FastLongArray.this.fastRemove(--this.ordinal);
		}
		
		@Override
		public void slowRemove()
		{
			FastLongArray.this.slowRemove(--this.ordinal);
		}
	}
	
}

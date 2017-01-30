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

import java.util.Comparator;

public final class SynchronizedArray<E> extends AbstractArray<E>
{
	private static final long serialVersionUID = -8477384427415127978L;
	private E[] array;
	private int size;
	
	public SynchronizedArray(Class<E> type)
	{
		super(type);
	}
	
	public SynchronizedArray(Class<E> type, int size)
	{
		super(type, size);
	}
	
	@Override
	public synchronized SynchronizedArray<E> add(E element)
	{
		if (this.size == this.array.length)
		{
			this.array = Arrays.copyOf(this.array, ((this.array.length * 3) / 2) + 1);
		}
		this.array[this.size++] = element;
		return this;
	}
	
	@Override
	public synchronized SynchronizedArray<E> addAll(Array<? extends E> addArray)
	{
		if ((addArray == null) || addArray.isEmpty())
		{
			return this;
		}
		int diff = (this.size + addArray.size()) - this.array.length;
		if (diff > 0)
		{
			this.array = Arrays.copyOf(this.array, diff);
		}
		E[] array = addArray.array();
		int i = 0;
		int length = addArray.size();
		while (i < length)
		{
			this.add(array[i]);
			++i;
		}
		return this;
	}
	
	@Override
	public synchronized Array<E> addAll(E[] addArray)
	{
		if ((addArray == null) || (addArray.length < 1))
		{
			return this;
		}
		int diff = (this.size + addArray.length) - this.array.length;
		if (diff > 0)
		{
			this.array = Arrays.copyOf(this.array, diff);
		}
		int i = 0;
		int length = addArray.length;
		while (i < length)
		{
			this.add(addArray[i]);
			++i;
		}
		return this;
	}
	
	@Override
	public E[] array()
	{
		return this.array;
	}
	
	@Override
	public synchronized SynchronizedArray<E> clear()
	{
		int i = 0;
		while (i < this.size)
		{
			this.array[i] = null;
			++i;
		}
		this.size = 0;
		return this;
	}
	
	@Override
	public synchronized boolean contains(Object object)
	{
		int i = 0;
		while (i < this.size)
		{
			if (this.array[i].equals(object))
			{
				return true;
			}
			++i;
		}
		return false;
	}
	
	@Override
	public synchronized E fastRemove(int index)
	{
		if ((index < 0) || (this.size < 1))
		{
			return null;
		}
		E old = this.array[index];
		this.array[index] = this.array[--this.size];
		this.array[this.size] = null;
		return old;
	}
	
	@Override
	public synchronized E first()
	{
		if (this.size < 1)
		{
			return null;
		}
		return this.array[0];
	}
	
	@Override
	public synchronized E get(int index)
	{
		return this.array[index];
	}
	
	@Override
	public synchronized int indexOf(Object object)
	{
		if (object == null)
		{
			return -1;
		}
		int i = 0;
		while (i < this.size)
		{
			if (this.array[i].equals(object))
			{
				return i;
			}
			++i;
		}
		return -1;
	}
	
	@Override
	public boolean isEmpty()
	{
		if (this.size < 1)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public ArrayIterator<E> iterator()
	{
		return new FastIterator(this);
	}
	
	@Override
	public synchronized E last()
	{
		if (this.size < 1)
		{
			return null;
		}
		return this.array[this.size - 1];
	}
	
	@Override
	public synchronized int lastIndexOf(Object object)
	{
		if (object == null)
		{
			return -1;
		}
		int last = -1;
		int i = 0;
		while (i < this.size)
		{
			E element = this.array[i];
			if (element.equals(object))
			{
				last = i;
			}
			++i;
		}
		return last;
	}
	
	@Override
	public E poll()
	{
		return this.slowRemove(0);
	}
	
	@Override
	public E pop()
	{
		return this.fastRemove(this.size - 1);
	}
	
	@Override
	public synchronized boolean removeAll(Array<?> targetArray)
	{
		if (targetArray.isEmpty())
		{
			return true;
		}
		E[] array = (E[]) targetArray.array();
		int i = 0;
		int length = targetArray.size();
		while (i < length)
		{
			this.fastRemove(array[i]);
			++i;
		}
		return true;
	}
	
	@Override
	public synchronized boolean retainAll(Array<?> targetArray)
	{
		int i = 0;
		while (i < this.size)
		{
			if (!targetArray.contains(this.array[i]))
			{
				this.fastRemove(i--);
			}
			++i;
		}
		return true;
	}
	
	@Override
	public synchronized E search(E required, Search<E> search)
	{
		int i = 0;
		while (i < this.size)
		{
			E element = this.array[i];
			if (search.compare(required, element))
			{
				return element;
			}
			++i;
		}
		return null;
	}
	
	@Override
	public synchronized void set(int index, E element)
	{
		if ((index < this.size) || (element == null))
		{
			return;
		}
		if (this.array[index] != null)
		{
			--this.size;
		}
		this.array[index] = element;
		++this.size;
	}
	
	@Override
	protected void setArray(E[] array)
	{
		this.array = array;
	}
	
	@Override
	protected void setSize(int size)
	{
		this.size = size;
	}
	
	@Override
	public int size()
	{
		return this.size;
	}
	
	@Override
	public synchronized E slowRemove(int index)
	{
		if ((index < 0) || (this.size < 1))
		{
			return null;
		}
		int numMoved = this.size - index - 1;
		E old = this.array[index];
		if (numMoved > 0)
		{
			System.arraycopy(this.array, index + 1, this.array, index, numMoved);
		}
		this.array[--this.size] = null;
		return old;
	}
	
	@Override
	public synchronized SynchronizedArray<E> sort(Comparator<E> comparator)
	{
		Arrays.sort(this.array, comparator);
		return this;
	}
	
	@Override
	public synchronized <T> T[] toArray(T[] newArray)
	{
		if (newArray.length >= this.size)
		{
			int i = 0;
			int j = 0;
			int length = this.array.length;
			int newLength = newArray.length;
			while ((i < length) && (j < newLength))
			{
				if (this.array[i] != null)
				{
					newArray[j++] = (T) this.array[i];
				}
				++i;
			}
			return newArray;
		}
		return (T[]) this.array;
	}
	
	@Override
	public synchronized SynchronizedArray<E> trimToSize()
	{
		if (this.size == this.array.length)
		{
			return this;
		}
		this.array = Arrays.copyOfRange(this.array, 0, this.size);
		return this;
	}
	
	@Override
	public synchronized void apply(FuncElement<? super E> func)
	{
		E[] array = this.array();
		int i = 0;
		int length = this.size;
		while (i < length)
		{
			func.apply(array[i]);
			++i;
		}
	}
	
	private final class FastIterator implements ArrayIterator<E>
	{
		private int ordinal;
		final /* synthetic */ SynchronizedArray this$0;
		
		private FastIterator(SynchronizedArray synchronizedArray)
		{
			this.this$0 = synchronizedArray;
		}
		
		@Override
		public void fastRemove()
		{
			this.this$0.fastRemove(--this.ordinal);
		}
		
		@Override
		public boolean hasNext()
		{
			if (this.ordinal < this.this$0.size)
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
		public E next()
		{
			return (E) this.this$0.array[this.ordinal++];
		}
		
		@Override
		public void remove()
		{
			this.this$0.fastRemove(--this.ordinal);
		}
		
		@Override
		public void slowRemove()
		{
			this.this$0.slowRemove(--this.ordinal);
		}
	}
}

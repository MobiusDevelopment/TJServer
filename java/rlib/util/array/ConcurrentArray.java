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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import rlib.concurrent.Locks;

public class ConcurrentArray<E> extends AbstractArray<E>
{
	private static final long serialVersionUID = 1;
	private final Lock readLock;
	private final Lock writeLock;
	private volatile E[] array;
	private volatile int size;
	
	public ConcurrentArray(Class<E> type)
	{
		this(type, 10);
	}
	
	public ConcurrentArray(Class<E> type, int size)
	{
		super(type, size);
		ReadWriteLock readWriteLock = Locks.newRWLock();
		this.readLock = readWriteLock.readLock();
		this.writeLock = readWriteLock.writeLock();
	}
	
	@Override
	public ConcurrentArray<E> add(E element)
	{
		this.writeLock();
		try
		{
			if (this.size < 0)
			{
				this.size = 0;
			}
			if (this.size >= this.array.length)
			{
				this.array = Arrays.copyOf(this.array, ((this.array.length * 3) / 2) + 1);
			}
			this.array[this.size] = element;
			++this.size;
			ConcurrentArray concurrentArray = this;
			return concurrentArray;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final ConcurrentArray<E> addAll(Array<? extends E> elements)
	{
		if ((elements == null) || elements.isEmpty())
		{
			return this;
		}
		this.writeLock();
		try
		{
			int diff = (this.size + elements.size()) - this.array.length;
			if (diff > 0)
			{
				this.array = Arrays.copyOf(this.array, diff);
			}
			E[] array = elements.array();
			int i = 0;
			int length = elements.size();
			while (i < length)
			{
				this.add(array[i]);
				++i;
			}
			ConcurrentArray concurrentArray = this;
			return concurrentArray;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final Array<E> addAll(E[] elements)
	{
		if ((elements == null) || (elements.length < 1))
		{
			return this;
		}
		this.writeLock();
		try
		{
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
			ConcurrentArray concurrentArray = this;
			return concurrentArray;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final E[] array()
	{
		return this.array;
	}
	
	@Override
	public final ConcurrentArray<E> clear()
	{
		this.writeLock();
		try
		{
			Arrays.clear(this.array);
			this.size = 0;
			ConcurrentArray concurrentArray = this;
			return concurrentArray;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final boolean contains(Object object)
	{
		this.readLock();
		try
		{
			E[] array = this.array();
			int i = 0;
			int length = this.size;
			while (i < length)
			{
				if (array[i].equals(object))
				{
					return true;
				}
				++i;
			}
			return false;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final E fastRemove(int index)
	{
		if (index < 0)
		{
			return null;
		}
		this.writeLock();
		try
		{
			E[] array = this.array();
			if ((this.size < 1) || (index >= this.size))
			{
				return null;
			}
			--this.size;
			E old = array[index];
			array[index] = array[this.size];
			array[this.size] = null;
			E e = old;
			return e;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final E first()
	{
		this.readLock();
		try
		{
			if (this.size < 1)
			{
				return null;
			}
			E e = this.array[0];
			return e;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final E get(int index)
	{
		this.readLock();
		try
		{
			E e = this.array[index];
			return e;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final int indexOf(Object object)
	{
		if (object == null)
		{
			return -1;
		}
		this.readLock();
		try
		{
			E[] array = this.array();
			int i = 0;
			int length = this.size;
			while (i < length)
			{
				E element = array[i];
				if (element.equals(object))
				{
					int n = i;
					return n;
				}
				++i;
			}
			return -1;
		}
		finally
		{
			this.readUnlock();
		}
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
	public final ArrayIterator<E> iterator()
	{
		return new FastIterator(this);
	}
	
	@Override
	public final E last()
	{
		this.readLock();
		try
		{
			if (this.size < 1)
			{
				return null;
			}
			E e = this.array[this.size - 1];
			return e;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final int lastIndexOf(Object object)
	{
		if (object == null)
		{
			return -1;
		}
		this.readLock();
		try
		{
			E[] array = this.array();
			int last = -1;
			int i = 0;
			int length = this.size;
			while (i < length)
			{
				E element = array[i];
				if (element.equals(object))
				{
					last = i;
				}
				++i;
			}
			int n = last;
			return n;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final E poll()
	{
		return this.slowRemove(0);
	}
	
	@Override
	public final E pop()
	{
		return this.fastRemove(this.size - 1);
	}
	
	@Override
	public final void readLock()
	{
		this.readLock.lock();
	}
	
	@Override
	public final void readUnlock()
	{
		this.readLock.unlock();
	}
	
	@Override
	public final boolean removeAll(Array<?> target)
	{
		if (target.isEmpty())
		{
			return true;
		}
		this.writeLock();
		try
		{
			E[] array = (E[]) target.array();
			int i = 0;
			int length = target.size();
			while (i < length)
			{
				fastRemove(array[i]);
				++i;
			}
		}
		finally
		{
			this.writeUnlock();
		}
		return true;
	}
	
	@Override
	public final boolean retainAll(Array<?> target)
	{
		this.writeLock();
		try
		{
			E[] array = this.array();
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
		}
		finally
		{
			this.writeUnlock();
		}
		return true;
	}
	
	@Override
	public final E search(E required, Search<E> search)
	{
		this.readLock();
		try
		{
			E[] array = this.array();
			int i = 0;
			int length = this.size;
			while (i < length)
			{
				E element = array[i];
				if (search.compare(required, element))
				{
					E e = element;
					return e;
				}
				++i;
			}
			return null;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final void set(int index, E element)
	{
		if ((index < 0) || (index >= this.size) || (element == null))
		{
			return;
		}
		this.writeLock();
		try
		{
			E[] array = this.array();
			if (array[index] != null)
			{
				--this.size;
			}
			array[index] = element;
			++this.size;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	protected final void setArray(E[] array)
	{
		this.array = array;
	}
	
	@Override
	protected final void setSize(int size)
	{
		this.size = size;
	}
	
	@Override
	public final int size()
	{
		return this.size;
	}
	
	@Override
	public final E slowRemove(int index)
	{
		if ((index < 0) || (this.size < 1))
		{
			return null;
		}
		this.writeLock();
		try
		{
			E[] array = this.array();
			int numMoved = this.size - index - 1;
			E old = array[index];
			if (numMoved > 0)
			{
				System.arraycopy(array, index + 1, array, index, numMoved);
			}
			--this.size;
			array[this.size] = null;
			E e = old;
			return e;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final ConcurrentArray<E> sort(Comparator<E> comparator)
	{
		this.writeLock();
		try
		{
			Arrays.sort(this.array, comparator);
			ConcurrentArray concurrentArray = this;
			return concurrentArray;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final <T> T[] toArray(T[] container)
	{
		this.readLock();
		try
		{
			E[] array = this.array();
			if (container.length >= this.size)
			{
				int i = 0;
				int j = 0;
				int length = array.length;
				int newLength = container.length;
				while ((i < length) && (j < newLength))
				{
					if (array[i] != null)
					{
						container[j++] = (T) array[i];
					}
					++i;
				}
				T[] arrT = container;
				return arrT;
			}
			E[] arrE = array;
			return (T[]) arrE;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final ConcurrentArray<E> trimToSize()
	{
		if (this.size == this.array.length)
		{
			return this;
		}
		this.writeLock();
		try
		{
			this.array = Arrays.copyOfRange(this.array, 0, this.size);
			ConcurrentArray concurrentArray = this;
			return concurrentArray;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final void writeLock()
	{
		this.writeLock.lock();
	}
	
	@Override
	public final void writeUnlock()
	{
		this.writeLock.unlock();
	}
	
	@Override
	public void apply(FuncElement<? super E> func)
	{
		this.readLock();
		try
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
		finally
		{
			this.readUnlock();
		}
	}
	
	private final class FastIterator implements ArrayIterator<E>
	{
		private int ordinal;
		final /* synthetic */ ConcurrentArray this$0;
		
		private FastIterator(ConcurrentArray concurrentArray)
		{
			this.this$0 = concurrentArray;
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
			if (this.ordinal >= this.this$0.array.length)
			{
				return null;
			}
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

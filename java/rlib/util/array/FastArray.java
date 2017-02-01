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

public class FastArray<E> extends AbstractArray<E>
{
	private static final long serialVersionUID = -8477384427415127978L;
	protected E[] array;
	protected int size;
	
	public FastArray(Class<E> type)
	{
		super(type);
	}
	
	public FastArray(Class<E> type, int size)
	{
		super(type, size);
	}
	
	@Override
	public FastArray<E> add(E element)
	{
		if (this.size == this.array.length)
		{
			this.array = Arrays.copyOf(this.array, ((this.array.length * 3) / 2) + 1);
		}
		this.array[this.size++] = element;
		return this;
	}
	
	@Override
	public final FastArray<E> addAll(Array<? extends E> elements)
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
		E[] array = elements.array();
		int i = 0;
		int length = elements.size();
		while (i < length)
		{
			this.add(array[i]);
			++i;
		}
		return this;
	}
	
	@Override
	public final Array<E> addAll(E[] elements)
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
	public final E[] array()
	{
		return this.array;
	}
	
	@Override
	public final FastArray<E> clear()
	{
		Arrays.clear(this.array);
		this.size = 0;
		return this;
	}
	
	@Override
	public final boolean contains(Object object)
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
	
	@Override
	public final E fastRemove(int index)
	{
		E[] array = this.array();
		if ((index < 0) || (this.size < 1) || (index >= this.size))
		{
			return null;
		}
		--this.size;
		E old = array[index];
		array[index] = array[this.size];
		array[this.size] = null;
		return old;
	}
	
	@Override
	public final E first()
	{
		if (this.size < 1)
		{
			return null;
		}
		return this.array[0];
	}
	
	@Override
	public final E get(int index)
	{
		return this.array[index];
	}
	
	@Override
	public final int indexOf(Object object)
	{
		if (object == null)
		{
			return -1;
		}
		E[] array = this.array();
		int i = 0;
		int length = this.size;
		while (i < length)
		{
			E element = array[i];
			if (element.equals(object))
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
	public final ArrayIterator<E> iterator()
	{
		return new FastIterator();
	}
	
	@Override
	public final E last()
	{
		if (this.size < 1)
		{
			return null;
		}
		return this.array[this.size - 1];
	}
	
	@Override
	public final int lastIndexOf(Object object)
	{
		if (object == null)
		{
			return -1;
		}
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
		return last;
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
	public final boolean removeAll(Array<?> target)
	{
		if (target.isEmpty())
		{
			return true;
		}
		E[] array = (E[]) target.array();
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
	public final boolean retainAll(Array<?> target)
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
		return true;
	}
	
	@Override
	public final E search(E required, Search<E> search)
	{
		E[] array = this.array();
		int i = 0;
		int length = this.size;
		while (i < length)
		{
			E element = array[i];
			if (search.compare(required, element))
			{
				return element;
			}
			++i;
		}
		return null;
	}
	
	@Override
	public final void set(int index, E element)
	{
		if ((index < 0) || (index >= this.size) || (element == null))
		{
			return;
		}
		E[] array = this.array();
		if (array[index] != null)
		{
			--this.size;
		}
		array[index] = element;
		++this.size;
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
		E[] array = this.array();
		int numMoved = this.size - index - 1;
		E old = array[index];
		if (numMoved > 0)
		{
			System.arraycopy(array, index + 1, array, index, numMoved);
		}
		--this.size;
		array[this.size] = null;
		return old;
	}
	
	@Override
	public final FastArray<E> sort(Comparator<E> comparator)
	{
		Arrays.sort(this.array, comparator);
		return this;
	}
	
	@Override
	public final <T> T[] toArray(T[] container)
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
			return container;
		}
		return (T[]) array;
	}
	
	@Override
	public final FastArray<E> trimToSize()
	{
		if (this.size == this.array.length)
		{
			return this;
		}
		this.array = Arrays.copyOfRange(this.array, 0, this.size);
		return this;
	}
	
	@Override
	public void apply(FuncElement<? super E> func)
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
		
		public FastIterator()
		{
			this.ordinal = 0;
		}
		
		@Override
		public void fastRemove()
		{
			FastArray.this.fastRemove(--this.ordinal);
		}
		
		@Override
		public boolean hasNext()
		{
			if (this.ordinal < FastArray.this.size)
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
			return FastArray.this.array[this.ordinal++];
		}
		
		@Override
		public void remove()
		{
			FastArray.this.fastRemove(--this.ordinal);
		}
		
		@Override
		public void slowRemove()
		{
			FastArray.this.slowRemove(--this.ordinal);
		}
	}
	
}

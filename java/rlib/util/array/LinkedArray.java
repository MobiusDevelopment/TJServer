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

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

public class LinkedArray<E> extends AbstractArray<E>
{
	private static final long serialVersionUID = 6363213482131539669L;
	private final FoldablePool<Node<E>> pool = Pools.newFoldablePool(Node.class);
	private Node<E> first;
	private Node<E> last;
	private int size;
	
	public LinkedArray(Class<E> type)
	{
		super(type);
	}
	
	private void addToLast(E element)
	{
		Node<E> last = this.getLast();
		Node<E> node = this.getNewNode(last, element, null);
		this.setLast(node);
		if (last == null)
		{
			this.setFirst(node);
		}
		else
		{
			last.setNext(node);
		}
		++this.size;
	}
	
	public void setLast(Node<E> last)
	{
		this.last = last;
	}
	
	public void setFirst(Node<E> first)
	{
		this.first = first;
	}
	
	private Node<E> getNewNode(Node<E> prev, E item, Node<E> next)
	{
		Node<E> node = this.pool.take();
		if (node == null)
		{
			node = new Node();
		}
		node.setItem(item);
		node.setNext(next);
		node.setPrev(prev);
		return node;
	}
	
	private Node<E> getFirst()
	{
		return this.first;
	}
	
	private Node<E> getLast()
	{
		return this.last;
	}
	
	@Override
	public Array<E> add(E object)
	{
		this.addToLast(object);
		return this;
	}
	
	@Override
	public Array<E> addAll(Array<? extends E> array)
	{
		for (E element : array)
		{
			this.add(element);
		}
		System.out.println(this.getClass() + ".addAll() is not recomended method.");
		return this;
	}
	
	@Override
	public Array<E> addAll(E[] array)
	{
		E[] arrE = array;
		int n = arrE.length;
		int n2 = 0;
		while (n2 < n)
		{
			E element = arrE[n2];
			this.add(element);
			++n2;
		}
		System.out.println(this.getClass() + ".addAll() is not recomended method.");
		return this;
	}
	
	@Override
	public E[] array()
	{
		throw new IllegalArgumentException("not supported method.");
	}
	
	public FoldablePool<Node<E>> getPool()
	{
		return this.pool;
	}
	
	@Override
	public Array<E> clear()
	{
		FoldablePool<Node<E>> pool = this.getPool();
		Node<E> node = this.getFirst();
		while (node != null)
		{
			pool.put(node);
			node = node.getNext();
		}
		this.setFirst(null);
		this.setLast(null);
		this.size = 0;
		return null;
	}
	
	@Override
	public boolean contains(Object object)
	{
		Node<E> node = this.getFirst();
		while (node != null)
		{
			E item = node.getItem();
			if (item.equals(object))
			{
				return true;
			}
			node = node.getNext();
		}
		return false;
	}
	
	@Override
	public E fastRemove(int index)
	{
		return null;
	}
	
	@Override
	public E first()
	{
		Node<E> first = this.getFirst();
		return first == null ? null : (E) first.getItem();
	}
	
	@Override
	public E get(int index)
	{
		int size = this.size();
		if (index < (size >> 1))
		{
			int i = 0;
			Node<E> node = this.getFirst();
			while (node != null)
			{
				if (i == index)
				{
					return node.getItem();
				}
				++i;
				node = node.getNext();
			}
		}
		else
		{
			int i = size - 1;
			Node<E> node = this.getLast();
			while (node != null)
			{
				if (i == index)
				{
					return node.getItem();
				}
				--i;
				node = node.getPrev();
			}
		}
		return null;
	}
	
	@Override
	public int indexOf(Object object)
	{
		return 0;
	}
	
	@Override
	public boolean isEmpty()
	{
		return false;
	}
	
	@Override
	public ArrayIterator<E> iterator()
	{
		return null;
	}
	
	@Override
	public E last()
	{
		return null;
	}
	
	@Override
	public int lastIndexOf(Object object)
	{
		return 0;
	}
	
	@Override
	public E poll()
	{
		return null;
	}
	
	@Override
	public E pop()
	{
		return null;
	}
	
	@Override
	public boolean removeAll(Array<?> array)
	{
		return false;
	}
	
	@Override
	public boolean retainAll(Array<?> array)
	{
		return false;
	}
	
	@Override
	public E search(E required, Search<E> search)
	{
		return null;
	}
	
	@Override
	public void set(int index, E element)
	{
	}
	
	@Override
	public int size()
	{
		return 0;
	}
	
	@Override
	public E slowRemove(int index)
	{
		return null;
	}
	
	@Override
	public Array<E> sort(Comparator<E> comparator)
	{
		return null;
	}
	
	@Override
	public <T> T[] toArray(T[] newArray)
	{
		return null;
	}
	
	@Override
	public Array<E> trimToSize()
	{
		return null;
	}
	
	@Override
	public void apply(FuncElement<? super E> func)
	{
	}
	
	@Override
	protected void setArray(E[] array)
	{
	}
	
	@Override
	protected void setSize(int size)
	{
	}
	
	private static final class Node<E> implements Foldable
	{
		private E item;
		private Node<E> prev;
		private Node<E> next;
		
		private Node()
		{
		}
		
		public void setNext(Node<E> next)
		{
			this.next = next;
		}
		
		public Node<E> getNext()
		{
			return this.next;
		}
		
		public void setItem(E item)
		{
			this.item = item;
		}
		
		public E getItem()
		{
			return this.item;
		}
		
		public void setPrev(Node<E> prev)
		{
			this.prev = prev;
		}
		
		public Node<E> getPrev()
		{
			return this.prev;
		}
		
		@Override
		public void finalyze()
		{
			this.item = null;
			this.prev = null;
			this.next = null;
		}
		
		@Override
		public void reinit()
		{
		}
	}
}

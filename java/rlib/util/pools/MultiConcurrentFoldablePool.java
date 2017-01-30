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
package rlib.util.pools;

public final class MultiConcurrentFoldablePool<E extends Foldable> implements FoldablePool<E>
{
	private final ConcurrentFoldablePool<E>[] pools;
	private final int limit;
	private int order;
	
	protected MultiConcurrentFoldablePool(int size, Class<?> type)
	{
		this.pools = new ConcurrentFoldablePool[size];
		this.limit = size;
		int i = 0;
		while (i < size)
		{
			this.pools[i] = new ConcurrentFoldablePool(10, type);
			++i;
		}
	}
	
	private final int getNextOrder()
	{
		int next = this.order + 1;
		if (next >= this.limit)
		{
			next = 0;
		}
		this.setOrder(next);
		return next;
	}
	
	private final int getOrder()
	{
		int next = this.order;
		if (next >= this.limit)
		{
			next = 0;
		}
		this.setOrder(next);
		return next;
	}
	
	private final ConcurrentFoldablePool<E>[] getPools()
	{
		return this.pools;
	}
	
	@Override
	public boolean isEmpty()
	{
		ConcurrentFoldablePool<E>[] pools = this.getPools();
		int i = 0;
		int length = pools.length;
		while (i < length)
		{
			if (!pools[i].isEmpty())
			{
				return false;
			}
			++i;
		}
		return true;
	}
	
	@Override
	public void put(E object)
	{
		if (object == null)
		{
			return;
		}
		object.finalyze();
		this.pools[this.getOrder()].put(object);
	}
	
	private final void setOrder(int order)
	{
		this.order = order;
	}
	
	@Override
	public E take()
	{
		E object = this.pools[this.getNextOrder()].take();
		if (object == null)
		{
			return null;
		}
		object.reinit();
		return object;
	}
}

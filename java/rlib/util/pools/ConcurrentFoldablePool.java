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

import rlib.util.array.Array;
import rlib.util.array.Arrays;

public class ConcurrentFoldablePool<E extends Foldable> implements FoldablePool<E>
{
	private final Array<E> pool;
	
	protected ConcurrentFoldablePool(int size, Class<?> type)
	{
		this.pool = Arrays.toConcurrentArray(type, size);
	}
	
	@Override
	public boolean isEmpty()
	{
		return this.pool.isEmpty();
	}
	
	@Override
	public void put(E object)
	{
		if (object == null)
		{
			return;
		}
		object.finalyze();
		this.pool.add(object);
	}
	
	@Override
	public E take()
	{
		Foldable object = this.pool.pop();
		if (object == null)
		{
			return null;
		}
		object.reinit();
		return (E) object;
	}
}

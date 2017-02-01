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
package rlib.util.table;

import rlib.util.array.Array;
import rlib.util.array.IntegerArray;
import rlib.util.array.LongArray;

public abstract class AbstractTable<K, V> implements Table<K, V>
{
	protected static final int DEFAULT_INITIAL_CAPACITY = 16;
	protected static final int DEFAULT_MAXIMUM_CAPACITY = 1073741824;
	protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	protected static int hash(int hash)
	{
		hash ^= (hash >>> 20) ^ (hash >>> 12);
		return hash ^ (hash >>> 7) ^ (hash >>> 4);
	}
	
	protected static int hash(long key)
	{
		int hash = (int) (key ^ (key >>> 32));
		hash ^= (hash >>> 20) ^ (hash >>> 12);
		return hash ^ (hash >>> 7) ^ (hash >>> 4);
	}
	
	protected static int indexFor(int hash, int length)
	{
		return hash & (length - 1);
	}
	
	@Override
	public boolean containsKey(int key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public boolean containsKey(K key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public boolean containsKey(long key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public boolean containsValue(V value)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void finalyze()
	{
		clear();
	}
	
	@Override
	public V get(int key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public V get(K key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public V get(long key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public boolean isEmpty()
	{
		if (size() < 1)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public Array<K> keyArray(Array<K> container)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public IntegerArray keyIntegerArray(IntegerArray container)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public LongArray keyLongArray(LongArray container)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public V put(int key, V value)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public V put(K key, V value)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public V put(long key, V value)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void put(Table<K, V> table)
	{
		table.moveTo(this);
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
	
	@Override
	public V remove(int key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public V remove(K key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public V remove(long key)
	{
		throw new IllegalArgumentException("not supported.");
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

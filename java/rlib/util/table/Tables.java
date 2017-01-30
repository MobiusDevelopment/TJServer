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

public final class Tables
{
	public static final int hash(int hash)
	{
		hash ^= (hash >>> 20) ^ (hash >>> 12);
		return hash ^ (hash >>> 7) ^ (hash >>> 4);
	}
	
	public static final int indexFor(int hash, int length)
	{
		return hash & (length - 1);
	}
	
	public static final <V> Table<IntKey, V> newConcurrentIntegerTable()
	{
		return new ConcurrentIntegerTable();
	}
	
	public static final <V> Table<IntKey, V> newConcurrentIntegerTable(float loadFactor)
	{
		return new ConcurrentIntegerTable(loadFactor);
	}
	
	public static final <V> Table<IntKey, V> newConcurrentIntegerTable(int initCapacity)
	{
		return new ConcurrentIntegerTable(initCapacity);
	}
	
	public static final <V> Table<LongKey, V> newConcurrentLongTable()
	{
		return new ConcurrentLongTable();
	}
	
	public static final <V> Table<LongKey, V> newConcurrentLongTable(float loadFactor)
	{
		return new ConcurrentLongTable(loadFactor);
	}
	
	public static final <V> Table<LongKey, V> newConcurrentLongTable(int initCapacity)
	{
		return new ConcurrentLongTable(initCapacity);
	}
	
	public static final <K, V> Table<K, V> newConcurrentObjectTable()
	{
		return new ConcurrentObjectTable();
	}
	
	public static final <K, V> Table<K, V> newConcurrentObjectTable(float loadFactor)
	{
		return new ConcurrentObjectTable(loadFactor);
	}
	
	public static final <K, V> Table<K, V> newConcurrentObjectTable(int initCapacity)
	{
		return new ConcurrentObjectTable(initCapacity);
	}
	
	public static final <K, V> Table<K, V> newConcurrentTable()
	{
		return new ConcurrentTable();
	}
	
	public static final <V> Table<IntKey, V> newIntegerTable()
	{
		return new FastIntegerTable();
	}
	
	public static final <V> Table<IntKey, V> newIntegerTable(float loadFactor)
	{
		return new FastIntegerTable(loadFactor);
	}
	
	public static final <V> Table<IntKey, V> newIntegerTable(int initCapacity)
	{
		return new FastIntegerTable(initCapacity);
	}
	
	public static final <V> Table<LongKey, V> newLongTable()
	{
		return new FastLongTable();
	}
	
	public static final <V> Table<LongKey, V> newLongTable(float loadFactor)
	{
		return new FastLongTable(loadFactor);
	}
	
	public static final <V> Table<LongKey, V> newLongTable(int initCapacity)
	{
		return new FastLongTable(initCapacity);
	}
	
	public static final <K, V> Table<K, V> newObjectTable()
	{
		return new FastObjectTable();
	}
	
	public static final <K, V> Table<K, V> newObjectTable(float loadFactor)
	{
		return new FastObjectTable(loadFactor);
	}
	
	public static final <K, V> Table<K, V> newObjectTable(int initCapacity)
	{
		return new FastObjectTable(initCapacity);
	}
	
	public static final <K, V> Table<K, V> newTable()
	{
		return new FastTable();
	}
	
	private Tables()
	{
		throw new IllegalArgumentException();
	}
}

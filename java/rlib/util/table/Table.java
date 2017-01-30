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
import rlib.util.pools.Foldable;

public interface Table<K, V> extends Iterable<V>, Foldable
{
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	public static final int MAXIMUM_CAPACITY = 1073741824;
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	public void apply(FuncKeyValue<K, V> var1);
	
	public void apply(FuncValue<V> var1);
	
	public void clear();
	
	public boolean containsKey(int var1);
	
	public boolean containsKey(K var1);
	
	public boolean containsKey(long var1);
	
	public boolean containsValue(V var1);
	
	public V get(int var1);
	
	public V get(K var1);
	
	public V get(long var1);
	
	public TableType getType();
	
	public boolean isEmpty();
	
	public Array<K> keyArray(Array<K> var1);
	
	public IntegerArray keyIntegerArray(IntegerArray var1);
	
	public LongArray keyLongArray(LongArray var1);
	
	public void moveTo(Table<K, V> var1);
	
	public V put(int var1, V var2);
	
	public V put(K var1, V var2);
	
	public V put(long var1, V var3);
	
	public void put(Table<K, V> var1);
	
	public void readLock();
	
	public void readUnlock();
	
	public V remove(int var1);
	
	public V remove(K var1);
	
	public V remove(long var1);
	
	public int size();
	
	public Array<V> values(Array<V> var1);
	
	public void writeLock();
	
	public void writeUnlock();
}

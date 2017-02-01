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
	int DEFAULT_INITIAL_CAPACITY = 16;
	int MAXIMUM_CAPACITY = 1073741824;
	float DEFAULT_LOAD_FACTOR = 0.75f;
	
	void apply(FuncKeyValue<K, V> var1);
	
	void apply(FuncValue<V> var1);
	
	void clear();
	
	boolean containsKey(int var1);
	
	boolean containsKey(K var1);
	
	boolean containsKey(long var1);
	
	boolean containsValue(V var1);
	
	V get(int var1);
	
	V get(K var1);
	
	V get(long var1);
	
	TableType getType();
	
	boolean isEmpty();
	
	Array<K> keyArray(Array<K> var1);
	
	IntegerArray keyIntegerArray(IntegerArray var1);
	
	LongArray keyLongArray(LongArray var1);
	
	void moveTo(Table<K, V> var1);
	
	V put(int var1, V var2);
	
	V put(K var1, V var2);
	
	V put(long var1, V var3);
	
	void put(Table<K, V> var1);
	
	void readLock();
	
	void readUnlock();
	
	V remove(int var1);
	
	V remove(K var1);
	
	V remove(long var1);
	
	int size();
	
	Array<V> values(Array<V> var1);
	
	void writeLock();
	
	void writeUnlock();
}

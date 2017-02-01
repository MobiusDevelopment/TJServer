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

public interface LongArray extends Iterable<Long>
{
	LongArray add(long var1);
	
	LongArray addAll(long[] var1);
	
	LongArray addAll(LongArray var1);
	
	long[] array();
	
	LongArray clear();
	
	boolean contains(long var1);
	
	boolean containsAll(long[] var1);
	
	boolean containsAll(LongArray var1);
	
	boolean fastRemove(int var1);
	
	boolean fastRemove(long var1);
	
	long first();
	
	long get(int var1);
	
	int indexOf(long var1);
	
	boolean isEmpty();
	
	@Override ArrayIterator<Long> iterator();
	
	long last();
	
	int lastIndexOf(long var1);
	
	long poll();
	
	long pop();
	
	void readLock();
	
	void readUnlock();
	
	boolean removeAll(LongArray var1);
	
	boolean retainAll(LongArray var1);
	
	int size();
	
	boolean slowRemove(int var1);
	
	boolean slowRemove(long var1);
	
	LongArray sort();
	
	long[] toArray(long[] var1);
	
	LongArray trimToSize();
	
	void writeLock();
	
	void writeUnlock();
}

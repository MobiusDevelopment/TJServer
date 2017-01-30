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
	public LongArray add(long var1);
	
	public LongArray addAll(long[] var1);
	
	public LongArray addAll(LongArray var1);
	
	public long[] array();
	
	public LongArray clear();
	
	public boolean contains(long var1);
	
	public boolean containsAll(long[] var1);
	
	public boolean containsAll(LongArray var1);
	
	public boolean fastRemove(int var1);
	
	public boolean fastRemove(long var1);
	
	public long first();
	
	public long get(int var1);
	
	public int indexOf(long var1);
	
	public boolean isEmpty();
	
	@Override
	public ArrayIterator<Long> iterator();
	
	public long last();
	
	public int lastIndexOf(long var1);
	
	public long poll();
	
	public long pop();
	
	public void readLock();
	
	public void readUnlock();
	
	public boolean removeAll(LongArray var1);
	
	public boolean retainAll(LongArray var1);
	
	public int size();
	
	public boolean slowRemove(int var1);
	
	public boolean slowRemove(long var1);
	
	public LongArray sort();
	
	public long[] toArray(long[] var1);
	
	public LongArray trimToSize();
	
	public void writeLock();
	
	public void writeUnlock();
}

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

public interface IntegerArray extends Iterable<Integer>
{
	IntegerArray add(int var1);
	
	IntegerArray addAll(int[] var1);
	
	IntegerArray addAll(IntegerArray var1);
	
	int[] array();
	
	IntegerArray clear();
	
	boolean contains(int var1);
	
	boolean containsAll(int[] var1);
	
	boolean containsAll(IntegerArray var1);
	
	boolean fastRemove(int var1);
	
	boolean fastRemoveByIndex(int var1);
	
	int first();
	
	int get(int var1);
	
	int indexOf(int var1);
	
	boolean isEmpty();
	
	@Override ArrayIterator<Integer> iterator();
	
	int last();
	
	int lastIndexOf(int var1);
	
	int poll();
	
	int pop();
	
	void readLock();
	
	void readUnlock();
	
	boolean removeAll(IntegerArray var1);
	
	boolean retainAll(IntegerArray var1);
	
	int size();
	
	boolean slowRemove(int var1);
	
	boolean slowRemoveByIndex(int var1);
	
	IntegerArray sort();
	
	int[] toArray(int[] var1);
	
	IntegerArray trimToSize();
	
	void writeLock();
	
	void writeUnlock();
}

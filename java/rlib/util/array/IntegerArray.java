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
	public IntegerArray add(int var1);
	
	public IntegerArray addAll(int[] var1);
	
	public IntegerArray addAll(IntegerArray var1);
	
	public int[] array();
	
	public IntegerArray clear();
	
	public boolean contains(int var1);
	
	public boolean containsAll(int[] var1);
	
	public boolean containsAll(IntegerArray var1);
	
	public boolean fastRemove(int var1);
	
	public boolean fastRemoveByIndex(int var1);
	
	public int first();
	
	public int get(int var1);
	
	public int indexOf(int var1);
	
	public boolean isEmpty();
	
	@Override
	public ArrayIterator<Integer> iterator();
	
	public int last();
	
	public int lastIndexOf(int var1);
	
	public int poll();
	
	public int pop();
	
	public void readLock();
	
	public void readUnlock();
	
	public boolean removeAll(IntegerArray var1);
	
	public boolean retainAll(IntegerArray var1);
	
	public int size();
	
	public boolean slowRemove(int var1);
	
	public boolean slowRemoveByIndex(int var1);
	
	public IntegerArray sort();
	
	public int[] toArray(int[] var1);
	
	public IntegerArray trimToSize();
	
	public void writeLock();
	
	public void writeUnlock();
}

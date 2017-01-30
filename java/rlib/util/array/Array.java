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

import java.io.Serializable;
import java.util.Comparator;

import rlib.util.pools.Foldable;

public interface Array<E> extends Iterable<E>, Serializable, Foldable
{
	public Array<E> add(E var1);
	
	public Array<E> addAll(Array<? extends E> var1);
	
	public Array<E> addAll(E[] var1);
	
	public E[] array();
	
	public Array<E> clear();
	
	public boolean contains(Object var1);
	
	public boolean containsAll(Array<?> var1);
	
	public boolean containsAll(Object[] var1);
	
	public E fastRemove(int var1);
	
	public boolean fastRemove(Object var1);
	
	public E first();
	
	public E get(int var1);
	
	public int indexOf(Object var1);
	
	public boolean isEmpty();
	
	@Override
	public ArrayIterator<E> iterator();
	
	public E last();
	
	public int lastIndexOf(Object var1);
	
	public E poll();
	
	public E pop();
	
	public void readLock();
	
	public void readUnlock();
	
	public boolean removeAll(Array<?> var1);
	
	public boolean retainAll(Array<?> var1);
	
	public E search(E var1, Search<E> var2);
	
	public void set(int var1, E var2);
	
	public int size();
	
	public E slowRemove(int var1);
	
	public boolean slowRemove(Object var1);
	
	public Array<E> sort(Comparator<E> var1);
	
	public <T> T[] toArray(T[] var1);
	
	public Array<E> trimToSize();
	
	public void writeLock();
	
	public void writeUnlock();
	
	public void apply(FuncElement<? super E> var1);
}

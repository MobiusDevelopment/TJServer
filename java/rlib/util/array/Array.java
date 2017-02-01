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
	Array<E> add(E var1);
	
	Array<E> addAll(Array<? extends E> var1);
	
	Array<E> addAll(E[] var1);
	
	E[] array();
	
	Array<E> clear();
	
	boolean contains(Object var1);
	
	boolean containsAll(Array<?> var1);
	
	boolean containsAll(Object[] var1);
	
	E fastRemove(int var1);
	
	boolean fastRemove(Object var1);
	
	E first();
	
	E get(int var1);
	
	int indexOf(Object var1);
	
	boolean isEmpty();
	
	@Override ArrayIterator<E> iterator();
	
	E last();
	
	int lastIndexOf(Object var1);
	
	E poll();
	
	E pop();
	
	void readLock();
	
	void readUnlock();
	
	boolean removeAll(Array<?> var1);
	
	boolean retainAll(Array<?> var1);
	
	E search(E var1, Search<E> var2);
	
	void set(int var1, E var2);
	
	int size();
	
	E slowRemove(int var1);
	
	boolean slowRemove(Object var1);
	
	Array<E> sort(Comparator<E> var1);
	
	<T> T[] toArray(T[] var1);
	
	Array<E> trimToSize();
	
	void writeLock();
	
	void writeUnlock();
	
	void apply(FuncElement<? super E> var1);
}

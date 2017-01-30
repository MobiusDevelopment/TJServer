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

public class SortedArray<E extends Comparable<E>> extends FastArray<E>
{
	private static final long serialVersionUID = 1;
	
	public SortedArray(Class<E> type)
	{
		super(type);
	}
	
	public SortedArray(Class<E> type, int size)
	{
		super(type, size);
	}
	
	@Override
	public SortedArray<E> add(E element)
	{
		if (this.size == ((Comparable[]) this.array).length)
		{
			this.array = (E[]) Arrays.copyOf((Comparable[]) this.array, ((((Comparable[]) this.array).length * 3) / 2) + 1);
		}
		Comparable[] array = this.array();
		int i = 0;
		int length = array.length;
		while (i < length)
		{
			Comparable old = array[i];
			if (old == null)
			{
				array[i] = element;
				++this.size;
				return this;
			}
			if (element.compareTo((E) old) < 0)
			{
				++this.size;
				int numMoved = this.size - i - 1;
				System.arraycopy(array, i, array, i + 1, numMoved);
				array[i] = element;
				return this;
			}
			++i;
		}
		return this;
	}
}

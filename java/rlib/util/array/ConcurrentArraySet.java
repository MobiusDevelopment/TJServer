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

public class ConcurrentArraySet<E> extends ConcurrentArray<E>
{
	private static final long serialVersionUID = 1;
	
	public ConcurrentArraySet(Class<E> type)
	{
		super(type);
	}
	
	public ConcurrentArraySet(Class<E> type, int size)
	{
		super(type, size);
	}
	
	@Override
	public ConcurrentArray<E> add(E element)
	{
		if (this.contains(element))
		{
			return this;
		}
		return super.add(element);
	}
}

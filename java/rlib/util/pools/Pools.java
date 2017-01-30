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
package rlib.util.pools;

public final class Pools
{
	public static final <T extends Foldable> FoldablePool<T> newConcurrentFoldablePool(Class<? extends Foldable> type)
	{
		return new ConcurrentFoldablePool(10, type);
	}
	
	public static final <T extends Foldable> FoldablePool<T> newConcurrentFoldablePool(Class<? extends Foldable> type, int size)
	{
		return new ConcurrentFoldablePool(size, type);
	}
	
	public static final <T extends Foldable> FoldablePool<T> newFoldablePool(Class<? extends Foldable> type)
	{
		return new FastFoldablePool(10, type);
	}
	
	public static final <T extends Foldable> FoldablePool<T> newFoldablePool(Class<? extends Foldable> type, int size)
	{
		return new FastFoldablePool(size, type);
	}
	
	public static final <T extends Foldable> FoldablePool<T> newMultiConcurrentFoldablePool(int size, Class<? extends Foldable> type)
	{
		return new MultiConcurrentFoldablePool(size, type);
	}
	
	private Pools()
	{
		throw new IllegalArgumentException();
	}
}

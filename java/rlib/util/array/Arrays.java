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

import java.util.Comparator;

public abstract class Arrays
{
	public static <T> T[] addToArray(T[] array, T element, Class<T> type)
	{
		if (array == null)
		{
			array = Arrays.create(type, 1);
			array[0] = element;
			return array;
		}
		int length = array.length;
		array = Arrays.copyOf(array, 1);
		array[length] = element;
		return array;
	}
	
	public static void clear(Object[] array)
	{
		int i = 0;
		int length = array.length;
		while (i < length)
		{
			array[i] = null;
			++i;
		}
	}
	
	public static int[] combine(int[] base, int[] added)
	{
		if (base == null)
		{
			return added;
		}
		if ((added == null) || (added.length < 1))
		{
			return base;
		}
		int[] result = new int[base.length + added.length];
		int index = 0;
		int i = 0;
		int length = base.length;
		while (i < length)
		{
			result[index++] = base[i];
			++i;
		}
		i = 0;
		length = added.length;
		while (i < length)
		{
			result[index++] = added[i];
			++i;
		}
		return result;
	}
	
	public static <T, E extends T> T[] combine(T[] base, E[] added, Class<T> type)
	{
		if (base == null)
		{
			return added;
		}
		if ((added == null) || (added.length < 1))
		{
			return base;
		}
		T[] result = Arrays.create(type, base.length + added.length);
		int index = 0;
		int i = 0;
		int length = base.length;
		while (i < length)
		{
			result[index++] = base[i];
			++i;
		}
		i = 0;
		length = added.length;
		while (i < length)
		{
			result[index++] = added[i];
			++i;
		}
		return result;
	}
	
	public static boolean contains(int[] array, int val)
	{
		int i = 0;
		int length = array.length;
		while (i < length)
		{
			if (array[i] == val)
			{
				return true;
			}
			++i;
		}
		return false;
	}
	
	public static boolean contains(Object[] array, Object object)
	{
		int i = 0;
		int length = array.length;
		while (i < length)
		{
			if (array[i].equals(object))
			{
				return true;
			}
			++i;
		}
		return false;
	}
	
	public static byte[] copyOf(byte[] old, int added)
	{
		byte[] copy = new byte[old.length + added];
		System.arraycopy(old, 0, copy, 0, Math.min(old.length, copy.length));
		return copy;
	}
	
	public static int[] copyOf(int[] old, int added)
	{
		int[] copy = new int[old.length + added];
		System.arraycopy(old, 0, copy, 0, Math.min(old.length, copy.length));
		return copy;
	}
	
	public static long[] copyOf(long[] old, int added)
	{
		long[] copy = new long[old.length + added];
		System.arraycopy(old, 0, copy, 0, Math.min(old.length, copy.length));
		return copy;
	}
	
	public static <T> T[] copyOf(T[] old, int added)
	{
		Class<? extends Object[]> newType = old.getClass();
		T[] copy = Arrays.create(newType.getComponentType(), old.length + added);
		System.arraycopy(old, 0, copy, 0, Math.min(old.length, copy.length));
		return copy;
	}
	
	public static int[] copyOfRange(int[] original, int from, int to)
	{
		int newLength = to - from;
		int[] copy = new int[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}
	
	public static long[] copyOfRange(long[] original, int from, int to)
	{
		int newLength = to - from;
		long[] copy = new long[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}
	
	public static <T> T[] copyOfRange(T[] original, int from, int to)
	{
		Class<? extends Object[]> newType = original.getClass();
		int newLength = to - from;
		T[] copy = Arrays.create(newType.getComponentType(), newLength);
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] create(Class<?> type, int size)
	{
		return (T[]) java.lang.reflect.Array.newInstance(type, size);
	}
	
	public static int indexOf(Object[] array, Object object)
	{
		int i = 0;
		int length = array.length;
		while (i < length)
		{
			if (array[i] == object)
			{
				return i;
			}
			++i;
		}
		return -1;
	}
	
	public static IntegerArray newIntegerArray()
	{
		return new FastIntegerArray();
	}
	
	public static IntegerArray newIntegerArray(int size)
	{
		return new FastIntegerArray(size);
	}
	
	public static LongArray newLongArray()
	{
		return new FastLongArray();
	}
	
	public static LongArray newLongArray(int size)
	{
		return new FastLongArray(size);
	}
	
	public static void sort(Comparable<?>[] array)
	{
		java.util.Arrays.sort(array);
	}
	
	public static void sort(int[] array)
	{
		java.util.Arrays.sort(array);
	}
	
	public static void sort(int[] array, int fromIndex, int toIndex)
	{
		java.util.Arrays.sort(array, fromIndex, toIndex);
	}
	
	public static void sort(long[] array, int fromIndex, int toIndex)
	{
		java.util.Arrays.sort(array, fromIndex, toIndex);
	}
	
	public static <T> void sort(T[] array, Comparator<? super T> comparator)
	{
		java.util.Arrays.sort(array, comparator);
	}
	
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	public static <E> Array<E> toArray(Class<?> type)
	{
		return new FastArray(type);
	}
	
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	public static <E> Array<E> toArray(Class<?> type, int size)
	{
		return new FastArray(type, size);
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E> Array<E> toArraySet(Class<?> type)
	{
		return new FastArraySet(type);
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E> Array<E> toArraySet(Class<?> type, int size)
	{
		return new FastArraySet(type, size);
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E> Array<E> toConcurrentArray(Class<?> type)
	{
		return new ConcurrentArray(type);
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E> Array<E> toConcurrentArray(Class<?> type, int size)
	{
		return new ConcurrentArray(type, size);
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E> Array<E> toConcurrentArraySet(Class<?> type)
	{
		return new ConcurrentArraySet(type);
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E> Array<E> toConcurrentArraySet(Class<?> type, int size)
	{
		return new ConcurrentArraySet(type, size);
	}
	
	public static /* varargs */ float[] toFloatArray(float... elements)
	{
		return elements;
	}
	
	@SafeVarargs
	public static /* varargs */ <T, K extends T> T[] toGenericArray(K... elements)
	{
		return elements;
	}
	
	public static /* varargs */ int[] toIntegerArray(int... elements)
	{
		return elements;
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E extends Comparable<E>> Array<E> toSortedArray(Class<?> type)
	{
		return new SortedArray(type);
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E extends Comparable<E>> Array<E> toSortedArray(Class<?> type, int size)
	{
		return new SortedArray(type, size);
	}
	
	public static String toString(Array<?> array)
	{
		if (array == null)
		{
			return "[]";
		}
		String className = array.array().getClass().getSimpleName();
		StringBuilder builder = new StringBuilder(className.substring(0, className.length() - 1));
		int i = 0;
		int length = array.size() - 1;
		while (i <= length)
		{
			builder.append(String.valueOf(array.get(i)));
			if (i == length)
			{
				break;
			}
			builder.append(", ");
			++i;
		}
		builder.append("]");
		return builder.toString();
	}
	
	public static String toString(IntegerArray array)
	{
		if (array == null)
		{
			return "[]";
		}
		String className = array.array().getClass().getSimpleName();
		StringBuilder builder = new StringBuilder(className.substring(0, className.length() - 1));
		int i = 0;
		int length = array.size() - 1;
		while (i <= length)
		{
			builder.append(String.valueOf(array.get(i)));
			if (i == length)
			{
				break;
			}
			builder.append(", ");
			++i;
		}
		builder.append("]");
		return builder.toString();
	}
	
	public static String toString(LongArray array)
	{
		if (array == null)
		{
			return "[]";
		}
		String className = array.array().getClass().getSimpleName();
		StringBuilder builder = new StringBuilder(className.substring(0, className.length() - 1));
		int i = 0;
		int length = array.size() - 1;
		while (i <= length)
		{
			builder.append(String.valueOf(array.get(i)));
			if (i == length)
			{
				break;
			}
			builder.append(", ");
			++i;
		}
		builder.append("]");
		return builder.toString();
	}
	
	public static String toString(Object[] array)
	{
		if (array == null)
		{
			return "[]";
		}
		String className = array.getClass().getSimpleName();
		StringBuilder builder = new StringBuilder(className.substring(0, className.length() - 1));
		int i = 0;
		int length = array.length - 1;
		while (i <= length)
		{
			builder.append(String.valueOf(array[i]));
			if (i == length)
			{
				break;
			}
			builder.append(", ");
			++i;
		}
		builder.append("]");
		return builder.toString();
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public static <E> Array<E> toSynchronizedArray(Class<?> type)
	{
		return new SynchronizedArray(type);
	}
}

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
package rlib.util;

import java.lang.reflect.Field;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

public final class ReflectionUtils
{
	private ReflectionUtils()
	{
		throw new IllegalArgumentException();
	}
	
	public static /* varargs */ void addAllFields(Array<Field> container, Class<?> cs, Class<?> last, boolean declared, String... exceptions)
	{
		Class<?> next = cs;
		while ((next != null) && (next != last))
		{
			Field[] fields = declared ? next.getDeclaredFields() : next.getFields();
			next = next.getSuperclass();
			if (fields.length < 1)
			{
				continue;
			}
			if ((exceptions == null) || (exceptions.length < 1))
			{
				container.addAll(fields);
				continue;
			}
			int i = 0;
			int length = fields.length;
			while (i < length)
			{
				Field field = fields[i];
				if (!Arrays.contains(exceptions, field.getName()))
				{
					container.add(field);
				}
				++i;
			}
		}
	}
	
	public static /* varargs */ Array<Field> getAllFields(Class<?> cs, Class<?> last, boolean declared, String... exceptions)
	{
		Array<Field> container = Arrays.toArray(Field.class);
		ReflectionUtils.addAllFields(container, cs, last, declared, exceptions);
		return container;
	}
}

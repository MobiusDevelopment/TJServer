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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

public abstract class Objects
{
	@SuppressWarnings("unchecked")
	public static <T> T clone(T original)
	{
		if (original == null)
		{
			return null;
		}
		if (original instanceof Cloneable)
		{
			try
			{
				Method method = original.getClass().getMethod("clone", new Class[0]);
				method.setAccessible(true);
				return (T) method.invoke(original, new Object[0]);
			}
			catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e)
			{
				Loggers.warning("Objects", e);
				return null;
			}
		}
		Object newObject = Objects.newInstance(original.getClass());
		Objects.reload(newObject, original);
		return (T) newObject;
	}
	
	public static int hash(boolean value)
	{
		return value ? 1231 : 1237;
	}
	
	public static int hash(long value)
	{
		return (int) (value ^ (value >>> 32));
	}
	
	public static int hash(Object object)
	{
		return object.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> cs)
	{
		if ((cs == Boolean.class) || (cs == Boolean.TYPE))
		{
			return (T) Boolean.FALSE;
		}
		if ((cs == Character.class) || (cs == Character.TYPE))
		{
			return (T) Character.valueOf('x');
		}
		if ((cs == Byte.class) || (cs == Byte.TYPE))
		{
			return (T) Byte.valueOf((byte) 0);
		}
		if ((cs == Short.class) || (cs == Short.TYPE))
		{
			return (T) Short.valueOf((short) 0);
		}
		if ((cs == Integer.class) || (cs == Integer.TYPE))
		{
			return (T) Integer.valueOf(0);
		}
		if ((cs == Long.class) || (cs == Long.TYPE))
		{
			return (T) Long.valueOf(0);
		}
		if ((cs == Float.class) || (cs == Float.TYPE))
		{
			return (T) Float.valueOf(0.0f);
		}
		if ((cs == Double.class) || (cs == Double.TYPE))
		{
			return (T) Double.valueOf(0.0);
		}
		if (cs == String.class)
		{
			return cs.cast("");
		}
		if (cs == Class.class)
		{
			return (T) Object.class;
		}
		Constructor<?>[] arrconstructor = cs.getDeclaredConstructors();
		int n = arrconstructor.length;
		int n2 = 0;
		while (n2 < n)
		{
			Constructor<?> constructor = arrconstructor[n2];
			if (!constructor.isAccessible())
			{
				constructor.setAccessible(true);
			}
			Class<?>[] types = constructor.getParameterTypes();
			Object[] parametrs = new Object[types.length];
			int i = 0;
			int length = types.length;
			while (i < length)
			{
				Object object = Objects.newInstance(types[i]);
				parametrs[i] = object;
				++i;
			}
			try
			{
				return (T) constructor.newInstance(parametrs);
			}
			catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e)
			{
				Loggers.warning("Objects", e);
				++n2;
			}
		}
		return null;
	}
	
	public static <O, N extends O> void reload(O original, N updated)
	{
		if ((original == null) || (updated == null))
		{
			return;
		}
		Array<Field> array = Arrays.toArray(Field.class);
		Class<?> cs = original.getClass();
		while (cs != null)
		{
			Field[] fields = cs.getDeclaredFields();
			Field[] arrfield = fields;
			int n = arrfield.length;
			int n2 = 0;
			while (n2 < n)
			{
				Field field = arrfield[n2];
				array.add(field);
				++n2;
			}
			cs = cs.getSuperclass();
		}
		array.trimToSize();
		for (Field field : array)
		{
			String str = field.toString();
			if (str.contains("final") || str.contains("static"))
			{
				continue;
			}
			field.setAccessible(true);
			try
			{
				field.set(original, field.get(updated));
				continue;
			}
			catch (IllegalAccessException | IllegalArgumentException e)
			{
				Loggers.warning("Objects", e.getMessage());
			}
		}
	}
}

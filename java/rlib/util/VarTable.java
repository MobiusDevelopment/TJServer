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

import java.lang.reflect.Array;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import rlib.geom.Vector;
import rlib.util.table.Table;
import rlib.util.table.Tables;

public class VarTable
{
	private final Table<String, Object> values = Tables.newObjectTable();
	
	public static VarTable newInstance()
	{
		return new VarTable();
	}
	
	public static VarTable newInstance(Node node)
	{
		return VarTable.newInstance().parse(node);
	}
	
	public static VarTable newInstance(Node node, String childName, String nameType, String nameValue)
	{
		return VarTable.newInstance().parse(node, childName, nameType, nameValue);
	}
	
	public void clear()
	{
		this.values.clear();
	}
	
	public Object get(String key)
	{
		return this.values.get(key);
	}
	
	public <T, E extends T> T get(String key, Class<T> type, E def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (type.isInstance(object))
		{
			return type.cast(object);
		}
		return def;
	}
	
	public <T> T get(String key, T def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		Class type = def.getClass();
		if (type.isInstance(object))
		{
			return (T) object;
		}
		return def;
	}
	
	public boolean getBoolean(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Boolean)
		{
			return (Boolean) object;
		}
		if (object instanceof String)
		{
			return Boolean.parseBoolean(object.toString());
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public boolean getBoolean(String key, boolean def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Boolean)
		{
			return (Boolean) object;
		}
		if (object instanceof String)
		{
			return Boolean.parseBoolean(object.toString());
		}
		return def;
	}
	
	public boolean[] getBooleanArray(String key, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof boolean[])
		{
			return (boolean[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			boolean[] result = new boolean[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Boolean.parseBoolean(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ boolean[] getBooleanArray(String key, String regex, boolean... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof boolean[])
		{
			return (boolean[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			boolean[] result = new boolean[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Boolean.parseBoolean(strs[i]);
				++i;
			}
			return result;
		}
		return def;
	}
	
	public byte getByte(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Byte)
		{
			return ((Byte) object).byteValue();
		}
		if (object instanceof String)
		{
			return Byte.parseByte(object.toString());
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public byte getByte(String key, byte def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Byte)
		{
			return ((Byte) object).byteValue();
		}
		if (object instanceof String)
		{
			return Byte.parseByte(object.toString());
		}
		return def;
	}
	
	public byte[] getByteArray(String key, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof byte[])
		{
			return (byte[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			byte[] result = new byte[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Byte.parseByte(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ byte[] getByteArray(String key, String regex, byte... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof byte[])
		{
			return (byte[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			byte[] result = new byte[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Byte.parseByte(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public double getDouble(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Double)
		{
			return (Double) object;
		}
		if (object instanceof String)
		{
			return Double.parseDouble(object.toString());
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public double getDouble(String key, double def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Double)
		{
			return (Double) object;
		}
		if (object instanceof String)
		{
			return Double.parseDouble(object.toString());
		}
		return def;
	}
	
	public double[] getDoubleArray(String key, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof double[])
		{
			return (double[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			double[] result = new double[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Double.parseDouble(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ double[] getDoubleArray(String key, String regex, double... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof double[])
		{
			return (double[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			double[] result = new double[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Double.parseDouble(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public <T extends Enum<T>> T getEnum(String key, Class<T> type)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (type.isInstance(object))
		{
			return (T) ((Enum) type.cast(object));
		}
		if (object instanceof String)
		{
			return Enum.valueOf(type, object.toString());
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public <T extends Enum<T>> T getEnum(String key, Class<T> type, T def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (type.isInstance(object))
		{
			return (T) ((Enum) type.cast(object));
		}
		if (object instanceof String)
		{
			return Enum.valueOf(type, object.toString());
		}
		return def;
	}
	
	public <T extends Enum<T>> T[] getEnumArray(String key, Class<T> type, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Enum[])
		{
			return (T[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			Enum[] result = (Enum[]) Array.newInstance(type, strs.length);
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Enum.valueOf(type, strs[i]);
				++i;
			}
			return (T[]) result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ <T extends Enum<T>> T[] getEnumArray(String key, Class<T> type, String regex, T... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Enum[])
		{
			return (T[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			Enum[] result = (Enum[]) Array.newInstance(type, strs.length);
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Enum.valueOf(type, strs[i]);
				++i;
			}
			return (T[]) result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public float getFloat(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Float)
		{
			return ((Float) object).floatValue();
		}
		if (object instanceof String)
		{
			return Float.parseFloat(object.toString());
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public float getFloat(String key, float def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Float)
		{
			return ((Float) object).floatValue();
		}
		if (object instanceof String)
		{
			return Float.parseFloat(object.toString());
		}
		return def;
	}
	
	public float[] getFloatArray(String key, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException();
		}
		if (object instanceof float[])
		{
			return (float[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			float[] result = new float[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Float.parseFloat(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ float[] getFloatArray(String key, String regex, float... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof float[])
		{
			return (float[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			float[] result = new float[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Float.parseFloat(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public <T> T getGeneric(String key, Class<T> type)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException();
		}
		if (type.isInstance(object))
		{
			return type.cast(object);
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public <T> T[] getGenericArray(String key, Class<T[]> type)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (type.isInstance(object))
		{
			return (T[]) object;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ <T> T[] getGenericArray(String key, Class<T[]> type, T... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (type.isInstance(object))
		{
			return (T[]) object;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public int getInteger(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Integer)
		{
			return (Integer) object;
		}
		if (object instanceof String)
		{
			return Integer.parseInt(object.toString());
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public int getInteger(String key, int def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Integer)
		{
			return (Integer) object;
		}
		if (object instanceof String)
		{
			return Integer.parseInt(object.toString());
		}
		return def;
	}
	
	public int[] getIntegerArray(String key, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof int[])
		{
			return (int[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			int[] result = new int[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Integer.parseInt(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ int[] getIntegerArray(String key, String regex, int... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof int[])
		{
			return (int[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			int[] result = new int[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Integer.parseInt(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public long getLong(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Long)
		{
			return (Long) object;
		}
		if (object instanceof String)
		{
			return Long.parseLong(object.toString());
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public long getLong(String key, long def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Long)
		{
			return (Long) object;
		}
		if (object instanceof String)
		{
			return Long.parseLong(object.toString());
		}
		return def;
	}
	
	public long[] getLongArray(String key, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof long[])
		{
			return (long[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			long[] result = new long[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Long.parseLong(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ long[] getLongArray(String key, String regex, long... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof long[])
		{
			return (long[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			long[] result = new long[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Long.parseLong(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public short getShort(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Short)
		{
			return (Short) object;
		}
		if (object instanceof String)
		{
			return Short.parseShort(object.toString());
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public short getShort(String key, short def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Short)
		{
			return (Short) object;
		}
		if (object instanceof String)
		{
			return Short.parseShort(object.toString());
		}
		return def;
	}
	
	public short[] getShortArray(String key, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof short[])
		{
			return (short[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			short[] result = new short[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Short.parseShort(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ short[] getShortArray(String key, String regex, short... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof short[])
		{
			return (short[]) object;
		}
		if (object instanceof String)
		{
			String[] strs = object.toString().split(regex);
			short[] result = new short[strs.length];
			int i = 0;
			int length = strs.length;
			while (i < length)
			{
				result[i] = Short.parseShort(strs[i]);
				++i;
			}
			return result;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public String getString(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof String)
		{
			return (String) object;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public String getString(String key, String def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof String)
		{
			return (String) object;
		}
		return def;
	}
	
	public String[] getStringArray(String key, String regex)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof String[])
		{
			return (String[]) object;
		}
		if (object instanceof String)
		{
			return object.toString().split(regex);
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public /* varargs */ String[] getStringArray(String key, String regex, String... def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof String[])
		{
			return (String[]) object;
		}
		if (object instanceof String)
		{
			return object.toString().split(regex);
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public Table<String, Object> getValues()
	{
		return this.values;
	}
	
	public Vector getVector(String key)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			throw new IllegalArgumentException("not found " + key);
		}
		if (object instanceof Vector)
		{
			return (Vector) object;
		}
		if (object instanceof String)
		{
			String[] vals = object.toString().split(",");
			Vector vector = Vector.newInstance();
			vector.setXYZ(Float.parseFloat(vals[0]), Float.parseFloat(vals[1]), Float.parseFloat(vals[2]));
			return vector;
		}
		throw new IllegalArgumentException("not found " + key);
	}
	
	public Vector getVector(String key, Vector def)
	{
		Object object = this.values.get(key);
		if (object == null)
		{
			return def;
		}
		if (object instanceof Vector)
		{
			return (Vector) object;
		}
		if (object instanceof String)
		{
			String[] vals = object.toString().split(",");
			Vector vector = Vector.newInstance();
			vector.setXYZ(Float.parseFloat(vals[0]), Float.parseFloat(vals[1]), Float.parseFloat(vals[2]));
			return vector;
		}
		return def;
	}
	
	public VarTable parse(Node node)
	{
		this.values.clear();
		if (node == null)
		{
			return this;
		}
		NamedNodeMap attrs = node.getAttributes();
		if (attrs == null)
		{
			return this;
		}
		int i = 0;
		int length = attrs.getLength();
		while (i < length)
		{
			Node item = attrs.item(i);
			this.set(item.getNodeName(), item.getNodeValue());
			++i;
		}
		return this;
	}
	
	public VarTable parse(Node node, String childName, String nameType, String nameValue)
	{
		this.values.clear();
		if (node == null)
		{
			return this;
		}
		Node child = node.getFirstChild();
		while (child != null)
		{
			if (childName.equals(child.getNodeName()))
			{
				NamedNodeMap attrs = child.getAttributes();
				Node name = attrs.getNamedItem(nameType);
				Node val = attrs.getNamedItem(nameValue);
				if ((name != null) && (val != null))
				{
					this.set(name.getNodeValue(), val.getNodeValue());
				}
			}
			child = child.getNextSibling();
		}
		return this;
	}
	
	public void set(String key, Object value)
	{
		this.values.put(key, value);
	}
	
	public VarTable set(VarTable set)
	{
		this.values.put(set.getValues());
		return this;
	}
	
	@Override
	public String toString()
	{
		return "VarTable: " + (this.values != null ? new StringBuilder("values = ").append(this.values).toString() : "");
	}
}

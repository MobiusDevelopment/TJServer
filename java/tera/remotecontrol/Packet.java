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
package tera.remotecontrol;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Ronn
 * @created 26.03.2012
 */
public final class Packet implements Serializable
{
	private static final long serialVersionUID = -5515767380967971929L;
	private final PacketType type;
	private Serializable[] values;
	private int ordinal;
	
	/**
	 * Constructor for Packet.
	 * @param type PacketType
	 * @param values Serializable[]
	 */
	public Packet(PacketType type, Serializable... values)
	{
		this.type = type;
		this.values = values;
	}
	
	/**
	 * Method getType.
	 * @return PacketType
	 */
	public PacketType getType()
	{
		return type;
	}
	
	/**
	 * Method getValues.
	 * @return Serializable[]
	 */
	public Serializable[] getValues()
	{
		return values;
	}
	
	/**
	 * Method hasNext.
	 * @return boolean
	 */
	public boolean hasNext()
	{
		return ordinal < values.length;
	}
	
	/**
	 * Method next.
	 * @return Object
	 */
	public Object next()
	{
		return values[ordinal++];
	}
	
	/**
	 * Method next.
	 * @param <T>
	 * @param type Class<T>
	 * @return T
	 */
	public <T> T next(Class<T> type)
	{
		return type.cast(values[ordinal++]);
	}
	
	/**
	 * Method nextBoolean.
	 * @return boolean
	 */
	public boolean nextBoolean()
	{
		return (Boolean) values[ordinal++];
	}
	
	/**
	 * Method nextDouble.
	 * @return double
	 */
	public double nextDouble()
	{
		return (Double) values[ordinal++];
	}
	
	/**
	 * Method nextFloat.
	 * @return float
	 */
	public float nextFloat()
	{
		return (Float) values[ordinal++];
	}
	
	/**
	 * Method nextInt.
	 * @return int
	 */
	public int nextInt()
	{
		return (Integer) values[ordinal++];
	}
	
	/**
	 * Method nextLong.
	 * @return long
	 */
	public long nextLong()
	{
		return (Long) values[ordinal++];
	}
	
	/**
	 * Method nextString.
	 * @return String
	 */
	public String nextString()
	{
		return values[ordinal++].toString();
	}
	
	/**
	 * Method setValues.
	 * @param values Serializable[]
	 */
	public void setValues(Serializable... values)
	{
		this.values = values;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "Packet  " + (type != null ? "type = " + type + ", " : "") + (values != null ? "values = " + Arrays.toString(values) + ", " : "") + "ordinal = " + ordinal;
	}
}
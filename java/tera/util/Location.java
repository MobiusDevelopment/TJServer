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
package tera.util;

import rlib.geom.GamePoint;
import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public final class Location implements GamePoint
{
	protected static final float HEADINGS_IN_PI = 10430.378350470452724949566316381F;
	
	/**
	 * Method valueOf.
	 * @param string String
	 * @return Location
	 */
	public static Location valueOf(String string)
	{
		if (string == null)
		{
			return new Location();
		}
		
		final String[] coords = string.split(",");
		
		if (coords.length < 3)
		{
			return null;
		}
		
		try
		{
			final Location newLoc = new Location(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]), Float.parseFloat(coords[2]));
			
			if (coords.length > 3)
			{
				newLoc.setHeading(Integer.parseInt(coords[3]));
			}
			
			return newLoc;
		}
		catch (NumberFormatException e)
		{
			Loggers.warning("Location", e);
		}
		
		return null;
	}
	
	private float x;
	private float y;
	private float z;
	private int heading;
	private int continentId;
	
	public Location()
	{
		this(0f, 0f, 0f, 0);
	}
	
	/**
	 * Constructor for Location.
	 * @param x float
	 * @param y float
	 * @param z float
	 */
	public Location(float x, float y, float z)
	{
		this(x, y, z, 0);
	}
	
	/**
	 * Constructor for Location.
	 * @param locx float
	 * @param locy float
	 * @param locz float
	 * @param locheading int
	 */
	public Location(float locx, float locy, float locz, int locheading)
	{
		x = locx;
		y = locy;
		z = locz;
		heading = locheading;
	}
	
	/**
	 * Constructor for Location.
	 * @param locx float
	 * @param locy float
	 * @param locz float
	 * @param locheading int
	 * @param loccontinentId int
	 */
	public Location(float locx, float locy, float locz, int locheading, int loccontinentId)
	{
		x = locx;
		y = locy;
		z = locz;
		heading = locheading;
		continentId = loccontinentId;
	}
	
	/**
	 * Constructor for Location.
	 * @param points float[]
	 * @param locheading int
	 */
	public Location(float[] points, int locheading)
	{
		x = points[0];
		y = points[1];
		z = points[2];
		heading = locheading;
	}
	
	/**
	 * Constructor for Location.
	 * @param loc Location
	 */
	public Location(Location loc)
	{
		this(loc.x, loc.y, loc.z, loc.heading);
	}
	
	/**
	 * Method calcHeading.
	 * @param targetX float
	 * @param targetY float
	 * @return int
	 */
	public final int calcHeading(float targetX, float targetY)
	{
		return (int) (Math.atan2(y - targetY, x - targetX) * HEADINGS_IN_PI) + 32768;
	}
	
	/**
	 * Method equals.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @return boolean
	 */
	public boolean equals(float x, float y, float z)
	{
		return (x == x) && (y == y) && (z == z);
	}
	
	/**
	 * Method equals.
	 * @param locx float
	 * @param locy float
	 * @param locz float
	 * @param locheading int
	 * @return boolean
	 */
	public boolean equals(float locx, float locy, float locz, int locheading)
	{
		return (x == locx) && (y == locy) && (z == locz) && (heading == locheading);
	}
	
	/**
	 * Method equals.
	 * @param locx int
	 * @param locy int
	 * @param locz int
	 * @return boolean
	 */
	public boolean equals(int locx, int locy, int locz)
	{
		return (x == locx) && (y == locy) && (z == locz);
	}
	
	/**
	 * Method equals.
	 * @param locx int
	 * @param locy int
	 * @param locz int
	 * @param locheading int
	 * @return boolean
	 */
	public boolean equals(int locx, int locy, int locz, int locheading)
	{
		return (x == locx) && (y == locy) && (z == locz) && (heading == locheading);
	}
	
	/**
	 * Method equals.
	 * @param loc Location
	 * @return boolean
	 */
	public boolean equals(Location loc)
	{
		return (loc.x == x) && (loc.y == y) && (loc.z == z);
	}
	
	/**
	 * Method getContinentId.
	 * @return int
	 */
	public int getContinentId()
	{
		return continentId;
	}
	
	/**
	 * Method getHeading.
	 * @return int
	 * @see rlib.geom.GamePoint#getHeading()
	 */
	@Override
	public int getHeading()
	{
		return heading;
	}
	
	/**
	 * Method getX.
	 * @return float
	 * @see rlib.geom.GamePoint#getX()
	 */
	@Override
	public float getX()
	{
		return x;
	}
	
	/**
	 * Method getY.
	 * @return float
	 * @see rlib.geom.GamePoint#getY()
	 */
	@Override
	public float getY()
	{
		return y;
	}
	
	/**
	 * Method getZ.
	 * @return float
	 * @see rlib.geom.GamePoint#getZ()
	 */
	@Override
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Method isNull.
	 * @return boolean
	 */
	public boolean isNull()
	{
		return (x == 0f) || (y == 0f) || (z == 0f);
	}
	
	/**
	 * Method set.
	 * @param loc Location
	 */
	public void set(Location loc)
	{
		x = loc.x;
		y = loc.y;
		z = loc.z;
		heading = loc.heading;
	}
	
	/**
	 * Method setContinentId.
	 * @param loccontinentId int
	 */
	public void setContinentId(int loccontinentId)
	{
		continentId = loccontinentId;
	}
	
	/**
	 * Method setHeading.
	 * @param locheading int
	 * @return Location
	 * @see rlib.geom.GamePoint#setHeading(int)
	 */
	@Override
	public Location setHeading(int locheading)
	{
		heading = locheading;
		return this;
	}
	
	/**
	 * Method setX.
	 * @param locx float
	 * @return Location
	 * @see rlib.geom.GamePoint#setX(float)
	 */
	@Override
	public Location setX(float locx)
	{
		x = locx;
		return this;
	}
	
	/**
	 * Method setXYZ.
	 * @param locx float
	 * @param locy float
	 * @param locz float
	 * @return Location
	 * @see rlib.geom.GamePoint#setXYZ(float, float, float)
	 */
	@Override
	public Location setXYZ(float locx, float locy, float locz)
	{
		x = locx;
		y = locy;
		z = locz;
		return this;
	}
	
	/**
	 * Method setXYZH.
	 * @param locx float
	 * @param locy float
	 * @param locz float
	 * @param locheading int
	 * @return Location
	 * @see rlib.geom.GamePoint#setXYZH(float, float, float, int)
	 */
	@Override
	public Location setXYZH(float locx, float locy, float locz, int locheading)
	{
		x = locx;
		y = locy;
		z = locz;
		heading = locheading;
		return this;
	}
	
	/**
	 * Method setXYZHC.
	 * @param locx float
	 * @param locy float
	 * @param locz float
	 * @param locheading int
	 * @param loccontinentId int
	 * @return Location
	 */
	public Location setXYZHC(float locx, float locy, float locz, int locheading, int loccontinentId)
	{
		x = locx;
		y = locy;
		z = locz;
		heading = locheading;
		continentId = loccontinentId;
		return this;
	}
	
	/**
	 * Method setY.
	 * @param locy float
	 * @return Location
	 * @see rlib.geom.GamePoint#setY(float)
	 */
	@Override
	public Location setY(float locy)
	{
		y = locy;
		return this;
	}
	
	/**
	 * Method setZ.
	 * @param locz float
	 * @return Location
	 * @see rlib.geom.GamePoint#setZ(float)
	 */
	@Override
	public Location setZ(float locz)
	{
		z = locz;
		return this;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public final String toString()
	{
		return "x = " + x + ", y = " + y + ", z = " + z + ", continentId = " + continentId + ", heading = " + heading;
	}
}
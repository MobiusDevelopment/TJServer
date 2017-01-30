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
package tera.gameserver.model;

import java.awt.Polygon;

import tera.gameserver.tables.WorldZoneTable;
import tera.util.Location;

import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 08.03.2012
 */
public final class WorldZone
{
	private int maximumX;
	private int minimumX;
	private int maximumY;
	private int minimumY;
	private final int maximumZ;
	private final int minimumZ;
	private final int zoneId;
	private final int continentId;
	private final Polygon zone;
	private Location[] respawnPoints;
	
	/**
	 * Constructor for WorldZone.
	 * @param maxZ int
	 * @param minZ int
	 * @param zoneId int
	 * @param continentId int
	 */
	public WorldZone(int maxZ, int minZ, int zoneId, int continentId)
	{
		maximumZ = maxZ;
		minimumZ = minZ;
		this.zoneId = zoneId;
		this.continentId = continentId;
		maximumX = Integer.MIN_VALUE;
		minimumX = Integer.MAX_VALUE;
		maximumY = Integer.MIN_VALUE;
		minimumY = Integer.MAX_VALUE;
		zone = new Polygon();
		respawnPoints = new Location[0];
	}
	
	/**
	 * Method addPoint.
	 * @param x int
	 * @param y int
	 */
	public final void addPoint(int x, int y)
	{
		maximumX = Math.max(maximumX, x);
		minimumX = Math.min(minimumX, x);
		maximumY = Math.max(maximumY, y);
		minimumY = Math.min(minimumY, y);
		zone.addPoint(x, y);
	}
	
	/**
	 * Method addRespawnPoint.
	 * @param point Location
	 */
	public final void addRespawnPoint(Location point)
	{
		respawnPoints = Arrays.addToArray(respawnPoints, point, Location.class);
	}
	
	/**
	 * Method contains.
	 * @param x int
	 * @param y int
	 * @param z int
	 * @return boolean
	 */
	public final boolean contains(int x, int y, int z)
	{
		if ((z > maximumZ) || (z < minimumZ))
		{
			return false;
		}
		
		return zone.contains(x, y);
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
	 * Method getMaximumX.
	 * @return int
	 */
	public final int getMaximumX()
	{
		return maximumX;
	}
	
	/**
	 * Method getMaximumY.
	 * @return int
	 */
	public final int getMaximumY()
	{
		return maximumY;
	}
	
	/**
	 * Method getMaximumZ.
	 * @return int
	 */
	public final int getMaximumZ()
	{
		return maximumZ;
	}
	
	/**
	 * Method getMinimumX.
	 * @return int
	 */
	public final int getMinimumX()
	{
		return minimumX;
	}
	
	/**
	 * Method getMinimumY.
	 * @return int
	 */
	public final int getMinimumY()
	{
		return minimumY;
	}
	
	/**
	 * Method getMinimumZ.
	 * @return int
	 */
	public final int getMinimumZ()
	{
		return minimumZ;
	}
	
	/**
	 * Method getRespawn.
	 * @param object TObject
	 * @return Location
	 */
	public final Location getRespawn(TObject object)
	{
		Location target = null;
		float min = Float.MAX_VALUE;
		
		for (Location point : respawnPoints)
		{
			final float dist = object.getDistance(point.getX(), point.getY(), point.getZ());
			
			if (dist < min)
			{
				min = dist;
				target = point;
			}
		}
		
		if (target == null)
		{
			return WorldZoneTable.DEFAULT_RESPAWN_POINT;
		}
		
		return target;
	}
	
	/**
	 * Method getRespawnPoints.
	 * @return Location[]
	 */
	public final Location[] getRespawnPoints()
	{
		return respawnPoints;
	}
	
	/**
	 * Method getZone.
	 * @return Polygon
	 */
	public final Polygon getZone()
	{
		return zone;
	}
	
	/**
	 * Method getZoneId.
	 * @return int
	 */
	public final int getZoneId()
	{
		return zoneId;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "WorldZone maxX = " + maximumX + ", minX = " + minimumX + ", maxY = " + maximumY + ", minY = " + minimumY + ", maxZ = " + maximumZ + ", minZ = " + minimumZ + ", zoneId = " + zoneId + ", zone = " + zone + ", respawnPoints = " + Arrays.toString(respawnPoints);
	}
}
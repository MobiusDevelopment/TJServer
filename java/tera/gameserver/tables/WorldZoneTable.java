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
package tera.gameserver.tables;

import java.io.File;

import tera.Config;
import tera.gameserver.document.DocumentWorldZone;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.WorldZone;
import tera.util.Location;

import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 * @created 08.03.2012
 */
public final class WorldZoneTable
{
	public static final Location DEFAULT_RESPAWN_POINT = new Location(66596, -79856, -2994);
	public static final int CELL_POWER = 15;
	private static WorldZoneTable instance;
	
	/**
	 * Method getInstance.
	 * @return WorldZoneTable
	 */
	public static WorldZoneTable getInstance()
	{
		if (instance == null)
		{
			instance = new WorldZoneTable();
		}
		
		return instance;
	}
	
	private final Location[] respawnPoints;
	
	private WorldZoneTable()
	{
		final Table<WorldRegion, Array<WorldZone>> zones = Tables.newObjectTable();
		final Array<WorldZone> zons = new DocumentWorldZone(new File(Config.SERVER_DIR + "/data/zones.xml")).parse();
		final Array<Location> points = Arrays.toArray(Location.class);
		final Array<WorldRegion> regions = Arrays.toArray(WorldRegion.class);
		
		for (WorldZone worldZone : zons)
		{
			final int minimumX = (worldZone.getMinimumX() / World.REGION_WIDTH) + World.OFFSET_X;
			final int maximumX = (worldZone.getMaximumX() / World.REGION_WIDTH) + World.OFFSET_X;
			final int minimumY = (worldZone.getMinimumY() / World.REGION_WIDTH) + World.OFFSET_Y;
			final int maximumY = (worldZone.getMaximumY() / World.REGION_WIDTH) + World.OFFSET_Y;
			final int minimumZ = (worldZone.getMinimumZ() / World.REGION_HEIGHT) + World.OFFSET_Z;
			final int maximumZ = (worldZone.getMaximumZ() / World.REGION_HEIGHT) + World.OFFSET_Z;
			points.addAll(worldZone.getRespawnPoints());
			
			for (int x = minimumX; x <= maximumX; x++)
			{
				for (int y = minimumY; y <= maximumY; y++)
				{
					for (int z = minimumZ; z <= maximumZ; z++)
					{
						final WorldRegion region = World.region(worldZone.getContinentId(), x, y, z);
						Array<WorldZone> array = zones.get(region);
						
						if (array == null)
						{
							array = Arrays.toArray(WorldZone.class);
							zones.put(region, array);
						}
						
						if (!regions.contains(region))
						{
							regions.add(region);
						}
						
						if (array.contains(worldZone))
						{
							continue;
						}
						
						array.add(worldZone);
					}
				}
			}
		}
		
		for (WorldRegion region : regions)
		{
			final Array<WorldZone> array = zones.get(region);
			
			if (array.isEmpty())
			{
				continue;
			}
			
			array.trimToSize();
			region.setZones(array.array());
		}
		
		points.trimToSize();
		respawnPoints = points.array();
		Loggers.info(WorldZoneTable.class, "loaded " + zons.size() + " world zones for " + regions.size() + " regions.");
	}
	
	/**
	 * Method getDefaultRespawn.
	 * @param object TObject
	 * @return Location
	 */
	public final Location getDefaultRespawn(TObject object)
	{
		Location target = null;
		float min = Float.MAX_VALUE;
		
		for (Location point : respawnPoints)
		{
			if (point.getContinentId() != object.getContinentId())
			{
				continue;
			}
			
			final float dist = object.getDistance(point.getX(), point.getY(), point.getZ());
			
			if (dist < min)
			{
				min = dist;
				target = point;
			}
		}
		
		if (target != null)
		{
			return target;
		}
		
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
	 * Method getRespawn.
	 * @param object TObject
	 * @return Location
	 */
	public final Location getRespawn(TObject object)
	{
		final WorldRegion region = object.getCurrentRegion();
		
		if (region == null)
		{
			return getDefaultRespawn(object);
		}
		
		final WorldZone[] zones = region.getZones();
		
		if (zones == null)
		{
			return getDefaultRespawn(object);
		}
		
		final int x = (int) object.getX();
		final int y = (int) object.getY();
		final int z = (int) object.getZ();
		
		for (WorldZone zone : zones)
		{
			if (zone.contains(x, y, z))
			{
				return zone.getRespawn(object);
			}
		}
		
		return getDefaultRespawn(object);
	}
	
	/**
	 * Method getZoneId.
	 * @param object TObject
	 * @return int
	 */
	public final int getZoneId(TObject object)
	{
		final WorldRegion region = object.getCurrentRegion();
		
		if (region == null)
		{
			return -1;
		}
		
		return region.getZoneId(object);
	}
}
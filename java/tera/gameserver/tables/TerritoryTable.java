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
import tera.gameserver.document.DocumentTerritory;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.territory.Territory;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class TerritoryTable
{
	private static final Logger log = Loggers.getLogger(TerritoryTable.class);
	
	private static TerritoryTable instance;
	
	/**
	 * Method getInstance.
	 * @return TerritoryTable
	 */
	public static TerritoryTable getInstance()
	{
		if (instance == null)
		{
			instance = new TerritoryTable();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Territory> tableId;
	
	private final Table<String, Territory> tableName;
	
	private TerritoryTable()
	{
		tableId = Tables.newIntegerTable();
		tableName = Tables.newObjectTable();
		final Table<WorldRegion, Array<Territory>> territories = Tables.newObjectTable();
		final Array<WorldRegion> regions = Arrays.toArray(WorldRegion.class);
		final Array<Territory> parsed = new DocumentTerritory(new File(Config.SERVER_DIR + "/data/territories.xml")).parse();
		
		for (Territory territory : parsed)
		{
			final int minimumX = (territory.getMinimumX() / World.REGION_WIDTH) + World.OFFSET_X;
			final int maximumX = (territory.getMaximumX() / World.REGION_WIDTH) + World.OFFSET_X;
			final int minimumY = (territory.getMinimumY() / World.REGION_WIDTH) + World.OFFSET_Y;
			final int maximumY = (territory.getMaximumY() / World.REGION_WIDTH) + World.OFFSET_Y;
			final int minimumZ = (territory.getMinimumZ() / World.REGION_HEIGHT) + World.OFFSET_Z;
			final int maximumZ = (territory.getMaximumZ() / World.REGION_HEIGHT) + World.OFFSET_Z;
			
			for (int x = minimumX; x <= maximumX; x++)
			{
				for (int y = minimumY; y <= maximumY; y++)
				{
					for (int z = minimumZ; z <= maximumZ; z++)
					{
						final WorldRegion region = World.region(territory.getContinentId(), x, y, z);
						Array<Territory> array = territories.get(region);
						
						if (array == null)
						{
							array = Arrays.toArray(Territory.class);
							territories.put(region, array);
						}
						
						if (!regions.contains(region))
						{
							regions.add(region);
						}
						
						if (array.contains(territory))
						{
							continue;
						}
						
						array.add(territory);
						WorldRegion[] regs = territory.getRegions();
						
						if ((regs == null) || !Arrays.contains(regs, region))
						{
							regs = Arrays.addToArray(regs, region, WorldRegion.class);
							territory.setRegions(regs);
						}
					}
				}
			}
			
			if (tableId.containsKey(territory.getId()))
			{
				log.warning("found duplicate territory " + territory.getId());
			}
			
			if (tableName.containsKey(territory.getName()))
			{
				log.warning("found duplicate territory " + territory.getName());
			}
			
			tableId.put(territory.getId(), territory);
			tableName.put(territory.getName(), territory);
		}
		
		for (WorldRegion region : regions)
		{
			final Array<Territory> array = territories.get(region);
			
			if (array.isEmpty())
			{
				continue;
			}
			
			array.trimToSize();
			region.setTerritories(array.array());
		}
		
		log.info("loaded " + parsed.size() + " territories for " + regions.size() + " regions.");
	}
	
	/**
	 * Method getTerritory.
	 * @param id int
	 * @return Territory
	 */
	public Territory getTerritory(int id)
	{
		return tableId.get(id);
	}
	
	/**
	 * Method getTerritory.
	 * @param name String
	 * @return Territory
	 */
	public Territory getTerritory(String name)
	{
		return tableName.get(name);
	}
	
	/**
	 * Method onEnterWorld.
	 * @param object TObject
	 * @return boolean
	 */
	public boolean onEnterWorld(TObject object)
	{
		final WorldRegion region = object.getCurrentRegion();
		
		if (region == null)
		{
			return false;
		}
		
		final Territory[] terrs = region.getTerritories();
		
		if ((terrs == null) || (terrs.length == 0))
		{
			return false;
		}
		
		final Array<Territory> territories = object.getTerritories();
		
		for (Territory territory : terrs)
		{
			if (territory.contains(object.getX(), object.getY(), object.getZ()))
			{
				if (territories.contains(territory))
				{
					continue;
				}
				
				territories.add(territory);
			}
		}
		
		if (territories.isEmpty())
		{
			return true;
		}
		
		final Territory[] result = territories.array();
		
		for (int i = 0, length = territories.size(); i < length; i++)
		{
			result[i].onEnter(object);
		}
		
		return true;
	}
	
	/**
	 * Method onExitWorld.
	 * @param object TObject
	 * @return boolean
	 */
	public boolean onExitWorld(TObject object)
	{
		final Array<Territory> territories = object.getTerritories();
		
		if (territories.isEmpty())
		{
			return false;
		}
		
		territories.readLock();
		
		try
		{
			final Territory[] result = territories.array();
			
			for (int i = 0, length = territories.size(); i < length; i++)
			{
				result[i].onExit(object);
			}
		}
		
		finally
		{
			territories.readUnlock();
		}
		territories.clear();
		return true;
	}
}

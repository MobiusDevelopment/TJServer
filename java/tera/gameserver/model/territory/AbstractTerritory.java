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
package tera.gameserver.model.territory;

import java.awt.Polygon;

import org.w3c.dom.Node;

import tera.gameserver.model.TObject;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.listeners.TerritoryListener;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class AbstractTerritory implements Territory
{
	protected static final Logger log = Loggers.getLogger(Territory.class);
	
	protected final int id;
	
	protected final int continentId;
	
	protected int minimumX;
	protected int maximumX;
	
	protected int minimumY;
	protected int maximumY;
	
	protected int minimumZ;
	protected int maximumZ;
	
	protected final String name;
	
	protected final Polygon territory;
	
	protected final TerritoryType type;
	
	protected final Array<TerritoryListener> listeners;
	
	protected final Array<TObject> objects;
	
	protected WorldRegion[] regions;
	
	/**
	 * Constructor for AbstractTerritory.
	 * @param node Node
	 * @param type TerritoryType
	 */
	public AbstractTerritory(Node node, TerritoryType type)
	{
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			id = vars.getInteger("id");
			continentId = vars.getInteger("continentId", 0);
			maximumZ = vars.getInteger("maxZ");
			minimumZ = vars.getInteger("minZ");
			name = vars.getString("name");
			this.type = type;
			minimumX = Integer.MAX_VALUE;
			maximumX = Integer.MIN_VALUE;
			minimumY = Integer.MAX_VALUE;
			maximumY = Integer.MIN_VALUE;
			territory = new Polygon();
			listeners = Arrays.toConcurrentArray(TerritoryListener.class);
			objects = Arrays.toConcurrentArray(TObject.class);
			
			for (Node point = node.getFirstChild(); point != null; point = point.getNextSibling())
			{
				if ("point".equals(point.getNodeName()))
				{
					vars.parse(point);
					addPoint((int) vars.getFloat("x"), (int) vars.getFloat("y"));
				}
			}
		}
		catch (Exception e)
		{
			log.warning(e);
			throw e;
		}
	}
	
	/**
	 * Method addListener.
	 * @param listener TerritoryListener
	 * @see tera.gameserver.model.territory.Territory#addListener(TerritoryListener)
	 */
	@Override
	public final void addListener(TerritoryListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Method addPoint.
	 * @param x int
	 * @param y int
	 * @see tera.gameserver.model.territory.Territory#addPoint(int, int)
	 */
	@Override
	public final void addPoint(int x, int y)
	{
		minimumX = Math.min(minimumX, x);
		maximumX = Math.max(maximumX, x);
		minimumY = Math.min(minimumY, y);
		maximumY = Math.max(maximumY, y);
		territory.addPoint(x, y);
	}
	
	/**
	 * Method contains.
	 * @param x float
	 * @param y float
	 * @return boolean
	 * @see tera.gameserver.model.territory.Territory#contains(float, float)
	 */
	@Override
	public final boolean contains(float x, float y)
	{
		return territory.contains(x, y);
	}
	
	/**
	 * Method contains.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @return boolean
	 * @see tera.gameserver.model.territory.Territory#contains(float, float, float)
	 */
	@Override
	public final boolean contains(float x, float y, float z)
	{
		if ((z > maximumZ) || (z < minimumZ))
		{
			return false;
		}
		
		return contains(x, y);
	}
	
	/**
	 * Method getContinentId.
	 * @return int
	 * @see tera.gameserver.model.territory.Territory#getContinentId()
	 */
	@Override
	public int getContinentId()
	{
		return continentId;
	}
	
	/**
	 * Method getId.
	 * @return int
	 * @see tera.gameserver.model.territory.Territory#getId()
	 */
	@Override
	public final int getId()
	{
		return id;
	}
	
	/**
	 * Method getMaximumX.
	 * @return int
	 * @see tera.gameserver.model.territory.Territory#getMaximumX()
	 */
	@Override
	public final int getMaximumX()
	{
		return maximumX;
	}
	
	/**
	 * Method getMaximumY.
	 * @return int
	 * @see tera.gameserver.model.territory.Territory#getMaximumY()
	 */
	@Override
	public final int getMaximumY()
	{
		return maximumY;
	}
	
	/**
	 * Method getMaximumZ.
	 * @return int
	 * @see tera.gameserver.model.territory.Territory#getMaximumZ()
	 */
	@Override
	public final int getMaximumZ()
	{
		return maximumZ;
	}
	
	/**
	 * Method getMinimumX.
	 * @return int
	 * @see tera.gameserver.model.territory.Territory#getMinimumX()
	 */
	@Override
	public final int getMinimumX()
	{
		return minimumX;
	}
	
	/**
	 * Method getMinimumY.
	 * @return int
	 * @see tera.gameserver.model.territory.Territory#getMinimumY()
	 */
	@Override
	public final int getMinimumY()
	{
		return minimumY;
	}
	
	/**
	 * Method getMinimumZ.
	 * @return int
	 * @see tera.gameserver.model.territory.Territory#getMinimumZ()
	 */
	@Override
	public final int getMinimumZ()
	{
		return minimumZ;
	}
	
	/**
	 * Method getName.
	 * @return String
	 * @see tera.gameserver.model.territory.Territory#getName()
	 */
	@Override
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getObjects.
	 * @return Array<TObject>
	 * @see tera.gameserver.model.territory.Territory#getObjects()
	 */
	@Override
	public Array<TObject> getObjects()
	{
		return objects;
	}
	
	/**
	 * Method getRegions.
	 * @return WorldRegion[]
	 * @see tera.gameserver.model.territory.Territory#getRegions()
	 */
	@Override
	public WorldRegion[] getRegions()
	{
		return regions;
	}
	
	/**
	 * Method getType.
	 * @return TerritoryType
	 * @see tera.gameserver.model.territory.Territory#getType()
	 */
	@Override
	public final TerritoryType getType()
	{
		return type;
	}
	
	/**
	 * Method hashCode.
	 * @return int
	 */
	@Override
	public final int hashCode()
	{
		return id;
	}
	
	/**
	 * Method onEnter.
	 * @param object TObject
	 * @see tera.gameserver.model.territory.Territory#onEnter(TObject)
	 */
	@Override
	public void onEnter(TObject object)
	{
		objects.add(object);
		
		if (listeners.isEmpty())
		{
			return;
		}
		
		listeners.readLock();
		
		try
		{
			final TerritoryListener[] array = listeners.array();
			
			for (int i = 0, length = listeners.size(); i < length; i++)
			{
				array[i].onEnter(this, object);
			}
		}
		
		finally
		{
			listeners.readUnlock();
		}
	}
	
	/**
	 * Method onExit.
	 * @param object TObject
	 * @see tera.gameserver.model.territory.Territory#onExit(TObject)
	 */
	@Override
	public void onExit(TObject object)
	{
		objects.fastRemove(object);
		
		if (listeners.isEmpty())
		{
			return;
		}
		
		listeners.readLock();
		
		try
		{
			final TerritoryListener[] array = listeners.array();
			
			for (int i = 0, length = listeners.size(); i < length; i++)
			{
				array[i].onExit(this, object);
			}
		}
		
		finally
		{
			listeners.readUnlock();
		}
	}
	
	/**
	 * Method removeListener.
	 * @param listener TerritoryListener
	 * @see tera.gameserver.model.territory.Territory#removeListener(TerritoryListener)
	 */
	@Override
	public final void removeListener(TerritoryListener listener)
	{
		listeners.fastRemove(listener);
	}
	
	/**
	 * Method setRegions.
	 * @param regions WorldRegion[]
	 * @see tera.gameserver.model.territory.Territory#setRegions(WorldRegion[])
	 */
	@Override
	public void setRegions(WorldRegion[] regions)
	{
		this.regions = regions;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "Territory id = " + id + ", " + "type = " + type;
	}
}

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

import tera.gameserver.model.TObject;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.listeners.TerritoryListener;

import rlib.util.array.Array;

/**
 * @author Ronn
 * @created 13.04.2012
 */
public interface Territory
{
	/**
	 * Method addListener.
	 * @param listener TerritoryListener
	 */
	public void addListener(TerritoryListener listener);
	
	/**
	 * Method addPoint.
	 * @param x int
	 * @param y int
	 */
	public void addPoint(int x, int y);
	
	/**
	 * Method contains.
	 * @param x float
	 * @param y float
	 * @return boolean
	 */
	public boolean contains(float x, float y);
	
	/**
	 * Method contains.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @return boolean
	 */
	public boolean contains(float x, float y, float z);
	
	/**
	 * Method getContinentId.
	 * @return int
	 */
	public int getContinentId();
	
	/**
	 * Method getId.
	 * @return int
	 */
	public int getId();
	
	/**
	 * Method getMaximumX.
	 * @return int
	 */
	public int getMaximumX();
	
	/**
	 * Method getMaximumY.
	 * @return int
	 */
	public int getMaximumY();
	
	/**
	 * Method getMaximumZ.
	 * @return int
	 */
	public int getMaximumZ();
	
	/**
	 * Method getMinimumX.
	 * @return int
	 */
	public int getMinimumX();
	
	/**
	 * Method getMinimumY.
	 * @return int
	 */
	public int getMinimumY();
	
	/**
	 * Method getMinimumZ.
	 * @return int
	 */
	public int getMinimumZ();
	
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName();
	
	/**
	 * Method getObjects.
	 * @return Array<TObject>
	 */
	public Array<TObject> getObjects();
	
	/**
	 * Method getRegions.
	 * @return WorldRegion[]
	 */
	public WorldRegion[] getRegions();
	
	/**
	 * Method getType.
	 * @return TerritoryType
	 */
	public TerritoryType getType();
	
	/**
	 * Method onEnter.
	 * @param object TObject
	 */
	public void onEnter(TObject object);
	
	/**
	 * Method onExit.
	 * @param object TObject
	 */
	public void onExit(TObject object);
	
	/**
	 * Method removeListener.
	 * @param listener TerritoryListener
	 */
	public void removeListener(TerritoryListener listener);
	
	/**
	 * Method setRegions.
	 * @param regions WorldRegion[]
	 */
	public void setRegions(WorldRegion[] regions);
}

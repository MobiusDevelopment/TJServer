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
	void addListener(TerritoryListener listener);
	
	/**
	 * Method addPoint.
	 * @param x int
	 * @param y int
	 */
	void addPoint(int x, int y);
	
	/**
	 * Method contains.
	 * @param x float
	 * @param y float
	 * @return boolean
	 */
	boolean contains(float x, float y);
	
	/**
	 * Method contains.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @return boolean
	 */
	boolean contains(float x, float y, float z);
	
	/**
	 * Method getContinentId.
	 * @return int
	 */
	int getContinentId();
	
	/**
	 * Method getId.
	 * @return int
	 */
	int getId();
	
	/**
	 * Method getMaximumX.
	 * @return int
	 */
	int getMaximumX();
	
	/**
	 * Method getMaximumY.
	 * @return int
	 */
	int getMaximumY();
	
	/**
	 * Method getMaximumZ.
	 * @return int
	 */
	int getMaximumZ();
	
	/**
	 * Method getMinimumX.
	 * @return int
	 */
	int getMinimumX();
	
	/**
	 * Method getMinimumY.
	 * @return int
	 */
	int getMinimumY();
	
	/**
	 * Method getMinimumZ.
	 * @return int
	 */
	int getMinimumZ();
	
	/**
	 * Method getName.
	 * @return String
	 */
	String getName();
	
	/**
	 * Method getObjects.
	 * @return Array<TObject>
	 */
	Array<TObject> getObjects();
	
	/**
	 * Method getRegions.
	 * @return WorldRegion[]
	 */
	WorldRegion[] getRegions();
	
	/**
	 * Method getType.
	 * @return TerritoryType
	 */
	TerritoryType getType();
	
	/**
	 * Method onEnter.
	 * @param object TObject
	 */
	void onEnter(TObject object);
	
	/**
	 * Method onExit.
	 * @param object TObject
	 */
	void onExit(TObject object);
	
	/**
	 * Method removeListener.
	 * @param listener TerritoryListener
	 */
	void removeListener(TerritoryListener listener);
	
	/**
	 * Method setRegions.
	 * @param regions WorldRegion[]
	 */
	void setRegions(WorldRegion[] regions);
}

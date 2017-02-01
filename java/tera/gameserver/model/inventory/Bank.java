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
package tera.gameserver.model.inventory;

import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.ItemLocation;

import rlib.util.Synchronized;
import rlib.util.pools.Foldable;

/**
 * @author Ronn
 */
public interface Bank extends Foldable, Synchronized
{
	/**
	 * Method addItem.
	 * @param itemId int
	 * @param itemCount int
	 * @return boolean
	 */
	boolean addItem(int itemId, int itemCount);
	
	/**
	 * Method addMoney.
	 * @param count long
	 */
	void addMoney(long count);
	
	void fold();
	
	/**
	 * Method getCell.
	 * @param index int
	 * @return Cell
	 */
	Cell getCell(int index);
	
	/**
	 * Method getCells.
	 * @return Cell[]
	 */
	Cell[] getCells();
	
	/**
	 * Method getItemForObjectId.
	 * @param objectId int
	 * @return ItemInstance
	 */
	ItemInstance getItemForObjectId(int objectId);
	
	/**
	 * Method getLastIndex.
	 * @return int
	 */
	int getLastIndex();
	
	/**
	 * Method getLocation.
	 * @return ItemLocation
	 */
	ItemLocation getLocation();
	
	/**
	 * Method getMaxSize.
	 * @return int
	 */
	int getMaxSize();
	
	/**
	 * Method getMoney.
	 * @return long
	 */
	long getMoney();
	
	/**
	 * Method getOwner.
	 * @return Object
	 */
	Object getOwner();
	
	/**
	 * Method getTabSize.
	 * @return int
	 */
	int getTabSize();
	
	/**
	 * Method getUsedCount.
	 * @return int
	 */
	int getUsedCount();
	
	/**
	 * Method putItem.
	 * @param item ItemInstance
	 * @return boolean
	 */
	boolean putItem(ItemInstance item);
	
	/**
	 * Method removeItem.
	 * @param itemId int
	 * @param itemCount int
	 * @return boolean
	 */
	boolean removeItem(int itemId, int itemCount);
	
	/**
	 * Method removeItem.
	 * @param item ItemInstance
	 * @return boolean
	 */
	boolean removeItem(ItemInstance item);
	
	/**
	 * Method setItem.
	 * @param index int
	 * @param item ItemInstance
	 * @return boolean
	 */
	boolean setItem(int index, ItemInstance item);
	
	/**
	 * Method setOwner.
	 * @param owner Object
	 */
	void setOwner(Object owner);
	
	/**
	 * Method sort.
	 * @return boolean
	 */
	boolean sort();
	
	/**
	 * Method subMoney.
	 * @param count long
	 */
	void subMoney(long count);
}
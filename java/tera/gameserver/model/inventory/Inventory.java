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

import tera.gameserver.model.Character;
import tera.gameserver.model.items.ItemInstance;

import rlib.util.Synchronized;
import rlib.util.pools.Foldable;

/**
 * @author Ronn
 * @created 11.04.2012
 */
public interface Inventory extends Foldable, Synchronized
{
	
	int MONEY_ITEM_ID = 20000000;
	
	/**
	 * Method addItem.
	 * @param itemId int
	 * @param count long
	 * @param autor String
	 * @return boolean
	 */
	boolean addItem(int itemId, long count, String autor);
	
	void addLevel();
	
	/**
	 * Method addMoney.
	 * @param count long
	 */
	void addMoney(long count);
	
	/**
	 * Method containsItems.
	 * @param itemId int
	 * @param itemCount int
	 * @return boolean
	 */
	boolean containsItems(int itemId, int itemCount);
	
	void fold();
	
	/**
	 * Method forceAddItem.
	 * @param itemId int
	 * @param count long
	 * @param autor String
	 * @return boolean
	 */
	boolean forceAddItem(int itemId, long count, String autor);
	
	/**
	 * Method getAllMaxCells.
	 * @return int
	 */
	int getAllMaxCells();
	
	/**
	 * Method getBaseLevel.
	 * @return int
	 */
	int getBaseLevel();
	
	/**
	 * Method getCell.
	 * @param index int
	 * @return Cell
	 */
	Cell getCell(int index);
	
	/**
	 * Method getCellForObjectId.
	 * @param objectId int
	 * @return Cell
	 */
	Cell getCellForObjectId(int objectId);
	
	/**
	 * Method getCells.
	 * @return Cell[]
	 */
	Cell[] getCells();
	
	/**
	 * Method getEngagedCells.
	 * @return int
	 */
	int getEngagedCells();
	
	/**
	 * Method getFreeCells.
	 * @return int
	 */
	int getFreeCells();
	
	/**
	 * Method getGold.
	 * @return Cell
	 */
	Cell getGold();
	
	/**
	 * Method getItemCount.
	 * @param itemId int
	 * @return int
	 */
	int getItemCount(int itemId);
	
	/**
	 * Method getItemForItemId.
	 * @param itemId int
	 * @return ItemInstance
	 */
	ItemInstance getItemForItemId(int itemId);
	
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
	 * Method getLevel.
	 * @return int
	 */
	int getLevel();
	
	/**
	 * Method getLevelBonus.
	 * @return int
	 */
	int getLevelBonus();
	
	/**
	 * Method getMaxCells.
	 * @return int
	 */
	int getMaxCells();
	
	/**
	 * Method getMoney.
	 * @return long
	 */
	long getMoney();
	
	/**
	 * Method getOwner.
	 * @return Character
	 */
	Character getOwner();
	
	/**
	 * Method moveItem.
	 * @param item ItemInstance
	 * @param source Inventory
	 * @return boolean
	 */
	boolean moveItem(ItemInstance item, Inventory source);
	
	/**
	 * Method putItem.
	 * @param item ItemInstance
	 * @return boolean
	 */
	boolean putItem(ItemInstance item);
	
	/**
	 * Method removeItem.
	 * @param itemId int
	 * @return boolean
	 */
	boolean removeItem(int itemId);
	
	/**
	 * Method removeItem.
	 * @param itemId int
	 * @param count long
	 * @return boolean
	 */
	boolean removeItem(int itemId, long count);
	
	/**
	 * Method removeItem.
	 * @param item ItemInstance
	 * @return boolean
	 */
	boolean removeItem(ItemInstance item);
	
	/**
	 * Method removeItemFromIndex.
	 * @param count long
	 * @param index int
	 * @return boolean
	 */
	boolean removeItemFromIndex(long count, int index);
	
	/**
	 * Method setCells.
	 * @param cells Cell[]
	 */
	void setCells(Cell[] cells);
	
	/**
	 * Method setGold.
	 * @param gold Cell
	 */
	void setGold(Cell gold);
	
	/**
	 * Method setItem.
	 * @param item ItemInstance
	 * @param index int
	 * @return boolean
	 */
	boolean setItem(ItemInstance item, int index);
	
	/**
	 * Method setOwner.
	 * @param owenr Character
	 */
	void setOwner(Character owenr);
	
	/**
	 * Method sort.
	 * @return boolean
	 */
	boolean sort();
	
	void subLevel();
	
	/**
	 * Method subMoney.
	 * @param count long
	 */
	void subMoney(long count);
}

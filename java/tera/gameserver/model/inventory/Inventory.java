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
	
	public static final int MONEY_ITEM_ID = 20000000;
	
	/**
	 * Method addItem.
	 * @param itemId int
	 * @param count long
	 * @param autor String
	 * @return boolean
	 */
	public boolean addItem(int itemId, long count, String autor);
	
	public void addLevel();
	
	/**
	 * Method addMoney.
	 * @param count long
	 */
	public void addMoney(long count);
	
	/**
	 * Method containsItems.
	 * @param itemId int
	 * @param itemCount int
	 * @return boolean
	 */
	public boolean containsItems(int itemId, int itemCount);
	
	public void fold();
	
	/**
	 * Method forceAddItem.
	 * @param itemId int
	 * @param count long
	 * @param autor String
	 * @return boolean
	 */
	public boolean forceAddItem(int itemId, long count, String autor);
	
	/**
	 * Method getAllMaxCells.
	 * @return int
	 */
	public int getAllMaxCells();
	
	/**
	 * Method getBaseLevel.
	 * @return int
	 */
	public int getBaseLevel();
	
	/**
	 * Method getCell.
	 * @param index int
	 * @return Cell
	 */
	public Cell getCell(int index);
	
	/**
	 * Method getCellForObjectId.
	 * @param objectId int
	 * @return Cell
	 */
	public Cell getCellForObjectId(int objectId);
	
	/**
	 * Method getCells.
	 * @return Cell[]
	 */
	public Cell[] getCells();
	
	/**
	 * Method getEngagedCells.
	 * @return int
	 */
	public int getEngagedCells();
	
	/**
	 * Method getFreeCells.
	 * @return int
	 */
	public int getFreeCells();
	
	/**
	 * Method getGold.
	 * @return Cell
	 */
	public Cell getGold();
	
	/**
	 * Method getItemCount.
	 * @param itemId int
	 * @return int
	 */
	public int getItemCount(int itemId);
	
	/**
	 * Method getItemForItemId.
	 * @param itemId int
	 * @return ItemInstance
	 */
	public ItemInstance getItemForItemId(int itemId);
	
	/**
	 * Method getItemForObjectId.
	 * @param objectId int
	 * @return ItemInstance
	 */
	public ItemInstance getItemForObjectId(int objectId);
	
	/**
	 * Method getLastIndex.
	 * @return int
	 */
	public int getLastIndex();
	
	/**
	 * Method getLevel.
	 * @return int
	 */
	public int getLevel();
	
	/**
	 * Method getLevelBonus.
	 * @return int
	 */
	public int getLevelBonus();
	
	/**
	 * Method getMaxCells.
	 * @return int
	 */
	public int getMaxCells();
	
	/**
	 * Method getMoney.
	 * @return long
	 */
	public long getMoney();
	
	/**
	 * Method getOwner.
	 * @return Character
	 */
	public Character getOwner();
	
	/**
	 * Method moveItem.
	 * @param item ItemInstance
	 * @param source Inventory
	 * @return boolean
	 */
	public boolean moveItem(ItemInstance item, Inventory source);
	
	/**
	 * Method putItem.
	 * @param item ItemInstance
	 * @return boolean
	 */
	public boolean putItem(ItemInstance item);
	
	/**
	 * Method removeItem.
	 * @param itemId int
	 * @return boolean
	 */
	public boolean removeItem(int itemId);
	
	/**
	 * Method removeItem.
	 * @param itemId int
	 * @param count long
	 * @return boolean
	 */
	public boolean removeItem(int itemId, long count);
	
	/**
	 * Method removeItem.
	 * @param item ItemInstance
	 * @return boolean
	 */
	public boolean removeItem(ItemInstance item);
	
	/**
	 * Method removeItemFromIndex.
	 * @param count long
	 * @param index int
	 * @return boolean
	 */
	public boolean removeItemFromIndex(long count, int index);
	
	/**
	 * Method setCells.
	 * @param cells Cell[]
	 */
	public void setCells(Cell[] cells);
	
	/**
	 * Method setGold.
	 * @param gold Cell
	 */
	public void setGold(Cell gold);
	
	/**
	 * Method setItem.
	 * @param item ItemInstance
	 * @param index int
	 * @return boolean
	 */
	public boolean setItem(ItemInstance item, int index);
	
	/**
	 * Method setOwner.
	 * @param owenr Character
	 */
	public void setOwner(Character owenr);
	
	/**
	 * Method sort.
	 * @return boolean
	 */
	public boolean sort();
	
	public void subLevel();
	
	/**
	 * Method subMoney.
	 * @param count long
	 */
	public void subMoney(long count);
}

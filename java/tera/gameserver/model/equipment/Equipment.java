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
package tera.gameserver.model.equipment;

import tera.gameserver.model.Character;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;

import rlib.util.Synchronized;
import rlib.util.pools.Foldable;

/**
 * @author Ronn
 * @created 12.04.2012
 */
public interface Equipment extends Foldable, Synchronized
{
	/**
	 * Method dressItem.
	 * @param inventory Inventory
	 * @param cell Cell
	 * @return boolean
	 */
	boolean dressItem(Inventory inventory, Cell cell);
	
	/**
	 * Method equiped.
	 * @param item ItemInstance
	 * @return boolean
	 */
	boolean equiped(ItemInstance item);
	
	void fold();
	
	/**
	 * Method getCount.
	 * @param type SlotType
	 * @return int
	 */
	int getCount(SlotType type);
	
	/**
	 * Method getEngagedSlots.
	 * @return int
	 */
	int getEngagedSlots();
	
	/**
	 * Method getItem.
	 * @param index int
	 * @return ItemInstance
	 */
	ItemInstance getItem(int index);
	
	/**
	 * Method getItem.
	 * @param type SlotType
	 * @return ItemInstance
	 */
	ItemInstance getItem(SlotType type);
	
	/**
	 * Method getItemId.
	 * @param type SlotType
	 * @return int
	 */
	int getItemId(SlotType type);
	
	/**
	 * Method getOwner.
	 * @return Character
	 */
	Character getOwner();
	
	/**
	 * Method getSlotForObjectId.
	 * @param objectId int
	 * @return Slot
	 */
	Slot getSlotForObjectId(int objectId);
	
	/**
	 * Method getSlots.
	 * @return Slot[]
	 */
	Slot[] getSlots();
	
	/**
	 * Method recreateSlots.
	 * @param slotTypes SlotType[]
	 */
	void recreateSlots(SlotType... slotTypes);
	
	/**
	 * Method setItem.
	 * @param item ItemInstance
	 * @param index int
	 * @return boolean
	 */
	boolean setItem(ItemInstance item, int index);
	
	/**
	 * Method setOwner.
	 * @param owner Character
	 * @return Equipment
	 */
	Equipment setOwner(Character owner);
	
	/**
	 * Method setSlots.
	 * @param slots Slot[]
	 */
	void setSlots(Slot[] slots);
	
	/**
	 * Method shootItem.
	 * @param inventory Inventory
	 * @param index int
	 * @param itemId int
	 * @return boolean
	 */
	boolean shootItem(Inventory inventory, int index, int itemId);
	
	/**
	 * Method size.
	 * @return int
	 */
	int size();
	
	/**
	 * Method unequiped.
	 * @param item ItemInstance
	 * @return boolean
	 */
	boolean unequiped(ItemInstance item);
}

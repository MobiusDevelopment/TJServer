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

import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.ItemLocation;

/**
 * @author Ronn
 */
public final class Slot
{
	
	private SlotType type;
	
	private ItemInstance item;
	
	private int index;
	
	/**
	 * Constructor for Slot.
	 * @param type SlotType
	 * @param index int
	 */
	public Slot(SlotType type, int index)
	{
		this.type = type;
		this.index = index;
	}
	
	/**
	 * Method getIndex.
	 * @return int
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Method getItem.
	 * @return ItemInstance
	 */
	public ItemInstance getItem()
	{
		return item;
	}
	
	/**
	 * Method getType.
	 * @return SlotType
	 */
	public SlotType getType()
	{
		return type;
	}
	
	/**
	 * Method isEmpty.
	 * @return boolean
	 */
	public boolean isEmpty()
	{
		return item == null;
	}
	
	/**
	 * Method setIndex.
	 * @param index int
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	/**
	 * Method setItem.
	 * @param newItem ItemInstance
	 */
	public void setItem(ItemInstance newItem)
	{
		item = newItem;
		
		if (item != null)
		{
			item.setLocation(ItemLocation.EQUIPMENT);
			item.setIndex(index);
		}
	}
	
	/**
	 * Method setType.
	 * @param type SlotType
	 */
	public void setType(SlotType type)
	{
		this.type = type;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "Slot  " + (type != null ? "type = " + type + ", " : "") + (item != null ? "item = " + item + ", " : "") + "index = " + index;
	}
}

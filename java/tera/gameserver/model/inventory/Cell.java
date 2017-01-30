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

/**
 * @author Ronn
 */
public final class Cell implements Comparable<Cell>
{
	
	private ItemInstance item;
	
	private final ItemLocation location;
	
	private int index;
	
	/**
	 * Constructor for Cell.
	 * @param index int
	 * @param location ItemLocation
	 */
	public Cell(int index, ItemLocation location)
	{
		this.index = index;
		this.location = location;
	}
	
	/**
	 * Method compareTo.
	 * @param cell Cell
	 * @return int
	 */
	@Override
	public int compareTo(Cell cell)
	{
		return (item == null ? 1 : 0) - (cell.getItem() == null ? 1 : 0);
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
	 * Method getItemCount.
	 * @return long
	 */
	public long getItemCount()
	{
		return item == null ? 0 : item.getItemCount();
	}
	
	/**
	 * Method getItemId.
	 * @return int
	 */
	public int getItemId()
	{
		return item == null ? 0 : item.getItemId();
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
	 * @param item ItemInstance
	 */
	public void setItem(ItemInstance item)
	{
		this.item = item;
		
		if (item != null)
		{
			item.setLocation(location);
			item.setIndex(index);
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "index = " + index + ", item = " + item;
	}
}

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
package tera.gameserver.model.drop;

import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 */
public final class DropInfo
{
	private final ItemTemplate item;
	private final int minCount;
	private final int maxCount;
	private final int chance;
	
	/**
	 * Constructor for DropInfo.
	 * @param item ItemTemplate
	 * @param minCount int
	 * @param maxCount int
	 * @param chance int
	 */
	public DropInfo(ItemTemplate item, int minCount, int maxCount, int chance)
	{
		super();
		this.item = item;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.chance = chance;
	}
	
	/**
	 * Method getChance.
	 * @return int
	 */
	public final int getChance()
	{
		return chance;
	}
	
	/**
	 * Method getItem.
	 * @return ItemTemplate
	 */
	public final ItemTemplate getItem()
	{
		return item;
	}
	
	/**
	 * Method getItemId.
	 * @return int
	 */
	public final int getItemId()
	{
		return item.getItemId();
	}
	
	/**
	 * Method getMaxCount.
	 * @return int
	 */
	public final int getMaxCount()
	{
		return maxCount;
	}
	
	/**
	 * Method getMinCount.
	 * @return int
	 */
	public final int getMinCount()
	{
		return minCount;
	}
}
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
package tera.gameserver.model;

import tera.gameserver.model.territory.LocalTerritory;

/**
 * @author Ronn
 */
public final class TeleportRegion
{
	private LocalTerritory region;
	private int price;
	private int index;
	
	/**
	 * Constructor for TeleportRegion.
	 * @param region LocalTerritory
	 * @param price int
	 * @param index int
	 */
	public TeleportRegion(LocalTerritory region, int price, int index)
	{
		this.region = region;
		this.price = price;
		this.index = index;
	}
	
	/**
	 * Method getIndex.
	 * @return int
	 */
	public final int getIndex()
	{
		return index;
	}
	
	/**
	 * Method getPrice.
	 * @return int
	 */
	public final int getPrice()
	{
		return price;
	}
	
	/**
	 * Method getRegion.
	 * @return LocalTerritory
	 */
	public final LocalTerritory getRegion()
	{
		return region;
	}
	
	/**
	 * Method setIndex.
	 * @param index int
	 */
	public final void setIndex(int index)
	{
		this.index = index;
	}
	
	/**
	 * Method setPrice.
	 * @param price int
	 */
	public final void setPrice(int price)
	{
		this.price = price;
	}
	
	/**
	 * Method setRegion.
	 * @param region LocalTerritory
	 */
	public final void setRegion(LocalTerritory region)
	{
		this.region = region;
	}
}
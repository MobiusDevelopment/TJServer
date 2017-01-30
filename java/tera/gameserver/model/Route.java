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

/**
 * @author Ronn
 * @created 26.02.2012
 */
public final class Route
{
	private final TownInfo target;
	private final int index;
	private final int price;
	private final boolean local;
	
	/**
	 * Constructor for Route.
	 * @param index int
	 * @param price int
	 * @param target TownInfo
	 * @param local boolean
	 */
	public Route(int index, int price, TownInfo target, boolean local)
	{
		this.index = index;
		this.price = price;
		this.target = target;
		this.local = local;
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
	 * Method getPrice.
	 * @return int
	 */
	public int getPrice()
	{
		return price;
	}
	
	/**
	 * Method getTarget.
	 * @return TownInfo
	 */
	public TownInfo getTarget()
	{
		return target;
	}
	
	/**
	 * Method isLocal.
	 * @return boolean
	 */
	public final boolean isLocal()
	{
		return local;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "Route index = " + index + ", price = " + price + ", target = " + target;
	}
}
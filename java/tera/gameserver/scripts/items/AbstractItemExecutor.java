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
package tera.gameserver.scripts.items;

import java.util.Arrays;

import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 * @created 27.03.2012
 */
public abstract class AbstractItemExecutor implements ItemExecutor
{
	protected static final Logger log = Loggers.getLogger(ItemExecutor.class);
	
	private final int[] itemIds;
	
	private final int access;
	
	/**
	 * Constructor for AbstractItemExecutor.
	 * @param itemIds int[]
	 * @param access int
	 */
	public AbstractItemExecutor(int[] itemIds, int access)
	{
		this.itemIds = itemIds;
		this.access = access;
	}
	
	/**
	 * Method getAccess.
	 * @return int
	 * @see tera.gameserver.scripts.items.ItemExecutor#getAccess()
	 */
	@Override
	public int getAccess()
	{
		return access;
	}
	
	/**
	 * Method getItemIds.
	 * @return int[]
	 * @see tera.gameserver.scripts.items.ItemExecutor#getItemIds()
	 */
	@Override
	public int[] getItemIds()
	{
		return itemIds;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + (itemIds != null ? "itemIds = " + Arrays.toString(itemIds) + ", " : "") + "access = " + access;
	}
}

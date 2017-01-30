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

import tera.Config;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class SellableItem implements Foldable
{
	private static final FoldablePool<SellableItem> pool = Pools.newConcurrentFoldablePool(SellableItem.class);
	
	/**
	 * Method newInstance.
	 * @param item ItemInstance
	 * @param inventory Inventory
	 * @param count long
	 * @return SellableItem
	 */
	public static final SellableItem newInstance(ItemInstance item, Inventory inventory, long count)
	{
		SellableItem sell = pool.take();
		
		if (sell == null)
		{
			sell = new SellableItem(item, inventory, count);
		}
		else
		{
			sell.item = item;
			sell.inventory = inventory;
			sell.count = count;
		}
		
		return sell;
	}
	
	private ItemInstance item;
	private Inventory inventory;
	private long count;
	
	/**
	 * Constructor for SellableItem.
	 * @param item ItemInstance
	 * @param inventory Inventory
	 * @param count long
	 */
	private SellableItem(ItemInstance item, Inventory inventory, long count)
	{
		this.item = item;
		this.inventory = inventory;
		this.count = count;
	}
	
	/**
	 * Method addCount.
	 * @param count long
	 */
	public void addCount(long count)
	{
		this.count += count;
	}
	
	/**
	 * Method check.
	 * @return boolean
	 */
	public boolean check()
	{
		return (inventory != null) && (inventory.getCellForObjectId(item.getObjectId()) != null);
	}
	
	public void deleteItem()
	{
		inventory.lock();
		
		try
		{
			final Cell cell = inventory.getCellForObjectId(item.getObjectId());
			inventory.removeItemFromIndex(count, cell.getIndex());
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	/**
	 * Method equals.
	 * @param object Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object object)
	{
		if ((this == object) || (item == object))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		inventory = null;
		item = null;
		count = 0;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getCount.
	 * @return long
	 */
	public long getCount()
	{
		return count;
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
	 * Method getItemId.
	 * @return int
	 */
	public int getItemId()
	{
		return item.getItemId();
	}
	
	/**
	 * Method getObjectId.
	 * @return int
	 */
	public int getObjectId()
	{
		return item.getObjectId();
	}
	
	/**
	 * Method getSellPrice.
	 * @return long
	 */
	public long getSellPrice()
	{
		return (long) (count * item.getSellPrice() * Config.WORLD_SHOP_PRICE_MOD);
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	/**
	 * Method setCount.
	 * @param count int
	 */
	public void setCount(int count)
	{
		this.count = count;
	}
	
	/**
	 * Method setItem.
	 * @param item ItemInstance
	 */
	public void setItem(ItemInstance item)
	{
		this.item = item;
	}
	
	/**
	 * Method subCount.
	 * @param count int
	 */
	public void subCount(int count)
	{
		this.count -= count;
	}
}
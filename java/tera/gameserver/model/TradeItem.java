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

import tera.gameserver.model.items.ItemInstance;

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class TradeItem implements Foldable
{
	private static final FoldablePool<TradeItem> pool = Pools.newConcurrentFoldablePool(TradeItem.class);
	
	/**
	 * Method newInstance.
	 * @param item ItemInstance
	 * @param count long
	 * @return TradeItem
	 */
	public static final TradeItem newInstance(ItemInstance item, long count)
	{
		TradeItem tradeItem = pool.take();
		
		if (tradeItem == null)
		{
			tradeItem = new TradeItem();
		}
		
		tradeItem.item = item;
		tradeItem.count = count;
		return tradeItem;
	}
	
	private ItemInstance item;
	private long count;
	
	private TradeItem()
	{
		super();
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
	 * Method equals.
	 * @param object Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object object)
	{
		return item == object;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
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
	 * Method isStackable.
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return item.isStackable();
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
	 * Method subCount.
	 * @param count long
	 */
	public void subCount(long count)
	{
		this.count -= count;
	}
}
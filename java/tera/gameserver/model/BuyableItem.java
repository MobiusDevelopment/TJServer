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
import tera.gameserver.templates.ItemTemplate;

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class BuyableItem implements Foldable
{
	private static final FoldablePool<BuyableItem> pool = Pools.newConcurrentFoldablePool(BuyableItem.class);
	
	/**
	 * Method newInstance.
	 * @param item ItemTemplate
	 * @param count long
	 * @return BuyableItem
	 */
	public static BuyableItem newInstance(ItemTemplate item, long count)
	{
		BuyableItem buy = pool.take();
		
		if (buy == null)
		{
			buy = new BuyableItem();
		}
		
		buy.item = item;
		buy.count = count;
		return buy;
	}
	
	private ItemTemplate item;
	private long count;
	
	/**
	 * Method addCount.
	 * @param count long
	 */
	public void addCount(long count)
	{
		this.count += count;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		item = null;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getBuyPrice.
	 * @return long
	 */
	public long getBuyPrice()
	{
		return (long) (count * item.getBuyPrice() * Config.WORLD_SHOP_PRICE_MOD);
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
	 * @return ItemTemplate
	 */
	public ItemTemplate getItem()
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
	 * @param item ItemTemplate
	 */
	public void setItem(ItemTemplate item)
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
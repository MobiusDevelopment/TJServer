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

import tera.Config;
import tera.gameserver.manager.RandomManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.templates.ItemTemplate;

import rlib.logging.Loggers;
import rlib.util.array.Arrays;
import rlib.util.random.Random;

/**
 * @author Ronn
 */
public final class DropGroup
{
	private final DropInfo[] items;
	private final int id;
	private final int chance;
	private final int count;
	private boolean money;
	
	/**
	 * Constructor for DropGroup.
	 * @param id int
	 * @param chance int
	 * @param count int
	 * @param items DropInfo[]
	 */
	public DropGroup(int id, int chance, int count, DropInfo[] items)
	{
		this.id = id;
		this.chance = chance;
		this.items = items;
		this.count = count;
		money = true;
		
		for (DropInfo item : items)
		{
			if (item.getItemId() != Inventory.MONEY_ITEM_ID)
			{
				money = false;
				break;
			}
		}
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
	 * Method getCount.
	 * @return int
	 */
	public final int getCount()
	{
		return count;
	}
	
	/**
	 * Method getId.
	 * @return int
	 */
	public final int getId()
	{
		return id;
	}
	
	/**
	 * Method getItem.
	 * @return ItemInstance
	 */
	public ItemInstance getItem()
	{
		final RandomManager randManager = RandomManager.getInstance();
		final Random rand = randManager.getDropRandom();
		
		if (!rand.chance(chance))
		{
			return null;
		}
		
		final DropInfo[] items = getItems();
		
		for (DropInfo data : items)
		{
			if (rand.nextInt(0, 100000) > data.getChance())
			{
				continue;
			}
			
			final int count = rand.nextInt(data.getMinCount(), data.getMaxCount());
			
			if (count < 1)
			{
				continue;
			}
			
			final ItemTemplate template = data.getItem();
			
			if (template == null)
			{
				continue;
			}
			
			if (Arrays.contains(Config.WORLD_DONATE_ITEMS, template.getItemId()))
			{
				Loggers.warning(this, new Exception("not create donate item for id " + template.getItemId()));
				continue;
			}
			
			final ItemInstance item = template.newInstance();
			
			if (item == null)
			{
				continue;
			}
			
			if (template.isStackable())
			{
				item.setItemCount(count);
			}
			
			return item;
		}
		
		return null;
	}
	
	/**
	 * Method getItems.
	 * @return DropInfo[]
	 */
	public final DropInfo[] getItems()
	{
		return items;
	}
	
	/**
	 * Method isMoney.
	 * @return boolean
	 */
	public final boolean isMoney()
	{
		return money;
	}
}
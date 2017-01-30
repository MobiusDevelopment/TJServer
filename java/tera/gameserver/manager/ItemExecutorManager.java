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
package tera.gameserver.manager;

import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.scripts.items.ItemExecutor;
import tera.gameserver.scripts.items.ItemExecutorType;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class ItemExecutorManager
{
	private static final Logger log = Loggers.getLogger(ItemExecutorManager.class);
	
	private static ItemExecutorManager instance;
	
	/**
	 * Method getInstance.
	 * @return ItemExecutorManager
	 */
	public static ItemExecutorManager getInstance()
	{
		if (instance == null)
		{
			instance = new ItemExecutorManager();
		}
		
		return instance;
	}
	
	private final Table<IntKey, ItemExecutor> executors;
	
	private ItemExecutorManager()
	{
		executors = Tables.newIntegerTable();
		final int counter = 0;
		
		for (ItemExecutorType use : ItemExecutorType.values())
		{
			register(use.newInstance());
		}
		
		log.info("loaded " + counter + " executor items.");
	}
	
	/**
	 * Method execute.
	 * @param item ItemInstance
	 * @param player Player
	 * @return boolean
	 */
	public final boolean execute(ItemInstance item, Player player)
	{
		if (item == null)
		{
			return false;
		}
		
		final ItemExecutor executor = executors.get(item.getItemId());
		
		if (executor == null)
		{
			return false;
		}
		
		executor.execution(item, player);
		return true;
	}
	
	/**
	 * Method register.
	 * @param executor ItemExecutor
	 */
	public final void register(ItemExecutor executor)
	{
		final int ids[] = executor.getItemIds();
		
		for (int id : ids)
		{
			if (executors.containsKey(id))
			{
				throw new IllegalArgumentException();
			}
			
			executors.put(id, executor);
		}
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	public final int size()
	{
		return executors.size();
	}
}

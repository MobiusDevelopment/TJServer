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

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.ItemLocation;

import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class PlayerInventory extends AbstractInventory
{
	private static final FoldablePool<Inventory> inventoryPool = Pools.newConcurrentFoldablePool(Inventory.class);
	private static final Array<Cell[]>[] cellPool = Arrays.create(Array.class, 200);
	static
	{
		for (int i = 0; i < cellPool.length; i++)
		{
			final Array<Cell[]> cells = Arrays.toConcurrentArray(Cell[].class);
			cellPool[i] = cells;
		}
	}
	
	/**
	 * Method newInstance.
	 * @param owner Character
	 * @return Inventory
	 */
	public static Inventory newInstance(Character owner)
	{
		final Inventory inventory = newInstance(owner, 1);
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.createInventory(owner, inventory);
		return inventory;
	}
	
	/**
	 * Method newInstance.
	 * @param owner Character
	 * @param level int
	 * @return Inventory
	 */
	public static final Inventory newInstance(Character owner, int level)
	{
		AbstractInventory inventory = (AbstractInventory) inventoryPool.take();
		
		if (inventory == null)
		{
			inventory = new PlayerInventory(owner, level);
		}
		else
		{
			inventory.level = level;
			inventory.owner = owner;
			final int size = inventory.getAllMaxCells();
			Cell[] cells = cellPool[size].pop();
			
			if (cells == null)
			{
				cells = new Cell[size];
				
				for (int i = 0; i < size; i++)
				{
					cells[i] = new Cell(i, ItemLocation.INVENTORY);
				}
			}
			
			inventory.cells = cells;
		}
		
		return inventory;
	}
	
	/**
	 * Constructor for PlayerInventory.
	 * @param owner Character
	 * @param level int
	 */
	public PlayerInventory(Character owner, int level)
	{
		super(level);
		this.owner = owner;
	}
	
	/**
	 * Method addLevel.
	 * @see tera.gameserver.model.inventory.Inventory#addLevel()
	 */
	@Override
	public void addLevel()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		lock();
		
		try
		{
			final Cell[] old = cells;
			super.addLevel();
			cells = new Cell[getAllMaxCells()];
			
			for (int i = 0, length = cells.length; i < length; i++)
			{
				cells[i] = new Cell(i, ItemLocation.INVENTORY);
			}
			
			for (int i = 0, length = old.length; i < length; i++)
			{
				cells[i].setItem(old[i].getItem());
			}
			
			for (Cell element : old)
			{
				element.setItem(null);
			}
			
			cellPool[old.length].add(old);
			dbManager.updateInventory(owner, this);
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		final int size = cells.length;
		
		for (int i = 0; i < size; i++)
		{
			final Cell cell = cells[i];
			final ItemInstance item = cell.getItem();
			
			if (item == null)
			{
				continue;
			}
			
			item.deleteMe();
			cell.setItem(null);
		}
		
		cellPool[size].add(cells);
		cells = null;
		
		if (!gold.isEmpty())
		{
			final ItemInstance item = gold.getItem();
			item.deleteMe();
			gold.setItem(null);
		}
		
		owner = null;
	}
	
	/**
	 * Method fold.
	 * @see tera.gameserver.model.inventory.Inventory#fold()
	 */
	@Override
	public void fold()
	{
		inventoryPool.put(this);
	}
	
	/**
	 * Method getAllMaxCells.
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getAllMaxCells()
	 */
	@Override
	public int getAllMaxCells()
	{
		return getMaxCells() + 8;
	}
	
	/**
	 * Method getBaseLevel.
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getBaseLevel()
	 */
	@Override
	public int getBaseLevel()
	{
		return 1;
	}
	
	/**
	 * Method getLevelBonus.
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getLevelBonus()
	 */
	@Override
	public int getLevelBonus()
	{
		return 8;
	}
	
	/**
	 * Method getMaxCells.
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getMaxCells()
	 */
	@Override
	public int getMaxCells()
	{
		return 48 + (level * getLevelBonus());
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
	 * Method subLevel.
	 * @see tera.gameserver.model.inventory.Inventory#subLevel()
	 */
	@Override
	public void subLevel()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		lock();
		
		try
		{
			final Cell[] old = cells;
			super.subLevel();
			cells = new Cell[getAllMaxCells()];
			
			for (int i = 0, length = cells.length; i < length; i++)
			{
				cells[i] = new Cell(i, ItemLocation.INVENTORY);
			}
			
			for (int i = 0, length = cells.length; i < length; i++)
			{
				cells[i].setItem(old[i].getItem());
			}
			
			for (Cell element : old)
			{
				final ItemInstance item = element.getItem();
				
				if (item != null)
				{
					item.deleteMe();
				}
				
				element.setItem(null);
			}
			
			cellPool[old.length].add(old);
			dbManager.updateInventory(owner, this);
		}
		
		finally
		{
			unlock();
		}
	}
}
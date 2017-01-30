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

import java.util.concurrent.locks.Lock;

import tera.Config;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.ItemLocation;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.util.Identified;
import tera.util.LocalObjects;

import rlib.concurrent.Locks;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Nameable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBank<T extends Nameable & Identified> implements Bank
{
	protected static final Logger log = Loggers.getLogger(Bank.class);
	protected final Lock lock;
	protected T owner;
	protected Cell[] cells;
	protected Cell gold;
	
	public AbstractBank()
	{
		this.lock = Locks.newLock();
		this.cells = new Cell[getMaxSize()];
		final ItemLocation location = getLocation();
		
		for (int i = 0, length = cells.length; i < length; i++)
		{
			cells[i] = new Cell(i, location);
		}
		
		this.gold = new Cell(-1, location);
	}
	
	/**
	 * Method addItem.
	 * @param id int
	 * @param count int
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Bank#addItem(int, int)
	 */
	@Override
	public boolean addItem(int id, int count)
	{
		if (count < 1)
		{
			return false;
		}
		
		if (Arrays.contains(Config.WORLD_DONATE_ITEMS, id))
		{
			log.warning(this, new Exception("not create donate item for id " + id));
			return false;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		lock();
		
		try
		{
			if (id == Inventory.MONEY_ITEM_ID)
			{
				addMoney(count);
				return true;
			}
			
			final Cell[] cells = getCells();
			Cell empty = null;
			ItemInstance sametype = null;
			
			for (Cell cell : cells)
			{
				if (cell.isEmpty())
				{
					if (empty == null)
					{
						empty = cell;
					}
					
					continue;
				}
				
				final ItemInstance old = cell.getItem();
				
				if (old.isStackable() && (old.getItemId() == id))
				{
					sametype = old;
					break;
				}
			}
			
			if (sametype != null)
			{
				sametype.setAutor(owner.getName());
				sametype.addItemCount(count);
				dbManager.updateDataItem(sametype);
				return true;
			}
			
			final ItemTable itemTable = ItemTable.getInstance();
			final ItemTemplate template = itemTable.getItem(id);
			
			if ((template == null) || (empty == null))
			{
				return false;
			}
			
			final ItemInstance item = template.newInstance();
			
			if (item == null)
			{
				return false;
			}
			
			if (template.isStackable())
			{
				item.setItemCount(count);
			}
			
			item.setOwnerId(getOwnerId());
			item.setAutor(owner.getName());
			empty.setItem(item);
			dbManager.updateItem(item);
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method addMoney.
	 * @param count long
	 * @see tera.gameserver.model.inventory.Bank#addMoney(long)
	 */
	@Override
	public void addMoney(long count)
	{
		if (count < 1)
		{
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		lock();
		
		try
		{
			ItemInstance old = gold.getItem();
			
			if (old != null)
			{
				old.addItemCount(count);
			}
			else
			{
				final ItemTable itemTable = ItemTable.getInstance();
				old = itemTable.getItem(Inventory.MONEY_ITEM_ID).newInstance();
				
				if (old == null)
				{
					log.warning(this, new Exception("not created money item."));
					return;
				}
				
				old.setItemCount(count);
				old.setOwnerId(getOwnerId());
				gold.setItem(old);
			}
			
			dbManager.updateItem(old);
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
		final Cell[] cells = getCells();
		
		for (Cell cell : cells)
		{
			final ItemInstance item = cell.getItem();
			
			if (item != null)
			{
				item.deleteMe();
			}
			
			cell.setItem(null);
		}
		
		final ItemInstance item = gold.getItem();
		
		if (item != null)
		{
			item.deleteMe();
		}
		
		gold.setItem(null);
		setOwner(null);
	}
	
	/**
	 * Method getCell.
	 * @param index int
	 * @return Cell
	 * @see tera.gameserver.model.inventory.Bank#getCell(int)
	 */
	@Override
	public Cell getCell(int index)
	{
		if ((index < 0) || (index >= cells.length))
		{
			return null;
		}
		
		return cells[index];
	}
	
	/**
	 * Method getCells.
	 * @return Cell[]
	 * @see tera.gameserver.model.inventory.Bank#getCells()
	 */
	@Override
	public Cell[] getCells()
	{
		return cells;
	}
	
	/**
	 * Method getItemForObjectId.
	 * @param objectId int
	 * @return ItemInstance
	 * @see tera.gameserver.model.inventory.Bank#getItemForObjectId(int)
	 */
	@Override
	public ItemInstance getItemForObjectId(int objectId)
	{
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			
			for (Cell cell : cells)
			{
				if (cell.isEmpty())
				{
					continue;
				}
				
				final ItemInstance item = cell.getItem();
				
				if (item.getObjectId() == objectId)
				{
					return item;
				}
			}
			
			return null;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method getLastIndex.
	 * @return int
	 * @see tera.gameserver.model.inventory.Bank#getLastIndex()
	 */
	@Override
	public int getLastIndex()
	{
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			int last = cells.length - 1;
			
			for (int i = last; i >= 0; i--)
			{
				if (!cells[i].isEmpty())
				{
					break;
				}
				
				last--;
			}
			
			return last;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method getMoney.
	 * @return long
	 * @see tera.gameserver.model.inventory.Bank#getMoney()
	 */
	@Override
	public long getMoney()
	{
		return gold.getItemCount();
	}
	
	/**
	 * Method getOwner.
	 * @return T
	 * @see tera.gameserver.model.inventory.Bank#getOwner()
	 */
	@Override
	public T getOwner()
	{
		return owner;
	}
	
	/**
	 * Method getOwnerId.
	 * @return int
	 */
	protected int getOwnerId()
	{
		return 0;
	}
	
	/**
	 * Method getTabSize.
	 * @return int
	 * @see tera.gameserver.model.inventory.Bank#getTabSize()
	 */
	@Override
	public int getTabSize()
	{
		return 72;
	}
	
	/**
	 * Method getUsedCount.
	 * @return int
	 * @see tera.gameserver.model.inventory.Bank#getUsedCount()
	 */
	@Override
	public int getUsedCount()
	{
		lock();
		
		try
		{
			int counter = 0;
			final Cell[] cells = getCells();
			
			for (int i = 0, length = cells.length; i < length; i++)
			{
				if (!cells[i].isEmpty())
				{
					counter++;
				}
			}
			
			return counter;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method lock.
	 * @see rlib.util.Synchronized#lock()
	 */
	@Override
	public void lock()
	{
		lock.lock();
	}
	
	/**
	 * Method putItem.
	 * @param item ItemInstance
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Bank#putItem(ItemInstance)
	 */
	@Override
	public boolean putItem(ItemInstance item)
	{
		if (item == null)
		{
			return false;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			Cell empty = null;
			
			if (item.isStackable())
			{
				for (Cell cell : cells)
				{
					if (cell.isEmpty())
					{
						if (empty == null)
						{
							empty = cell;
						}
						
						continue;
					}
					
					final ItemInstance old = cell.getItem();
					
					if (old.getItemId() == item.getItemId())
					{
						old.addItemCount(item.getItemCount());
						dbManager.updateDataItem(old);
						return true;
					}
				}
			}
			else
			{
				for (Cell cell : cells)
				{
					if (cell.isEmpty())
					{
						empty = cell;
						break;
					}
				}
			}
			
			if (empty != null)
			{
				item.setOwnerId(getOwnerId());
				empty.setItem(item);
				dbManager.updateLocationItem(item);
				return true;
			}
			
			return false;
		}
		
		finally
		{
			unlock();
		}
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
	 * Method removeItem.
	 * @param itemId int
	 * @param itemCount int
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Bank#removeItem(int, int)
	 */
	@Override
	public boolean removeItem(int itemId, int itemCount)
	{
		if (itemCount < 1)
		{
			return false;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			
			for (Cell cell : cells)
			{
				if (cell.isEmpty())
				{
					continue;
				}
				
				final ItemInstance item = cell.getItem();
				
				if (item.getItemId() == itemId)
				{
					if (item.getItemCount() > itemCount)
					{
						item.subItemCount(itemCount);
						dbManager.updateDataItem(item);
						return true;
					}
					else if (item.getItemCount() == itemCount)
					{
						cell.setItem(null);
						item.setOwnerId(0);
						item.setItemCount(0);
						dbManager.updateItem(item);
						item.deleteMe();
						return true;
					}
					
					return false;
				}
			}
			
			return false;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method removeItem.
	 * @param item ItemInstance
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Bank#removeItem(ItemInstance)
	 */
	@Override
	public boolean removeItem(ItemInstance item)
	{
		if (item == null)
		{
			return false;
		}
		
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			
			for (Cell cell : cells)
			{
				if (cell.isEmpty())
				{
					continue;
				}
				
				final ItemInstance old = cell.getItem();
				
				if (old == item)
				{
					cell.setItem(null);
					return true;
				}
			}
			
			return false;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method setItem.
	 * @param index int
	 * @param item ItemInstance
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Bank#setItem(int, ItemInstance)
	 */
	@Override
	public boolean setItem(int index, ItemInstance item)
	{
		if ((index < -1) || (index >= cells.length))
		{
			return false;
		}
		
		lock();
		
		try
		{
			Cell cell = null;
			
			if (index == -1)
			{
				cell = gold;
			}
			else
			{
				cell = cells[index];
			}
			
			if (!cell.isEmpty())
			{
				return false;
			}
			
			cell.setItem(item);
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method setOwner.
	 * @param owner Object
	 * @see tera.gameserver.model.inventory.Bank#setOwner(Object)
	 */
	@Override
	public void setOwner(Object owner)
	{
		this.owner = (T) owner;
	}
	
	/**
	 * Method sort.
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Bank#sort()
	 */
	@Override
	public boolean sort()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			final int last = getLastIndex();
			
			if (last < 1)
			{
				return false;
			}
			
			boolean sorted = true;
			
			for (int i = last - 1; i >= 0; i--)
			{
				if (cells[i].isEmpty())
				{
					sorted = false;
					break;
				}
			}
			
			if (sorted)
			{
				return false;
			}
			
			final LocalObjects local = LocalObjects.get();
			final Array<ItemInstance> items = local.getNextItemList();
			
			for (Cell cell : cells)
			{
				if (cell.isEmpty())
				{
					continue;
				}
				
				items.add(cell.getItem());
				cell.setItem(null);
			}
			
			final ItemInstance[] array = items.array();
			
			for (int i = 0, g = 0, length = items.size(); i < length; i++)
			{
				final ItemInstance item = array[i];
				cells[g++].setItem(item);
				dbManager.updateLocationItem(item);
			}
			
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method subMoney.
	 * @param count long
	 * @see tera.gameserver.model.inventory.Bank#subMoney(long)
	 */
	@Override
	public void subMoney(long count)
	{
		if (count < 1)
		{
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		lock();
		
		try
		{
			final ItemInstance item = gold.getItem();
			
			if (item == null)
			{
				return;
			}
			
			item.subItemCount(Math.min(count, item.getItemCount()));
			dbManager.updateDataItem(gold.getItem());
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method unlock.
	 * @see rlib.util.Synchronized#unlock()
	 */
	@Override
	public void unlock()
	{
		lock.unlock();
	}
}
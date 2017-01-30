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
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.ItemLocation;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.util.LocalObjects;

import rlib.concurrent.Locks;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class AbstractInventory implements Inventory
{
	protected static final Logger log = Loggers.getLogger(Inventory.class);
	
	protected final Lock lock;
	
	protected Character owner;
	
	protected Cell[] cells;
	
	protected Cell gold;
	
	protected int level;
	
	/**
	 * Constructor for AbstractInventory.
	 * @param level int
	 */
	public AbstractInventory(int level)
	{
		this.level = level;
		lock = Locks.newLock();
		cells = new Cell[getAllMaxCells()];
		
		for (int i = 0; i < cells.length; i++)
		{
			cells[i] = new Cell(i, ItemLocation.INVENTORY);
		}
		
		gold = new Cell(-1, ItemLocation.INVENTORY);
	}
	
	/**
	 * Method addItem.
	 * @param itemId int
	 * @param count long
	 * @param autor String
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#addItem(int, long, String)
	 */
	@Override
	public boolean addItem(int itemId, long count, String autor)
	{
		return addItem(itemId, count, autor, getMaxCells());
	}
	
	/**
	 * Method addItem.
	 * @param itemId int
	 * @param count long
	 * @param autor String
	 * @param max int
	 * @return boolean
	 */
	private boolean addItem(int itemId, long count, String autor, int max)
	{
		if (count < 1)
		{
			return false;
		}
		
		if (!"wait_items".equals(autor) && Arrays.contains(Config.WORLD_DONATE_ITEMS, itemId))
		{
			log.warning(this, new Exception("not create donate item for id " + itemId));
			return false;
		}
		
		final Character owner = getOwner();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		lock();
		
		try
		{
			if (itemId == Inventory.MONEY_ITEM_ID)
			{
				addMoney(count);
				return true;
			}
			
			final Cell[] cells = getCells();
			Cell empty = null;
			ItemInstance sametype = null;
			
			for (int i = 0; i < max; i++)
			{
				final Cell cell = cells[i];
				
				if (cell.isEmpty())
				{
					if (empty == null)
					{
						empty = cell;
					}
					
					continue;
				}
				
				final ItemInstance old = cell.getItem();
				
				if (old.isStackable() && (old.getItemId() == itemId))
				{
					sametype = old;
					break;
				}
			}
			
			if (sametype != null)
			{
				sametype.setAutor(autor);
				sametype.addItemCount(count);
				eventManager.notifyInventoryAddItem(owner, sametype);
				dbManager.updateDataItem(sametype);
				return true;
			}
			
			final ItemTable itemTable = ItemTable.getInstance();
			final ItemTemplate template = itemTable.getItem(itemId);
			
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
			
			item.setOwnerId(owner.getObjectId());
			item.setAutor(autor);
			empty.setItem(item);
			eventManager.notifyInventoryAddItem(owner, item);
			dbManager.updateItem(item);
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method addLevel.
	 * @see tera.gameserver.model.inventory.Inventory#addLevel()
	 */
	@Override
	public synchronized void addLevel()
	{
		level++;
	}
	
	/**
	 * Method addMoney.
	 * @param count long
	 * @see tera.gameserver.model.inventory.Inventory#addMoney(long)
	 */
	@Override
	public void addMoney(long count)
	{
		if (count < 1)
		{
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		final ItemTable itemTable = ItemTable.getInstance();
		final Character owner = getOwner();
		lock();
		
		try
		{
			final ItemInstance old = gold.getItem();
			
			if (old != null)
			{
				old.addItemCount(count);
			}
			else
			{
				final ItemInstance item = itemTable.getItem(MONEY_ITEM_ID).newInstance();
				
				if (item == null)
				{
					log.warning(this, new Exception("not created money item"));
					return;
				}
				
				item.setItemCount(count);
				item.setOwnerId(owner.getObjectId());
				gold.setItem(item);
			}
			
			final ItemInstance item = gold.getItem();
			eventManager.notifyInventoryAddItem(owner, item);
			dbManager.updateItem(item);
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method containsItems.
	 * @param itemId int
	 * @param itemCount int
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#containsItems(int, int)
	 */
	@Override
	public boolean containsItems(int itemId, int itemCount)
	{
		lock();
		
		try
		{
			if (itemId == Inventory.MONEY_ITEM_ID)
			{
				final ItemInstance item = gold.getItem();
				
				if (item == null)
				{
					return false;
				}
				
				return item.getItemCount() >= itemCount;
			}
			
			int counter = 0;
			final Cell[] cells = getCells();
			
			for (Cell cell : cells)
			{
				final ItemInstance item = cell.getItem();
				
				if (item == null)
				{
					continue;
				}
				
				if (item.getItemId() == itemId)
				{
					counter += item.getItemCount();
				}
				
				if (counter >= itemCount)
				{
					return true;
				}
			}
			
			return counter >= itemCount;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method forceAddItem.
	 * @param itemId int
	 * @param count long
	 * @param autor String
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#forceAddItem(int, long, String)
	 */
	@Override
	public boolean forceAddItem(int itemId, long count, String autor)
	{
		return addItem(itemId, count, autor, getAllMaxCells());
	}
	
	/**
	 * Method getCell.
	 * @param index int
	 * @return Cell
	 * @see tera.gameserver.model.inventory.Inventory#getCell(int)
	 */
	@Override
	public Cell getCell(int index)
	{
		if ((index < 0) || (index >= getMaxCells()))
		{
			return null;
		}
		
		return cells[index];
	}
	
	/**
	 * Method getCellForObjectId.
	 * @param objectId int
	 * @return Cell
	 * @see tera.gameserver.model.inventory.Inventory#getCellForObjectId(int)
	 */
	@Override
	public Cell getCellForObjectId(int objectId)
	{
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			
			for (int i = 0, length = getMaxCells(); i < length; i++)
			{
				final Cell cell = cells[i];
				
				if (cell.isEmpty())
				{
					continue;
				}
				
				final ItemInstance item = cell.getItem();
				
				if (item.getObjectId() == objectId)
				{
					return cell;
				}
			}
			
			final ItemInstance item = gold.getItem();
			
			if ((item != null) && (item.getObjectId() == objectId))
			{
				return gold;
			}
			
			return null;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method getCells.
	 * @return Cell[]
	 * @see tera.gameserver.model.inventory.Inventory#getCells()
	 */
	@Override
	public Cell[] getCells()
	{
		return cells;
	}
	
	/**
	 * Method getEngagedCells.
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getEngagedCells()
	 */
	@Override
	public int getEngagedCells()
	{
		lock();
		
		try
		{
			int counter = 0;
			final Cell[] cells = getCells();
			
			for (int i = 0, length = getMaxCells(); i < length; i++)
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
	 * Method getFreeCells.
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getFreeCells()
	 */
	@Override
	public int getFreeCells()
	{
		lock();
		
		try
		{
			int counter = 0;
			final Cell[] cells = getCells();
			
			for (int i = 0, length = getMaxCells(); i < length; i++)
			{
				if (cells[i].isEmpty())
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
	 * Method getGold.
	 * @return Cell
	 * @see tera.gameserver.model.inventory.Inventory#getGold()
	 */
	@Override
	public Cell getGold()
	{
		return gold;
	}
	
	/**
	 * Method getItemCount.
	 * @param itemId int
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getItemCount(int)
	 */
	@Override
	public int getItemCount(int itemId)
	{
		lock();
		
		try
		{
			int counter = 0;
			final Cell[] cells = getCells();
			
			for (Cell cell : cells)
			{
				final ItemInstance item = cell.getItem();
				
				if ((item == null) || (item.getItemId() != itemId))
				{
					continue;
				}
				
				counter += item.getItemCount();
			}
			
			return counter;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method getItemForItemId.
	 * @param itemId int
	 * @return ItemInstance
	 * @see tera.gameserver.model.inventory.Inventory#getItemForItemId(int)
	 */
	@Override
	public ItemInstance getItemForItemId(int itemId)
	{
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			
			for (int i = 0, length = getMaxCells(); i < length; i++)
			{
				final ItemInstance item = cells[i].getItem();
				
				if ((item == null) || (item.getItemId() != itemId))
				{
					continue;
				}
				
				return item;
			}
			
			return null;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method getItemForObjectId.
	 * @param objectId int
	 * @return ItemInstance
	 * @see tera.gameserver.model.inventory.Inventory#getItemForObjectId(int)
	 */
	@Override
	public ItemInstance getItemForObjectId(int objectId)
	{
		lock();
		
		try
		{
			final Cell cell = getCellForObjectId(objectId);
			return cell == null ? null : cell.getItem();
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method getLastIndex.
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getLastIndex()
	 */
	@Override
	public int getLastIndex()
	{
		lock();
		
		try
		{
			int last = getMaxCells() - 1;
			final Cell[] cells = getCells();
			
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
	 * Method getLevel.
	 * @return int
	 * @see tera.gameserver.model.inventory.Inventory#getLevel()
	 */
	@Override
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Method getMoney.
	 * @return long
	 * @see tera.gameserver.model.inventory.Inventory#getMoney()
	 */
	@Override
	public long getMoney()
	{
		return gold.getItemCount();
	}
	
	/**
	 * Method getOwner.
	 * @return Character
	 * @see tera.gameserver.model.inventory.Inventory#getOwner()
	 */
	@Override
	public Character getOwner()
	{
		return owner;
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
	 * Method moveItem.
	 * @param item ItemInstance
	 * @param source Inventory
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#moveItem(ItemInstance, Inventory)
	 */
	@Override
	public boolean moveItem(ItemInstance item, Inventory source)
	{
		if (item == null)
		{
			return false;
		}
		
		final Character owner = getOwner();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final Cell[] cells = getCells();
		final int max = getMaxCells();
		Cell empty = null;
		
		if (item.isStackable())
		{
			for (int i = 0; i < max; i++)
			{
				final Cell cell = cells[i];
				
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
					source.removeItem(item);
					item.setOwnerId(0);
					dbManager.updateLocationItem(item);
					old.addItemCount(item.getItemCount());
					dbManager.updateDataItem(old);
					item.deleteMe();
					return true;
				}
			}
		}
		else
		{
			for (int i = 0; i < max; i++)
			{
				final Cell cell = cells[i];
				
				if (cell.isEmpty())
				{
					empty = cell;
					break;
				}
			}
		}
		
		if (empty != null)
		{
			source.removeItem(item);
			item.setOwnerId(owner.getObjectId());
			empty.setItem(item);
			dbManager.updateLocationItem(item);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method putItem.
	 * @param item ItemInstance
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#putItem(ItemInstance)
	 */
	@Override
	public boolean putItem(ItemInstance item)
	{
		if (item == null)
		{
			return false;
		}
		
		final Character owner = getOwner();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		lock();
		
		try
		{
			if (item.getItemId() == MONEY_ITEM_ID)
			{
				final ItemInstance old = gold.getItem();
				
				if (old == null)
				{
					item.setOwnerId(owner.getObjectId());
					gold.setItem(item);
					dbManager.updateLocationItem(item);
				}
				else
				{
					old.addItemCount(item.getItemCount());
					dbManager.updateDataItem(old);
				}
				
				eventManager.notifyInventoryAddItem(owner, item);
				return true;
			}
			
			final Cell[] cells = getCells();
			final int max = getMaxCells();
			Cell empty = null;
			
			if (item.isStackable())
			{
				for (int i = 0; i < max; i++)
				{
					final Cell cell = cells[i];
					
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
						eventManager.notifyInventoryAddItem(owner, item);
						dbManager.updateDataItem(old);
						return true;
					}
				}
			}
			else
			{
				for (int i = 0; i < max; i++)
				{
					final Cell cell = cells[i];
					
					if (cell.isEmpty())
					{
						empty = cell;
						break;
					}
				}
			}
			
			if (empty != null)
			{
				item.setOwnerId(owner.getObjectId());
				empty.setItem(item);
				eventManager.notifyInventoryAddItem(owner, item);
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
	 * Method removeItem.
	 * @param itemId int
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#removeItem(int)
	 */
	@Override
	public boolean removeItem(int itemId)
	{
		final Character owner = getOwner();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		lock();
		
		try
		{
			int counter = 0;
			final Cell[] cells = getCells();
			
			for (Cell cell : cells)
			{
				final ItemInstance item = cell.getItem();
				
				if ((item == null) || (item.getItemId() != itemId))
				{
					continue;
				}
				
				cell.setItem(null);
				item.setOwnerId(0);
				eventManager.notifyInventoryRemoveItem(owner, item);
				dbManager.updateLocationItem(item);
				item.deleteMe();
				counter++;
			}
			
			return counter > 0;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method removeItem.
	 * @param itemId int
	 * @param count long
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#removeItem(int, long)
	 */
	@Override
	public boolean removeItem(int itemId, long count)
	{
		final Character owner = getOwner();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		lock();
		
		try
		{
			final Cell[] cells = getCells();
			
			for (Cell cell : cells)
			{
				final ItemInstance item = cell.getItem();
				
				if ((item == null) || (item.getItemId() != itemId))
				{
					continue;
				}
				
				if (item.getItemCount() > count)
				{
					item.subItemCount(count);
					eventManager.notifyInventoryRemoveItem(owner, item);
					dbManager.updateDataItem(item);
					return true;
				}
				else if (item.getItemCount() == count)
				{
					cell.setItem(null);
					item.setOwnerId(0);
					eventManager.notifyInventoryRemoveItem(owner, item);
					dbManager.updateLocationItem(item);
					item.deleteMe();
					return true;
				}
				else
				{
					cell.setItem(null);
					item.setOwnerId(0);
					eventManager.notifyInventoryRemoveItem(owner, item);
					dbManager.updateLocationItem(item);
					item.deleteMe();
					count -= item.getItemCount();
				}
			}
			
			return count < 1;
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
	 * @see tera.gameserver.model.inventory.Inventory#removeItem(ItemInstance)
	 */
	@Override
	public boolean removeItem(ItemInstance item)
	{
		final Character owner = getOwner();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
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
					eventManager.notifyInventoryRemoveItem(owner, item);
					return true;
				}
			}
			
			final ItemInstance old = gold.getItem();
			
			if (!gold.isEmpty() && (old == item))
			{
				gold.setItem(null);
				eventManager.notifyInventoryRemoveItem(owner, item);
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
	 * Method removeItemFromIndex.
	 * @param count long
	 * @param index int
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#removeItemFromIndex(long, int)
	 */
	@Override
	public boolean removeItemFromIndex(long count, int index)
	{
		if ((index < 0) || (index >= cells.length))
		{
			return false;
		}
		
		final Character owner = getOwner();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		lock();
		
		try
		{
			final Cell cell = cells[index];
			final ItemInstance item = cell.getItem();
			
			if (item == null)
			{
				return false;
			}
			
			if (item.getItemCount() > count)
			{
				item.subItemCount(count);
				eventManager.notifyInventoryRemoveItem(owner, item);
				dbManager.updateDataItem(item);
				return true;
			}
			cell.setItem(null);
			item.setOwnerId(0);
			eventManager.notifyInventoryRemoveItem(owner, item);
			dbManager.updateLocationItem(item);
			item.deleteMe();
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method setCells.
	 * @param cells Cell[]
	 * @see tera.gameserver.model.inventory.Inventory#setCells(Cell[])
	 */
	@Override
	public void setCells(Cell[] cells)
	{
		this.cells = cells;
	}
	
	/**
	 * Method setGold.
	 * @param gold Cell
	 * @see tera.gameserver.model.inventory.Inventory#setGold(Cell)
	 */
	@Override
	public void setGold(Cell gold)
	{
		this.gold = gold;
	}
	
	/**
	 * Method setItem.
	 * @param item ItemInstance
	 * @param index int
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#setItem(ItemInstance, int)
	 */
	@Override
	public boolean setItem(ItemInstance item, int index)
	{
		if (item == null)
		{
			return false;
		}
		
		lock();
		
		try
		{
			if (index == -1)
			{
				gold.setItem(item);
				return true;
			}
			
			if ((index < 0) || (index >= cells.length))
			{
				return false;
			}
			
			cells[index].setItem(item);
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method setOwner.
	 * @param owner Character
	 * @see tera.gameserver.model.inventory.Inventory#setOwner(Character)
	 */
	@Override
	public void setOwner(Character owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Method sort.
	 * @return boolean
	 * @see tera.gameserver.model.inventory.Inventory#sort()
	 */
	@Override
	public boolean sort()
	{
		lock();
		
		try
		{
			final int last = getLastIndex();
			
			if (last < 1)
			{
				return false;
			}
			
			final Cell[] cells = getCells();
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
			
			final DataBaseManager dbManager = DataBaseManager.getInstance();
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
	 * Method subLevel.
	 * @see tera.gameserver.model.inventory.Inventory#subLevel()
	 */
	@Override
	public void subLevel()
	{
		level--;
	}
	
	/**
	 * Method subMoney.
	 * @param count long
	 * @see tera.gameserver.model.inventory.Inventory#subMoney(long)
	 */
	@Override
	public void subMoney(long count)
	{
		if (count < 1)
		{
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		lock();
		
		try
		{
			final ItemInstance item = gold.getItem();
			
			if (item == null)
			{
				return;
			}
			
			item.subItemCount(Math.min(count, item.getItemCount()));
			eventManager.notifyInventoryRemoveItem(owner, item);
			dbManager.updateDataItem(gold.getItem());
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "owner = " + owner.getName() + ", cells = " + Arrays.toString(cells) + ", gold = " + gold + ", level = " + level;
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

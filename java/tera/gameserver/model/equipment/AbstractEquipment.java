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
package tera.gameserver.model.equipment;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.CrystalInstance;
import tera.gameserver.model.items.CrystalList;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.ItemLocation;

import rlib.concurrent.Locks;

/**
 * @author Ronn
 */
public abstract class AbstractEquipment implements Equipment
{
	
	protected static final Slot[] EMPTY_SLOTS = new Slot[0];
	
	protected final Lock lock;
	
	private Slot[] slots;
	
	protected Character owner;
	
	/**
	 * Constructor for AbstractEquipment.
	 * @param owner Character
	 */
	public AbstractEquipment(Character owner)
	{
		lock = Locks.newLock();
		this.owner = owner;
		prepare();
	}
	
	/**
	 * Method dressItem.
	 * @param inventory Inventory
	 * @param cell Cell
	 * @return boolean
	 * @see tera.gameserver.model.equipment.Equipment#dressItem(Inventory, Cell)
	 */
	@Override
	public boolean dressItem(Inventory inventory, Cell cell)
	{
		if (cell == null)
		{
			return false;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final Slot[] slots = getSlots();
		final Character owner = getOwner();
		inventory.lock();
		
		try
		{
			lock();
			
			try
			{
				if (cell.isEmpty())
				{
					return false;
				}
				
				ItemInstance item = cell.getItem();
				
				if (item.isCrystal())
				{
					final CrystalInstance crystal = item.getCrystal();
					ItemInstance target = null;
					CrystalList list = null;
					
					for (Slot slot : slots)
					{
						if (slot.isEmpty())
						{
							continue;
						}
						
						item = slot.getItem();
						list = item.getCrystals();
						
						if (list == null)
						{
							continue;
						}
						
						if (crystal.isNoStack() && list.containsCrystal(crystal.getStackType()))
						{
							return false;
						}
						
						if ((target == null) && item.checkCrystal(crystal))
						{
							target = item;
						}
					}
					
					if (target == null)
					{
						owner.sendMessage(MessageType.ALL_CRYSTAL_SLOTS_ARE_FULL);
					}
					else
					{
						list = target.getCrystals();
						list.put(crystal, cell, owner);
						eventManager.notifyInventoryChanged(owner);
						eventManager.notifyStatChanged(owner);
						return true;
					}
					
					return false;
				}
				
				if (item.getSlotType() == SlotType.NONE)
				{
					return false;
				}
				
				if (!equiped(item))
				{
					return false;
				}
				
				Slot empty = null;
				Slot emplayed = null;
				
				for (Slot slot2 : slots)
				{
					final Slot slot = slot2;
					
					if (slot.getType() == item.getSlotType())
					{
						if (!slot.isEmpty())
						{
							emplayed = slot;
						}
						else
						{
							empty = slot;
							break;
						}
					}
				}
				
				if (empty != null)
				{
					empty.setItem(item);
					cell.setItem(null);
					item.addFuncsTo(owner);
					dbManager.updateLocationItem(item);
					eventManager.notifyEquipmentChanged(owner);
					eventManager.notifyStatChanged(owner);
					return true;
				}
				else if (emplayed != null)
				{
					final ItemInstance old = emplayed.getItem();
					old.removeFuncsTo(owner);
					cell.setItem(old);
					emplayed.setItem(item);
					item.addFuncsTo(owner);
					dbManager.updateLocationItem(old);
					dbManager.updateLocationItem(item);
					eventManager.notifyEquipmentChanged(owner);
					eventManager.notifyStatChanged(owner);
					return true;
				}
				
				return false;
			}
			
			finally
			{
				unlock();
			}
		}
		
		finally
		{
			inventory.unlock();
		}
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		final Slot[] slots = getSlots();
		final Character owner = getOwner();
		
		for (Slot slot : slots)
		{
			if (slot.isEmpty())
			{
				continue;
			}
			
			final ItemInstance item = slot.getItem();
			item.removeFuncsTo(owner);
			item.setObjectId(-1);
			item.deleteMe();
			slot.setItem(null);
		}
		
		setOwner(null);
	}
	
	/**
	 * Method getCount.
	 * @param type SlotType
	 * @return int
	 * @see tera.gameserver.model.equipment.Equipment#getCount(SlotType)
	 */
	@Override
	public int getCount(SlotType type)
	{
		int counter = 0;
		final Slot[] slots = getSlots();
		lock();
		
		try
		{
			for (Slot slot2 : slots)
			{
				final Slot slot = slot2;
				
				if (!slot.isEmpty() && (slot.getType() == type))
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
	 * Method getEngagedSlots.
	 * @return int
	 * @see tera.gameserver.model.equipment.Equipment#getEngagedSlots()
	 */
	@Override
	public int getEngagedSlots()
	{
		int counter = 0;
		final Slot[] slots = getSlots();
		lock();
		
		try
		{
			for (int i = 0, length = slots.length; i < length; i++)
			{
				if (!slots[i].isEmpty())
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
	 * Method getItem.
	 * @param index int
	 * @return ItemInstance
	 * @see tera.gameserver.model.equipment.Equipment#getItem(int)
	 */
	@Override
	public ItemInstance getItem(int index)
	{
		final Slot[] slots = getSlots();
		
		if ((index < 0) || (index >= slots.length))
		{
			return null;
		}
		
		final Slot slot = slots[index];
		return slot == null ? null : slot.getItem();
	}
	
	/**
	 * Method getItem.
	 * @param type SlotType
	 * @return ItemInstance
	 * @see tera.gameserver.model.equipment.Equipment#getItem(SlotType)
	 */
	@Override
	public ItemInstance getItem(SlotType type)
	{
		final Slot[] slots = getSlots();
		lock();
		
		try
		{
			for (Slot slot2 : slots)
			{
				final Slot slot = slot2;
				
				if ((slot.getType() == type) && !slot.isEmpty())
				{
					return slot.getItem();
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
	 * Method getItemId.
	 * @param type SlotType
	 * @return int
	 * @see tera.gameserver.model.equipment.Equipment#getItemId(SlotType)
	 */
	@Override
	public int getItemId(SlotType type)
	{
		final ItemInstance item = getItem(type);
		return item == null ? 0 : item.getItemId();
	}
	
	/**
	 * Method getOwner.
	 * @return Character
	 * @see tera.gameserver.model.equipment.Equipment#getOwner()
	 */
	@Override
	public Character getOwner()
	{
		return owner;
	}
	
	/**
	 * Method getSlotForObjectId.
	 * @param objectId int
	 * @return Slot
	 * @see tera.gameserver.model.equipment.Equipment#getSlotForObjectId(int)
	 */
	@Override
	public Slot getSlotForObjectId(int objectId)
	{
		final Slot[] slots = getSlots();
		lock();
		
		try
		{
			for (Slot slot : slots)
			{
				if (slot.isEmpty())
				{
					continue;
				}
				
				final ItemInstance item = slot.getItem();
				
				if (item.getObjectId() == objectId)
				{
					return slot;
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
	 * Method getSlots.
	 * @return Slot[]
	 * @see tera.gameserver.model.equipment.Equipment#getSlots()
	 */
	@Override
	public Slot[] getSlots()
	{
		return slots;
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
	
	protected abstract void prepare();
	
	/**
	 * Method recreateSlots.
	 * @param slotTypes SlotType[]
	 * @see tera.gameserver.model.equipment.Equipment#recreateSlots(SlotType[])
	 */
	@Override
	public void recreateSlots(SlotType... slotTypes)
	{
		lock();
		
		try
		{
			slots = new Slot[slotTypes.length];
			
			for (int i = 0; i < slots.length; i++)
			{
				slots[i] = new Slot(slotTypes[i], i);
			}
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
	 * Method setItem.
	 * @param item ItemInstance
	 * @param index int
	 * @return boolean
	 * @see tera.gameserver.model.equipment.Equipment#setItem(ItemInstance, int)
	 */
	@Override
	public boolean setItem(ItemInstance item, int index)
	{
		if (item == null)
		{
			return false;
		}
		
		slots[index].setItem(item);
		item.addFuncsTo(owner);
		return true;
	}
	
	/**
	 * Method setOwner.
	 * @param owner Character
	 * @return Equipment
	 * @see tera.gameserver.model.equipment.Equipment#setOwner(Character)
	 */
	@Override
	public Equipment setOwner(Character owner)
	{
		this.owner = owner;
		return this;
	}
	
	/**
	 * Method setSlots.
	 * @param slots Slot[]
	 * @see tera.gameserver.model.equipment.Equipment#setSlots(Slot[])
	 */
	@Override
	public void setSlots(Slot[] slots)
	{
		this.slots = slots;
	}
	
	/**
	 * Method shootItem.
	 * @param inventory Inventory
	 * @param index int
	 * @param itemId int
	 * @return boolean
	 * @see tera.gameserver.model.equipment.Equipment#shootItem(Inventory, int, int)
	 */
	@Override
	public boolean shootItem(Inventory inventory, int index, int itemId)
	{
		final Slot[] slots = getSlots();
		
		if ((index < 0) || (index >= slots.length))
		{
			return false;
		}
		
		final Character owner = getOwner();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		inventory.lock();
		
		try
		{
			lock();
			
			try
			{
				final Slot slot = slots[index];
				final ItemInstance item = slot.getItem();
				
				if (item == null)
				{
					return false;
				}
				
				if (item.getItemId() != itemId)
				{
					final CrystalList crystals = item.getCrystals();
					
					if (crystals == null)
					{
						return false;
					}
					
					final CrystalInstance target = crystals.getCrystal(itemId);
					
					if ((target == null) || !unequiped(target))
					{
						return false;
					}
					
					if (!inventory.putItem(target))
					{
						owner.sendMessage(MessageType.INVENTORY_IS_FULL);
					}
					else
					{
						crystals.remove(target);
						target.removeFuncsTo(owner);
						
						if (target.getLocation() == ItemLocation.CRYSTAL)
						{
							target.setOwnerId(0);
							dbManager.updateLocationItem(target);
							target.deleteMe();
						}
					}
					
					eventManager.notifyEquipmentChanged(owner);
					eventManager.notifyStatChanged(owner);
				}
				else
				{
					if (!unequiped(item))
					{
						return false;
					}
					
					if (!inventory.putItem(item))
					{
						owner.sendMessage(MessageType.INVENTORY_IS_FULL);
					}
					else
					{
						slot.setItem(null);
						item.removeFuncsTo(owner);
						eventManager.notifyEquipmentChanged(owner);
						eventManager.notifyStatChanged(owner);
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
		
		finally
		{
			inventory.unlock();
		}
	}
	
	/**
	 * Method size.
	 * @return int
	 * @see tera.gameserver.model.equipment.Equipment#size()
	 */
	@Override
	public int size()
	{
		return slots.length;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "Equipment  " + (slots != null ? "slots = " + Arrays.toString(slots) + ", " : "");
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

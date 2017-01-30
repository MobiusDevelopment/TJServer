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
package tera.gameserver.model.items;

import tera.Config;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.inventory.Cell;

import rlib.util.Rnd;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class CrystalList
{
	
	private final Array<CrystalInstance> crystals;
	
	private int objectId;
	
	private final int max;
	
	/**
	 * Constructor for CrystalList.
	 * @param max int
	 * @param objectId int
	 */
	public CrystalList(int max, int objectId)
	{
		this.max = max;
		this.objectId = objectId;
		crystals = Arrays.toConcurrentArray(CrystalInstance.class, max);
	}
	
	/**
	 * Method addFuncs.
	 * @param owner Character
	 */
	public void addFuncs(Character owner)
	{
		final Array<CrystalInstance> crystals = getCrystals();
		crystals.readLock();
		
		try
		{
			final CrystalInstance[] array = crystals.array();
			
			for (int i = 0, length = crystals.size(); i < length; i++)
			{
				array[i].addFuncsTo(owner);
			}
		}
		
		finally
		{
			crystals.readUnlock();
		}
	}
	
	/**
	 * Method containsCrystal.
	 * @param stackType StackType
	 * @return boolean
	 */
	public boolean containsCrystal(StackType stackType)
	{
		final Array<CrystalInstance> crystals = getCrystals();
		
		if (crystals.isEmpty())
		{
			return false;
		}
		
		crystals.readLock();
		
		try
		{
			final CrystalInstance[] array = crystals.array();
			
			for (int i = 0, length = crystals.size(); i < length; i++)
			{
				if (array[i].getStackType() == stackType)
				{
					return true;
				}
			}
			
			return false;
		}
		
		finally
		{
			crystals.readUnlock();
		}
	}
	
	/**
	 * Method destruction.
	 * @param owner Character
	 * @return boolean
	 */
	public boolean destruction(Character owner)
	{
		int counter = 0;
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final Array<CrystalInstance> crystals = getCrystals();
		crystals.writeLock();
		
		try
		{
			for (int i = 0, length = crystals.size(); i < length; i++)
			{
				if (Rnd.chance(Config.WORLD_CHANCE_DELETE_CRYSTAL))
				{
					final CrystalInstance crystal = crystals.get(i);
					crystals.fastRemove(i--);
					crystal.removeFuncsTo(owner);
					crystal.setOwnerId(0);
					dbManager.updateLocationItem(crystal);
					PacketManager.showDeleteItem(owner, crystal);
					length--;
					counter++;
				}
			}
			
			return counter > 0;
		}
		
		finally
		{
			crystals.writeUnlock();
		}
	}
	
	public void finalyze()
	{
		final Array<CrystalInstance> crystals = getCrystals();
		
		if (crystals.isEmpty())
		{
			return;
		}
		
		crystals.readLock();
		
		try
		{
			final CrystalInstance[] array = crystals.array();
			
			for (int i = 0, length = crystals.size(); i < length; i++)
			{
				array[i].deleteMe();
			}
		}
		
		finally
		{
			crystals.readUnlock();
		}
		crystals.clear();
	}
	
	/**
	 * Method getCrystal.
	 * @param itemId int
	 * @return CrystalInstance
	 */
	public CrystalInstance getCrystal(int itemId)
	{
		final Array<CrystalInstance> crystals = getCrystals();
		
		if (crystals.isEmpty())
		{
			return null;
		}
		
		crystals.readLock();
		
		try
		{
			final CrystalInstance[] array = crystals.array();
			
			for (int i = 0, length = crystals.size(); i < length; i++)
			{
				final CrystalInstance crystal = array[i];
				
				if (crystal.getItemId() == itemId)
				{
					return crystal;
				}
			}
			
			return null;
		}
		
		finally
		{
			crystals.readUnlock();
		}
	}
	
	/**
	 * Method getArray.
	 * @return CrystalInstance[]
	 */
	public CrystalInstance[] getArray()
	{
		return crystals.array();
	}
	
	/**
	 * Method getCrystals.
	 * @return Array<CrystalInstance>
	 */
	public Array<CrystalInstance> getCrystals()
	{
		return crystals;
	}
	
	/**
	 * Method hasEmptySlot.
	 * @return boolean
	 */
	public boolean hasEmptySlot()
	{
		return crystals.size() < max;
	}
	
	/**
	 * Method isEmpty.
	 * @return boolean
	 */
	public boolean isEmpty()
	{
		return crystals.isEmpty();
	}
	
	/**
	 * Method put.
	 * @param crystal CrystalInstance
	 * @param cell Cell
	 * @param owner Character
	 */
	public void put(CrystalInstance crystal, Cell cell, Character owner)
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final Array<CrystalInstance> crystals = getCrystals();
		crystals.writeLock();
		
		try
		{
			if (crystals.size() < max)
			{
				CrystalInstance target = null;
				
				if (crystal.getItemCount() < 2)
				{
					target = crystal;
					
					if (cell != null)
					{
						cell.setItem(null);
					}
				}
				else
				{
					target = (CrystalInstance) crystal.getTemplate().newInstance();
					
					if (target == null)
					{
						return;
					}
					
					crystal.subItemCount(1);
					dbManager.updateDataItem(crystal);
				}
				
				target.setOwnerId(objectId);
				target.setLocation(ItemLocation.CRYSTAL);
				target.setIndex(0);
				
				if (owner != null)
				{
					target.addFuncsTo(owner);
				}
				
				dbManager.updateLocationItem(target);
				crystals.add(target);
			}
		}
		
		finally
		{
			crystals.writeUnlock();
		}
	}
	
	/**
	 * Method remove.
	 * @param crystal CrystalInstance
	 */
	public void remove(CrystalInstance crystal)
	{
		crystals.fastRemove(crystal);
	}
	
	/**
	 * Method removeFuncs.
	 * @param owner Character
	 */
	public void removeFuncs(Character owner)
	{
		final Array<CrystalInstance> crystals = getCrystals();
		crystals.readLock();
		
		try
		{
			final CrystalInstance[] array = crystals.array();
			
			for (int i = 0, length = crystals.size(); i < length; i++)
			{
				array[i].removeFuncsTo(owner);
			}
		}
		
		finally
		{
			crystals.readUnlock();
		}
	}
	
	/**
	 * Method setObjectId.
	 * @param objectId int
	 */
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	public int size()
	{
		return crystals.size();
	}
}

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
package tera.gameserver.model.playable;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.base.Experience;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.territory.Territory;
import tera.gameserver.model.territory.TerritoryType;
import tera.gameserver.tables.TerritoryTable;
import tera.gameserver.templates.CharTemplate;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class Playable extends Character
{
	
	protected final Array<Territory> territories;
	
	protected Equipment equipment;
	
	protected Inventory inventory;
	
	protected Bank bank;
	
	protected int level;
	
	protected int zoneId;
	
	protected int fractionId;
	
	protected volatile int exp;
	
	/**
	 * Constructor for Playable.
	 * @param objectId int
	 * @param template CharTemplate
	 */
	public Playable(int objectId, CharTemplate template)
	{
		super(objectId, template);
		level = 1;
		exp = Experience.LEVEL[1];
		territories = Arrays.toConcurrentArray(Territory.class, 2);
	}
	
	@Override
	public void deleteMe()
	{
		if (isDeleted())
		{
			return;
		}
		
		final TerritoryTable territoryTable = TerritoryTable.getInstance();
		territoryTable.onExitWorld(this);
		territories.clear();
		super.deleteMe();
		effectList.fold();
	}
	
	/**
	 * Method getBank.
	 * @return Bank
	 */
	@Override
	public Bank getBank()
	{
		return bank;
	}
	
	/**
	 * Method getEquipment.
	 * @return Equipment
	 */
	@Override
	public final Equipment getEquipment()
	{
		return equipment;
	}
	
	/**
	 * Method getFractionId.
	 * @return int
	 */
	public final int getFractionId()
	{
		return fractionId;
	}
	
	/**
	 * Method getInventory.
	 * @return Inventory
	 */
	@Override
	public final Inventory getInventory()
	{
		return inventory;
	}
	
	/**
	 * Method getTerritories.
	 * @return Array<Territory>
	 */
	@Override
	public final Array<Territory> getTerritories()
	{
		return territories;
	}
	
	/**
	 * Method getTerritory.
	 * @param <T>
	 * @param type Class<T>
	 * @return T
	 */
	public <T extends Territory> T getTerritory(Class<T> type)
	{
		final Array<Territory> territories = getTerritories();
		
		if (territories.isEmpty())
		{
			return null;
		}
		
		territories.readLock();
		
		try
		{
			final Territory[] array = territories.array();
			
			for (int i = 0, length = territories.size(); i < length; i++)
			{
				final Territory territory = array[i];
				
				if (type.isInstance(territory))
				{
					return type.cast(territory);
				}
			}
			
			return null;
		}
		
		finally
		{
			territories.readUnlock();
		}
	}
	
	/**
	 * Method getZoneId.
	 * @return int
	 */
	public final int getZoneId()
	{
		return zoneId;
	}
	
	/**
	 * Method hasPremium.
	 * @return boolean
	 */
	public boolean hasPremium()
	{
		return false;
	}
	
	/**
	 * Method isInBattleTerritory.
	 * @return boolean
	 */
	@Override
	public boolean isInBattleTerritory()
	{
		if ((territories == null) || territories.isEmpty())
		{
			return false;
		}
		
		territories.readLock();
		
		try
		{
			final Territory[] array = territories.array();
			
			for (int i = 0, length = territories.size(); i < length; i++)
			{
				if (array[i].getType() == TerritoryType.BATTLE_TERRITORY)
				{
					return true;
				}
			}
			
			return false;
		}
		
		finally
		{
			territories.readUnlock();
		}
	}
	
	/**
	 * Method isInBonfireTerritory.
	 * @return boolean
	 */
	@Override
	public boolean isInBonfireTerritory()
	{
		if ((territories == null) || territories.isEmpty())
		{
			return false;
		}
		
		territories.readLock();
		
		try
		{
			final Territory[] array = territories.array();
			
			for (int i = 0, length = territories.size(); i < length; i++)
			{
				if (array[i].getType() == TerritoryType.CAMP_TERRITORY)
				{
					return true;
				}
			}
			
			return false;
		}
		
		finally
		{
			territories.readUnlock();
		}
	}
	
	/**
	 * Method isInPeaceTerritory.
	 * @return boolean
	 */
	@Override
	public boolean isInPeaceTerritory()
	{
		if ((territories == null) || territories.isEmpty())
		{
			return false;
		}
		
		territories.readLock();
		
		try
		{
			final Territory[] array = territories.array();
			
			for (int i = 0, length = territories.size(); i < length; i++)
			{
				if (array[i].getType() == TerritoryType.PEACE_TERRITORY)
				{
					return true;
				}
			}
			
			return false;
		}
		
		finally
		{
			territories.readUnlock();
		}
	}
	
	/**
	 * Method setBank.
	 * @param bank Bank
	 */
	public void setBank(Bank bank)
	{
		this.bank = bank;
	}
	
	/**
	 * Method setEquipment.
	 * @param equipment Equipment
	 */
	@Override
	public final void setEquipment(Equipment equipment)
	{
		this.equipment = equipment;
	}
	
	/**
	 * Method setFractionId.
	 * @param fractionId int
	 */
	public final void setFractionId(int fractionId)
	{
		this.fractionId = fractionId;
	}
	
	/**
	 * Method setInventory.
	 * @param inventory Inventory
	 */
	@Override
	public final void setInventory(Inventory inventory)
	{
		this.inventory = inventory;
	}
	
	/**
	 * Method setTerritories.
	 * @param territories Array<Territory>
	 */
	@Override
	public final void setTerritories(Array<Territory> territories)
	{
		this.territories.addAll(territories);
	}
	
	/**
	 * Method setZoneId.
	 * @param zoneId int
	 */
	public final void setZoneId(int zoneId)
	{
		this.zoneId = zoneId;
	}
	
	@Override
	public void updateTerritories()
	{
		territories.writeLock();
		
		try
		{
			if (!territories.isEmpty())
			{
				final Territory[] array = territories.array();
				
				for (int i = 0, length = territories.size(); i < length; i++)
				{
					final Territory territory = array[i];
					
					if (territory.contains(x, y, z))
					{
						continue;
					}
					
					territory.onExit(this);
					territories.fastRemove(i--);
					length--;
				}
			}
			
			if (currentRegion == null)
			{
				return;
			}
			
			final Territory[] news = currentRegion.getTerritories();
			
			if ((news != null) && (news.length > 0))
			{
				for (Territory new1 : news)
				{
					final Territory territory = new1;
					
					if (territories.contains(territory))
					{
						continue;
					}
					
					if (!territory.contains(x, y, z))
					{
						continue;
					}
					
					territory.onEnter(this);
					territories.add(territory);
				}
			}
		}
		
		finally
		{
			territories.writeUnlock();
		}
	}
	
	@Override
	public void updateZoneId()
	{
		new Exception("update zone id").printStackTrace();
		WorldRegion region = getCurrentRegion();
		
		if (region == null)
		{
			region = World.getRegion(this);
		}
		
		final int newZoneId = region.getZoneId(this);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		
		if ((newZoneId != -1) && (newZoneId != zoneId))
		{
			setZoneId(newZoneId);
			eventManager.notifyChangedZoneId(this);
		}
	}
}

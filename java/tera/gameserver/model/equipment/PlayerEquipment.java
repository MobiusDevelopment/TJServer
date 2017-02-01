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

import tera.gameserver.model.Character;
import tera.gameserver.model.items.ItemInstance;

import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class PlayerEquipment extends AbstractEquipment
{
	
	private static FoldablePool<Equipment> pool = Pools.newConcurrentFoldablePool(Equipment.class);
	
	private static final SlotType[] STRUCTURE =
	{
		SlotType.SLOT_WEAPON,
		SlotType.SLOT_SHIRT,
		SlotType.SLOT_ARMOR,
		SlotType.SLOT_GLOVES,
		SlotType.SLOT_BOOTS,
		
		SlotType.SLOT_EARRING,
		SlotType.SLOT_EARRING,
		SlotType.SLOT_RING,
		SlotType.SLOT_RING,
		SlotType.SLOT_NECKLACE,
		SlotType.SLOT_SHIRT,
		SlotType.SLOT_HAT,
		SlotType.SLOT_MASK,
	};
	
	public static Equipment newInstance(Character owner)
	{
		Equipment equipment = pool.take();
		
		if (equipment == null)
		{
			equipment = new PlayerEquipment(owner);
		}
		
		equipment.setOwner(owner);
		return equipment;
	}
	
	public PlayerEquipment(Character owner)
	{
		super(owner);
	}
	
	@Override
	public boolean equiped(ItemInstance item)
	{
		if ((item == null) || (owner == null))
		{
			return false;
		}
		
		if (owner.isBattleStanced())
		{
			owner.sendMessage("You can not wear things in a fighting stance.");
			return false;
		}
		
		return item.equipmentd(owner, true);
	}
	
	@Override
	public void fold()
	{
		pool.put(this);
	}
	
	@Override
	public void prepare()
	{
		recreateSlots(STRUCTURE);
	}
	
	@Override
	public PlayerEquipment setOwner(Character owner)
	{
		super.setOwner(owner);
		return this;
	}
	
	@Override
	public boolean unequiped(ItemInstance item)
	{
		if ((item == null) || (owner == null))
		{
			return false;
		}
		
		if (owner.isBattleStanced())
		{
			owner.sendMessage("Do not remove items in a fighting stance.");
			return false;
		}
		
		return true;
	}
}

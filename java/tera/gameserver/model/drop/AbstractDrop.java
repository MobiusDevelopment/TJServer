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

import java.util.Arrays;

import tera.Config;
import tera.gameserver.model.Character;
import tera.gameserver.model.TObject;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public abstract class AbstractDrop implements Drop
{
	protected static final Logger log = Loggers.getLogger(Drop.class);
	protected DropGroup[] groups;
	protected int templateId;
	
	/**
	 * Constructor for AbstractDrop.
	 * @param templateId int
	 * @param groups DropGroup[]
	 */
	public AbstractDrop(int templateId, DropGroup[] groups)
	{
		this.templateId = templateId;
		this.groups = groups;
	}
	
	/**
	 * Method addDrop.
	 * @param container Array<ItemInstance>
	 * @param creator TObject
	 * @param owner Character
	 */
	@Override
	public void addDrop(Array<ItemInstance> container, TObject creator, Character owner)
	{
		if (owner == null)
		{
			log.warning(this, new Exception("not found owner"));
			return;
		}
		
		if (creator == null)
		{
			log.warning(this, new Exception("not found creator"));
			return;
		}
		
		if (!checkCondition(creator, owner))
		{
			return;
		}
		
		final Player player = owner.getPlayer();
		float dropRate = Config.SERVER_RATE_DROP_ITEM;
		float moneyRate = Config.SERVER_RATE_MONEY;
		final boolean payed = player.hasPremium();
		
		if (Config.ACCOUNT_PREMIUM_MONEY && payed)
		{
			moneyRate *= Config.ACCOUNT_PREMIUM_MONEY_RATE;
		}
		
		if (Config.ACCOUNT_PREMIUM_DROP && payed)
		{
			dropRate *= Config.ACCOUNT_PREMIUM_DROP_RATE;
		}
		
		final DropGroup[] groups = getGroups();
		
		for (DropGroup group2 : groups)
		{
			final DropGroup group = group2;
			float maxCount = group.getCount();
			
			if (!group.isMoney())
			{
				maxCount *= dropRate;
			}
			
			final int count = Math.max((int) maxCount, 1);
			
			for (int g = 0; g < count; g++)
			{
				final ItemInstance item = group.getItem();
				
				if (item == null)
				{
					continue;
				}
				
				if (item.getItemId() == Inventory.MONEY_ITEM_ID)
				{
					final long newCount = (long) (item.getItemCount() * moneyRate);
					
					if (newCount < 1)
					{
						item.deleteMe();
						continue;
					}
					
					item.setItemCount(newCount);
				}
				
				container.add(item);
			}
		}
	}
	
	/**
	 * Method checkCondition.
	 * @param creator TObject
	 * @param owner Character
	 * @return boolean
	 */
	protected abstract boolean checkCondition(TObject creator, Character owner);
	
	/**
	 * Method getGroups.
	 * @return DropGroup[]
	 */
	protected final DropGroup[] getGroups()
	{
		return groups;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 * @see tera.gameserver.model.drop.Drop#getTemplateId()
	 */
	@Override
	public int getTemplateId()
	{
		return templateId;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "AbstractDrop groups = " + Arrays.toString(groups) + ", templateId = " + templateId;
	}
}
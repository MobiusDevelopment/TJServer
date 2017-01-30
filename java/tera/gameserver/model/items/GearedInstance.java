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

import tera.gameserver.model.Character;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.playable.Player;
import tera.gameserver.templates.ItemTemplate;

import rlib.util.Strings;

/**
 * @author Ronn
 */
public class GearedInstance extends ItemInstance
{
	
	protected CrystalList crystals;
	
	protected String ownerName;
	
	public GearedInstance(int objectId, ItemTemplate template)
	{
		super(objectId, template);
		ownerName = Strings.EMPTY;
		
		if (template.getSockets() > 0)
		{
			crystals = new CrystalList(template.getSockets(), objectId);
		}
		
		updateEnchantStats();
	}
	
	@Override
	public boolean equipmentd(Character character, boolean showMessage)
	{
		final Player player = character.getPlayer();
		
		if (player != null)
		{
			if (template.getBindType() == BindType.ON_EQUIP)
			{
				if (!isBinded())
				{
					if (showMessage)
					{
						player.sendMessage("You are not binded with an item.");
					}
					
					return false;
				}
				else if (!getOwnerName().equals(character.getName()))
				{
					if (showMessage)
					{
						player.sendMessage("This item is associated with another player.");
					}
					
					return false;
				}
			}
			
			if (!template.checkClass(player))
			{
				if (showMessage)
				{
					player.sendMessage(MessageType.THAT_ITEM_IS_UNAVAILABLE_TO_YOUR_CLASS);
				}
				
				return false;
			}
			
			if (template.getRequiredLevel() > player.getLevel())
			{
				if (showMessage)
				{
					player.sendMessage(MessageType.YOU_MUST_BE_A_HIGHER_LEVEL_TO_USE_THAT);
				}
				
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void finalyze()
	{
		super.finalyze();
		ownerName = Strings.EMPTY;
		
		if (crystals != null)
		{
			crystals.finalyze();
		}
	}
	
	@Override
	public CrystalList getCrystals()
	{
		return crystals;
	}
	
	@Override
	public String getOwnerName()
	{
		return ownerName;
	}
	
	@Override
	public boolean isBinded()
	{
		return ownerName != Strings.EMPTY;
	}
	
	@Override
	public void setObjectId(int objectId)
	{
		super.setObjectId(objectId);
		
		if (crystals != null)
		{
			crystals.setObjectId(objectId);
		}
	}
	
	@Override
	public void setOwnerName(String ownerName)
	{
		if (ownerName.isEmpty())
		{
			ownerName = Strings.EMPTY;
		}
		
		this.ownerName = ownerName;
	}
	
	@Override
	public final void setEnchantLevel(int enchantLevel)
	{
		super.setEnchantLevel(enchantLevel);
		updateEnchantStats();
	}
	
	protected void updateEnchantStats()
	{
	}
}

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
package tera.gameserver.templates;

import tera.gameserver.model.items.ArmorKind;
import tera.gameserver.model.items.ArmorType;
import tera.gameserver.model.playable.Player;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public final class ArmorTemplate extends GearedTemplate
{
	
	protected ArmorKind armorKind;
	
	/**
	 * Constructor for ArmorTemplate.
	 * @param type ArmorType
	 * @param vars VarTable
	 */
	public ArmorTemplate(ArmorType type, VarTable vars)
	{
		super(type, vars);
		
		try
		{
			armorKind = ArmorKind.valueOfXml(vars.getString("kind", "other"));
			slotType = type.getSlot();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Method checkClass.
	 * @param player Player
	 * @return boolean
	 */
	@Override
	public boolean checkClass(Player player)
	{
		return armorKind.checkClass(player);
	}
	
	/**
	 * Method getArmorKind.
	 * @return ArmorKind
	 */
	public final ArmorKind getArmorKind()
	{
		return armorKind;
	}
	
	/**
	 * Method getType.
	 * @return ArmorType
	 */
	@Override
	public ArmorType getType()
	{
		return (ArmorType) type;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ArmorTemplate  armorKind = " + armorKind + ", requiredLevel = " + requiredLevel + ", attack = " + attack + ", impact = " + impact + ", defence = " + defence + ", balance = " + balance + ", sockets = " + sockets + ", extractable = " + extractable + ", enchantable = " + enchantable + ", remodelable = " + remodelable + ", dyeable = " + dyeable + ", bindType = " + bindType + ", name = " + name + ", itemId = " + itemId + ", itemLevel = " + itemLevel + ", buyPrice = " + buyPrice + ", sellPrice = " + sellPrice + ", slotType = " + slotType + ", rank = " + rank + ", itemClass = " + itemClass + ", stackable = " + stackable + ", sellable = " + sellable + ", bank = " + bank + ", guildBank = " + guildBank + ", tradable = " + tradable + ", deletable = " + deletable + ", type = " + type;
	}
}

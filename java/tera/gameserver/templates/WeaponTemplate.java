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

import tera.gameserver.model.equipment.SlotType;
import tera.gameserver.model.items.WeaponType;
import tera.gameserver.model.playable.Player;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public final class WeaponTemplate extends GearedTemplate
{
	/**
	 * Constructor for WeaponTemplate.
	 * @param type WeaponType
	 * @param vars VarTable
	 */
	public WeaponTemplate(WeaponType type, VarTable vars)
	{
		super(type, vars);
		slotType = SlotType.SLOT_WEAPON;
	}
	
	/**
	 * Method checkClass.
	 * @param player Player
	 * @return boolean
	 */
	@Override
	public boolean checkClass(Player player)
	{
		return getType().checkClass(player);
	}
	
	/**
	 * Method getClassIdItemSkill.
	 * @return int
	 */
	@Override
	public int getClassIdItemSkill()
	{
		return -11;
	}
	
	/**
	 * Method getType.
	 * @return WeaponType
	 */
	@Override
	public final WeaponType getType()
	{
		return (WeaponType) type;
	}
}

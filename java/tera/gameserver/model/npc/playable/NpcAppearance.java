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
package tera.gameserver.model.npc.playable;

import tera.gameserver.model.playable.PlayerAppearance;

/**
 * @author Ronn
 */
public class NpcAppearance extends PlayerAppearance
{
	private int hatId;
	private int maskId;
	private int glovesId;
	private int bootsId;
	private int armorId;
	private int weaponId;
	
	/**
	 * Method getArmorId.
	 * @return int
	 */
	public int getArmorId()
	{
		return armorId;
	}
	
	/**
	 * Method getBootsId.
	 * @return int
	 */
	public int getBootsId()
	{
		return bootsId;
	}
	
	/**
	 * Method getGlovesId.
	 * @return int
	 */
	public int getGlovesId()
	{
		return glovesId;
	}
	
	/**
	 * Method getHatId.
	 * @return int
	 */
	public int getHatId()
	{
		return hatId;
	}
	
	/**
	 * Method getMaskId.
	 * @return int
	 */
	public int getMaskId()
	{
		return maskId;
	}
	
	/**
	 * Method getWeaponId.
	 * @return int
	 */
	public int getWeaponId()
	{
		return weaponId;
	}
}
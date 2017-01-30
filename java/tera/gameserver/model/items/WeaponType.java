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

import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public enum WeaponType
{
	
	TWIN_SWORDS("twinSwords", PlayerClass.WARRIOR),
	
	LANCE("lance", PlayerClass.LANCER),
	
	GREATSWORD("greatSword", PlayerClass.SLAYER),
	
	AXE("axe", PlayerClass.BERSERKER),
	
	BOW("bow", PlayerClass.ARCHER),
	
	DISC("disc", PlayerClass.SORCERER),
	
	STAFF("staff", PlayerClass.PRIEST),
	
	SCEPTER("scepter", PlayerClass.MYSTIC);
	
	/**
	 * Method valueOfXml.
	 * @param name String
	 * @return WeaponType
	 */
	public static WeaponType valueOfXml(String name)
	{
		for (WeaponType type : values())
		{
			if (type.getXmlName().equals(name))
			{
				return type;
			}
		}
		
		throw new IllegalArgumentException();
	}
	
	private String xmlName;
	
	private PlayerClass playerClass;
	
	/**
	 * Constructor for WeaponType.
	 * @param xmlName String
	 * @param playerClass PlayerClass
	 */
	private WeaponType(String xmlName, PlayerClass playerClass)
	{
		this.xmlName = xmlName;
		this.playerClass = playerClass;
	}
	
	/**
	 * Method checkClass.
	 * @param player Player
	 * @return boolean
	 */
	public boolean checkClass(Player player)
	{
		return playerClass.getId() == player.getClassId();
	}
	
	/**
	 * Method getXmlName.
	 * @return String
	 */
	public final String getXmlName()
	{
		return xmlName;
	}
}

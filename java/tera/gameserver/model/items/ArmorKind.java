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

import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public enum ArmorKind
{
	
	METAL("metal", PlayerClass.LANCER, PlayerClass.BERSERKER),
	
	LEATHER("leather", PlayerClass.WARRIOR, PlayerClass.SLAYER, PlayerClass.ARCHER),
	
	CLOTH("cloth", PlayerClass.PRIEST, PlayerClass.SORCERER, PlayerClass.MYSTIC),
	
	OTHER("other", PlayerClass.values);
	
	/**
	 * Method valueOfXml.
	 * @param name String
	 * @return ArmorKind
	 */
	public static ArmorKind valueOfXml(String name)
	{
		for (ArmorKind type : values())
		{
			if (type.getXmlName().equals(name))
			{
				return type;
			}
		}
		
		throw new IllegalArgumentException("no enum " + name);
	}
	
	private String xmlName;
	
	private PlayerClass[] classes;
	
	/**
	 * Constructor for ArmorKind.
	 * @param xmlName String
	 * @param classes PlayerClass[]
	 */
	private ArmorKind(String xmlName, PlayerClass... classes)
	{
		this.xmlName = xmlName;
		this.classes = classes;
	}
	
	/**
	 * Method checkClass.
	 * @param player Player
	 * @return boolean
	 */
	public boolean checkClass(Player player)
	{
		return Arrays.contains(classes, player.getPlayerClass());
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

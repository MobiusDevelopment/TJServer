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

import tera.gameserver.model.equipment.SlotType;

/**
 * @author Ronn
 */
public enum CommonType
{
	
	MONEY("money", SlotType.NONE),
	
	POTION("potion", SlotType.NONE),
	
	SCROLL("scroll", SlotType.NONE),
	
	QUEST("quest", SlotType.NONE),
	
	MATERIAL("material", SlotType.NONE),
	
	PACKAGE("package", SlotType.NONE),
	
	HERB("herb", SlotType.NONE),
	
	OTHER("other", SlotType.NONE);
	
	/**
	 * Method valueOfXml.
	 * @param name String
	 * @return CommonType
	 */
	public static CommonType valueOfXml(String name)
	{
		for (CommonType type : values())
		{
			if (type.getXmlName().equals(name))
			{
				return type;
			}
		}
		
		throw new IllegalArgumentException();
	}
	
	private String xmlName;
	
	private SlotType slot;
	
	/**
	 * Constructor for CommonType.
	 * @param xmlName String
	 * @param slot SlotType
	 */
	private CommonType(String xmlName, SlotType slot)
	{
		this.xmlName = xmlName;
		this.slot = slot;
	}
	
	/**
	 * Method getSlot.
	 * @return SlotType
	 */
	public final SlotType getSlot()
	{
		return slot;
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

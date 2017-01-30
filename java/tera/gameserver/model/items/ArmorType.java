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
public enum ArmorType
{
	
	BOOTS("boots", SlotType.SLOT_BOOTS),
	
	GLOVES("gloves", SlotType.SLOT_GLOVES),
	
	SHIRT("shirt", SlotType.SLOT_SHIRT),
	
	BODY("body", SlotType.SLOT_ARMOR),
	
	EARRING("earring", SlotType.SLOT_EARRING),
	
	RING("ring", SlotType.SLOT_RING),
	
	NECKLACE("necklace", SlotType.SLOT_NECKLACE),
	
	MASK("mask", SlotType.SLOT_MASK),
	
	HAT("hat", SlotType.SLOT_HAT),
	
	REMODEL("remodel", SlotType.SLOT_ARMOR_REMODEL);
	
	/**
	 * Method valueOfXml.
	 * @param name String
	 * @return ArmorType
	 */
	public static ArmorType valueOfXml(String name)
	{
		for (ArmorType type : values())
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
	 * Constructor for ArmorType.
	 * @param xmlName String
	 * @param slot SlotType
	 */
	private ArmorType(String xmlName, SlotType slot)
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

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

/**
 * @author Ronn
 */
public enum BindType
{
	UNKNOWN("unknown"),
	UNKNOW("unknow"),
	NONE("none"),
	ON_PICK_UP("onPickUp"),
	ON_EQUIP("onEquip");
	
	/**
	 * Method valueOfXml.
	 * @param name String
	 * @return BindType
	 */
	public static BindType valueOfXml(String name)
	{
		for (BindType type : values())
		{
			if (type.getXmlName().equals(name))
			{
				return type;
			}
		}
		
		throw new IllegalArgumentException("no enum " + name);
	}
	
	private String xmlName;
	
	/**
	 * Constructor for BindType.
	 * @param xmlName String
	 */
	private BindType(String xmlName)
	{
		this.xmlName = xmlName;
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

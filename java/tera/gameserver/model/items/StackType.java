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
public enum StackType
{
	NONE("none"),
	
	SLAYING_WEAPON("slaying"),
	FURIOUS_WEAPON("furious"),
	FOCUS_WEAPON("focused"),
	VIRULENT_WEAPON("virulent"),
	BRUTAL_WEAPON("brutal"),
	CRUEL_WEAPON("cruel"),
	FORCEFUL_WEAPON("forceful"),
	SAVAGE_WEAPON("savage"),
	CUNNING_WEAPON("cunning"),
	INFUSED_WEAPON("infused"),
	GLISTENING_WEAPON("glistening"),
	SWIFT_WEAPON("swift"),
	BRILLIANT_WEAPON("brilliant"),
	SALIVATING_WEAPON("salivating"),
	THREATENING_WEAPON("threatening"),
	ACRIMONIOUS_WEAPON("acrimonious"),
	BACKBITING_WEAPON("backbiting"),
	HUNTERS_WEAPON("hunters"),
	CARVING_WEAPON("carving"),
	DOMINEERING_WEAPON("domineering"),
	MUTINOUS_WEAPON("mutinous"),
	SQUELCHING_WEAPON("squelching"),
	
	PROTECTIVE_ARMOR("protective"),
	RESOLUTE_ARMOR("resolute"),
	POISED_ARMOR("poised"),
	WARDING_ARMOR("warding"),
	INSPIRING_ARMOR("inspiring"),
	RELENTLESS_ARMOR("relentless"),
	FLEETFOT_ARMOR("fleetfoot"),
	VIGOROUS_ARMOR("vigorous"),
	GRIEVING_ARMOR("grieving"),
	NOBLESSE_ARMOR("noblesse"),
	ANARCHIC_ARMOR("anarchic"),
	DAUNTLESS_ARMOR("dauntless"),
	EMPYREAN_ARMOR("empyrean"),
	STALWART_ARMOR("stalwart"),
	SOOTHING_ARMOR("soothing");
	
	/**
	 * Method valueOfXml.
	 * @param name String
	 * @return StackType
	 */
	public static StackType valueOfXml(String name)
	{
		for (StackType type : values())
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
	 * Constructor for StackType.
	 * @param xmlName String
	 */
	private StackType(String xmlName)
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

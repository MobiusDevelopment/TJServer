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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import tera.gameserver.IdFactory;
import tera.gameserver.templates.ArmorTemplate;
import tera.gameserver.templates.CommonTemplate;
import tera.gameserver.templates.CrystalTemplate;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.templates.WeaponTemplate;
import tera.util.constructors.ConstructorItem;

import rlib.logging.Loggers;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public enum ItemClass
{
	
	ARMOR("armor", ArmorTemplate.class, ConstructorItem.ARMOR, ArmorType.class),
	
	WEAPON("weapon", WeaponTemplate.class, ConstructorItem.WEAPON, WeaponType.class),
	
	COMMON_ITEM("common", CommonTemplate.class, ConstructorItem.COMMON, CommonType.class),
	
	CRYSTAL("crystal", CrystalTemplate.class, ConstructorItem.CRYSTAL, CrystalType.class);
	
	/**
	 * Method valueOfXml.
	 * @param name String
	 * @return ItemClass
	 */
	public static ItemClass valueOfXml(String name)
	{
		for (ItemClass type : values())
		{
			if (type.getXmlName().equals(name))
			{
				return type;
			}
		}
		
		return null;
	}
	
	private String xmlName;
	
	private Constructor<?> templateConstructor;
	
	private ConstructorItem constructor;
	
	private Method getType;
	
	/**
	 * Constructor for ItemClass.
	 * @param xmlName String
	 * @param templateClass Class<? extends ItemTemplate>
	 * @param constructor ConstructorItem
	 * @param itemType Class<? extends Enum<?>>
	 */
	private ItemClass(String xmlName, Class<? extends ItemTemplate> templateClass, ConstructorItem constructor, Class<? extends Enum<?>> itemType)
	{
		this.xmlName = xmlName;
		this.constructor = constructor;
		
		try
		{
			templateConstructor = templateClass.getConstructors()[0];
			getType = itemType.getMethod("valueOfXml", String.class);
		}
		catch (SecurityException | NoSuchMethodException e)
		{
			Loggers.warning(this, e);
		}
	}
	
	/**
	 * Method getXmlName.
	 * @return String
	 */
	public final String getXmlName()
	{
		return xmlName;
	}
	
	/**
	 * Method newInstance.
	 * @param objectId int
	 * @param template ItemTemplate
	 * @return ItemInstance
	 */
	public ItemInstance newInstance(int objectId, ItemTemplate template)
	{
		return constructor.newInstance(objectId, template);
	}
	
	/**
	 * Method newInstance.
	 * @param template ItemTemplate
	 * @return ItemInstance
	 */
	public ItemInstance newInstance(ItemTemplate template)
	{
		final IdFactory idFactory = IdFactory.getInstance();
		return constructor.newInstance(idFactory.getNextItemId(), template);
	}
	
	/**
	 * Method newTemplate.
	 * @param vars VarTable
	 * @return ItemTemplate
	 */
	public ItemTemplate newTemplate(VarTable vars)
	{
		try
		{
			return (ItemTemplate) templateConstructor.newInstance(getType.invoke(null, vars.getString("type")), vars);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, e);
			System.out.println(vars);
		}
		
		return null;
	}
}

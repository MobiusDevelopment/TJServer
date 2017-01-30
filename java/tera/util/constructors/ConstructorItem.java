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
package tera.util.constructors;

import tera.gameserver.model.items.ArmorInstance;
import tera.gameserver.model.items.CommonInstance;
import tera.gameserver.model.items.CrystalInstance;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.WeaponInstance;
import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 * @created 14.04.2012
 */
public interface ConstructorItem
{
	public static final ConstructorItem WEAPON = (objectId, template) -> new WeaponInstance(objectId, template);
	
	public static final ConstructorItem ARMOR = (objectId, template) -> new ArmorInstance(objectId, template);
	
	public static final ConstructorItem COMMON = (objectId, template) -> new CommonInstance(objectId, template);
	
	public static final ConstructorItem CRYSTAL = (objectId, template) -> new CrystalInstance(objectId, template);
	
	/**
	 * Method newInstance.
	 * @param objectId int
	 * @param template ItemTemplate
	 * @return ItemInstance
	 */
	public ItemInstance newInstance(int objectId, ItemTemplate template);
}

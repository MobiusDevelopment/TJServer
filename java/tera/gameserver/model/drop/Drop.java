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
package tera.gameserver.model.drop;

import tera.gameserver.model.Character;
import tera.gameserver.model.TObject;
import tera.gameserver.model.items.ItemInstance;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public interface Drop
{
	/**
	 * Method addDrop.
	 * @param container Array<ItemInstance>
	 * @param creator TObject
	 * @param owner Character
	 */
	public void addDrop(Array<ItemInstance> container, TObject creator, Character owner);
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	public int getTemplateId();
	
	/**
	 * Method getTemplateType.
	 * @return int
	 */
	public int getTemplateType();
}
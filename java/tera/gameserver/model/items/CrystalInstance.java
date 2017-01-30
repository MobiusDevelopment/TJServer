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

import tera.gameserver.templates.CrystalTemplate;
import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 */
public class CrystalInstance extends ItemInstance
{
	/**
	 * Constructor for CrystalInstance.
	 * @param objectId int
	 * @param template ItemTemplate
	 */
	public CrystalInstance(int objectId, ItemTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method getCrystal.
	 * @return CrystalInstance
	 */
	@Override
	public CrystalInstance getCrystal()
	{
		return this;
	}
	
	/**
	 * Method getStackType.
	 * @return StackType
	 */
	public StackType getStackType()
	{
		return getTemplate().getStackType();
	}
	
	/**
	 * Method getTemplate.
	 * @return CrystalTemplate
	 */
	@Override
	public CrystalTemplate getTemplate()
	{
		return (CrystalTemplate) template;
	}
	
	/**
	 * Method getType.
	 * @return CrystalType
	 */
	@Override
	public CrystalType getType()
	{
		return (CrystalType) template.getType();
	}
	
	/**
	 * Method isCrystal.
	 * @return boolean
	 */
	@Override
	public boolean isCrystal()
	{
		return true;
	}
	
	/**
	 * Method isNoStack.
	 * @return boolean
	 */
	public boolean isNoStack()
	{
		return getTemplate().isNoStack();
	}
}

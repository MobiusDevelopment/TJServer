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

/**
 * @author Ronn
 */
public final class ResourseDrop extends AbstractDrop
{
	/**
	 * Constructor for ResourseDrop.
	 * @param templateId int
	 * @param groups DropGroup[]
	 */
	public ResourseDrop(int templateId, DropGroup[] groups)
	{
		super(templateId, groups);
	}
	
	/**
	 * Method checkCondition.
	 * @param creator TObject
	 * @param owner Character
	 * @return boolean
	 */
	@Override
	protected boolean checkCondition(TObject creator, Character owner)
	{
		if (!creator.isResourse() || !owner.isPlayer())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 * @see tera.gameserver.model.drop.Drop#getTemplateType()
	 */
	@Override
	public int getTemplateType()
	{
		return -1;
	}
}
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
package tera.gameserver.templates;

import tera.gameserver.model.items.CrystalType;
import tera.gameserver.model.items.StackType;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class CrystalTemplate extends ItemTemplate
{
	
	private StackType stackType;
	
	private boolean noStack;
	
	/**
	 * Constructor for CrystalTemplate.
	 * @param type CrystalType
	 * @param vars VarTable
	 */
	public CrystalTemplate(CrystalType type, VarTable vars)
	{
		super(type, vars);
		
		try
		{
			stackType = StackType.valueOfXml(vars.getString("stackType", "none"));
			noStack = vars.getBoolean("noStack", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Method getClassIdItemSkill.
	 * @return int
	 */
	@Override
	public int getClassIdItemSkill()
	{
		return -10;
	}
	
	/**
	 * Method getStackType.
	 * @return StackType
	 */
	public StackType getStackType()
	{
		return stackType;
	}
	
	/**
	 * Method getType.
	 * @return CrystalType
	 */
	@Override
	public CrystalType getType()
	{
		return (CrystalType) type;
	}
	
	/**
	 * Method isNoStack.
	 * @return boolean
	 */
	public final boolean isNoStack()
	{
		return noStack;
	}
}

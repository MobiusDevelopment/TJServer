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

import tera.gameserver.model.items.BindType;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class GearedTemplate extends ItemTemplate
{
	
	protected int requiredLevel;
	
	protected int attack;
	
	protected int impact;
	
	protected int defence;
	
	protected int balance;
	
	protected int sockets;
	
	protected int extractable;
	
	protected boolean enchantable;
	
	protected boolean remodelable;
	
	protected boolean dyeable;
	
	protected BindType bindType;
	
	/**
	 * Constructor for GearedTemplate.
	 * @param type Enum<?>
	 * @param vars VarTable
	 */
	public GearedTemplate(Enum<?> type, VarTable vars)
	{
		super(type, vars);
		
		try
		{
			attack = vars.getInteger("attack", 0);
			impact = vars.getInteger("impact", 0);
			defence = vars.getInteger("defence", 0);
			balance = vars.getInteger("balance", 0);
			sockets = vars.getInteger("sockets", 0);
			extractable = vars.getInteger("extractable", 0);
			requiredLevel = vars.getInteger("requiredLevel", 0);
			itemLevel = vars.getInteger("itemLevel", requiredLevel);
			enchantable = vars.getBoolean("enchantable", true);
			remodelable = vars.getBoolean("remodelable", true);
			dyeable = vars.getBoolean("dyeable", true);
			stackable = false;
			bindType = BindType.valueOfXml(vars.getString("bindType", "none"));
		}
		catch (Exception e)
		{
			log.warning(this, e);
			throw e;
		}
	}
	
	/**
	 * Method getAttack.
	 * @return int
	 */
	@Override
	public final int getAttack()
	{
		return attack;
	}
	
	/**
	 * Method getBalance.
	 * @return int
	 */
	@Override
	public final int getBalance()
	{
		return balance;
	}
	
	/**
	 * Method getBindType.
	 * @return BindType
	 */
	@Override
	public final BindType getBindType()
	{
		return bindType;
	}
	
	/**
	 * Method getDefence.
	 * @return int
	 */
	@Override
	public final int getDefence()
	{
		return defence;
	}
	
	/**
	 * Method getExtractable.
	 * @return int
	 */
	@Override
	public final int getExtractable()
	{
		return extractable;
	}
	
	/**
	 * Method getImpact.
	 * @return int
	 */
	@Override
	public final int getImpact()
	{
		return impact;
	}
	
	/**
	 * Method getRequiredLevel.
	 * @return int
	 */
	@Override
	public final int getRequiredLevel()
	{
		return requiredLevel;
	}
	
	/**
	 * Method getSockets.
	 * @return int
	 */
	@Override
	public final int getSockets()
	{
		return sockets;
	}
	
	/**
	 * Method isEnchantable.
	 * @return boolean
	 */
	@Override
	public final boolean isEnchantable()
	{
		return enchantable;
	}
	
	/**
	 * Method isRemodelable.
	 * @return boolean
	 */
	@Override
	public final boolean isRemodelable()
	{
		return remodelable;
	}
}

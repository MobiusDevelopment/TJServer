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

import tera.gameserver.templates.ArmorTemplate;
import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 */
public final class ArmorInstance extends GearedInstance
{
	
	private int defense;
	
	private int balance;
	
	/**
	 * Constructor for ArmorInstance.
	 * @param objectId int
	 * @param template ItemTemplate
	 */
	public ArmorInstance(int objectId, ItemTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method checkCrystal.
	 * @param crystal CrystalInstance
	 * @return boolean
	 */
	@Override
	public boolean checkCrystal(CrystalInstance crystal)
	{
		if ((crystals == null) || (crystal.getType() != CrystalType.ARMOR))
		{
			return false;
		}
		
		if (crystal.getItemLevel() > template.getItemLevel())
		{
			return false;
		}
		
		return crystals.hasEmptySlot();
	}
	
	/**
	 * Method getArmor.
	 * @return ArmorInstance
	 */
	@Override
	public ArmorInstance getArmor()
	{
		return this;
	}
	
	/**
	 * Method getArmorKind.
	 * @return ArmorKind
	 */
	public ArmorKind getArmorKind()
	{
		return getTemplate().getArmorKind();
	}
	
	/**
	 * Method getTemplate.
	 * @return ArmorTemplate
	 */
	@Override
	public ArmorTemplate getTemplate()
	{
		return (ArmorTemplate) template;
	}
	
	/**
	 * Method isArmor.
	 * @return boolean
	 */
	@Override
	public boolean isArmor()
	{
		return true;
	}
	
	@Override
	protected void updateEnchantStats()
	{
		int defense = super.getDefence();
		int balance = super.getBalance();
		float mod = ((4.5F * getEnchantLevel()) / 100F) + 1;
		defense *= mod;
		mod = ((7F * getEnchantLevel()) / 100F) + 1;
		balance *= mod;
		setDefense(defense);
		setBalance(balance);
	}
	
	/**
	 * Method getDefence.
	 * @return int
	 */
	@Override
	public int getDefence()
	{
		return defense;
	}
	
	/**
	 * Method getBalance.
	 * @return int
	 */
	@Override
	public int getBalance()
	{
		return balance;
	}
	
	/**
	 * Method setDefense.
	 * @param defense int
	 */
	private void setDefense(int defense)
	{
		this.defense = defense;
	}
	
	/**
	 * Method setBalance.
	 * @param balance int
	 */
	private void setBalance(int balance)
	{
		this.balance = balance;
	}
}

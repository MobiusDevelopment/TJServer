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

import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.templates.WeaponTemplate;

/**
 * @author Ronn
 */
public final class WeaponInstance extends GearedInstance
{
	
	private int attack;
	
	private int impact;
	
	/**
	 * Constructor for WeaponInstance.
	 * @param objectId int
	 * @param template ItemTemplate
	 */
	public WeaponInstance(int objectId, ItemTemplate template)
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
		if ((crystals == null) || (crystal.getType() != CrystalType.WEAPON))
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
	 * Method getTemplate.
	 * @return WeaponTemplate
	 */
	@Override
	public WeaponTemplate getTemplate()
	{
		return (WeaponTemplate) template;
	}
	
	/**
	 * Method getWeapon.
	 * @return WeaponInstance
	 */
	@Override
	public WeaponInstance getWeapon()
	{
		return this;
	}
	
	/**
	 * Method isWeapon.
	 * @return boolean
	 */
	@Override
	public boolean isWeapon()
	{
		return true;
	}
	
	@Override
	protected void updateEnchantStats()
	{
		int attack = super.getAttack();
		int impact = super.getImpact();
		float mod = ((3F * getEnchantLevel()) / 100F) + 1;
		attack *= mod;
		mod = ((7F * getEnchantLevel()) / 100F) + 1;
		impact *= mod;
		setAttack(attack);
		setImpact(impact);
	}
	
	/**
	 * Method setAttack.
	 * @param attack int
	 */
	private void setAttack(int attack)
	{
		this.attack = attack;
	}
	
	/**
	 * Method setImpact.
	 * @param impact int
	 */
	private void setImpact(int impact)
	{
		this.impact = impact;
	}
	
	/**
	 * Method getImpact.
	 * @return int
	 */
	@Override
	public int getImpact()
	{
		return impact;
	}
	
	/**
	 * Method getAttack.
	 * @return int
	 */
	@Override
	public int getAttack()
	{
		return attack;
	}
}
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
package tera.gameserver.model;

/**
 * @author Ronn
 */
public final class AttackInfo
{
	
	private int damage;
	
	private boolean crit;
	
	private boolean blocked;
	
	private boolean owerturn;
	
	public AttackInfo()
	{
		super();
	}
	
	/**
	 * Constructor for AttackInfo.
	 * @param damage int
	 */
	public AttackInfo(int damage)
	{
		this.damage = damage;
	}
	
	/**
	 * Method addDamage.
	 * @param damage int
	 */
	public void addDamage(int damage)
	{
		this.damage += damage;
	}
	
	/**
	 * Method clear.
	 * @return AttackInfo
	 */
	public AttackInfo clear()
	{
		damage = 0;
		owerturn = false;
		crit = false;
		blocked = false;
		return this;
	}
	
	/**
	 * Method divDamage.
	 * @param mod float
	 */
	public void divDamage(float mod)
	{
		damage /= mod;
	}
	
	/**
	 * Method divDamage.
	 * @param mod int
	 */
	public void divDamage(int mod)
	{
		damage /= mod;
	}
	
	/**
	 * Method getDamage.
	 * @return int
	 */
	public int getDamage()
	{
		return damage;
	}
	
	/**
	 * Method getDamageMp.
	 * @return int
	 */
	public int getDamageMp()
	{
		return damage / 3;
	}
	
	/**
	 * Method isBlocked.
	 * @return boolean
	 */
	public boolean isBlocked()
	{
		return blocked;
	}
	
	/**
	 * Method isCrit.
	 * @return boolean
	 */
	public boolean isCrit()
	{
		return crit;
	}
	
	/**
	 * Method isNoDamage.
	 * @return boolean
	 */
	public boolean isNoDamage()
	{
		return damage < 1;
	}
	
	/**
	 * Method isOwerturn.
	 * @return boolean
	 */
	public boolean isOwerturn()
	{
		return owerturn;
	}
	
	/**
	 * Method mulDamage.
	 * @param mod float
	 */
	public void mulDamage(float mod)
	{
		damage *= mod;
	}
	
	/**
	 * Method mulDamage.
	 * @param mod int
	 */
	public void mulDamage(int mod)
	{
		damage *= mod;
	}
	
	/**
	 * Method setBlocked.
	 * @param blocked boolean
	 */
	public void setBlocked(boolean blocked)
	{
		this.blocked = blocked;
	}
	
	/**
	 * Method setCrit.
	 * @param crit boolean
	 */
	public void setCrit(boolean crit)
	{
		this.crit = crit;
	}
	
	/**
	 * Method setDamage.
	 * @param damage int
	 */
	public void setDamage(int damage)
	{
		this.damage = damage;
	}
	
	/**
	 * Method setOwerturn.
	 * @param owerturn boolean
	 */
	public void setOwerturn(boolean owerturn)
	{
		this.owerturn = owerturn;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "AttackInfo damage = " + damage + ", crit = " + crit + ", blocked = " + blocked + ", owerturn = " + owerturn;
	}
}

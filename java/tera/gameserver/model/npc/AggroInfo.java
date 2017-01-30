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
package tera.gameserver.model.npc;

import tera.gameserver.model.Character;

import rlib.util.pools.Foldable;

/**
 * @author Ronn
 */
public final class AggroInfo implements Foldable, Comparable<AggroInfo>
{
	private Character aggressor;
	private long aggro;
	private long damage;
	
	/**
	 * Method addAggro.
	 * @param aggro long
	 */
	public void addAggro(long aggro)
	{
		this.aggro += aggro;
	}
	
	/**
	 * Method addDamage.
	 * @param damage long
	 */
	public void addDamage(long damage)
	{
		this.damage += damage;
	}
	
	/**
	 * Method compareTo.
	 * @param info AggroInfo
	 * @return int
	 */
	@Override
	public int compareTo(AggroInfo info)
	{
		return (int) (aggro - info.aggro);
	}
	
	/**
	 * Method equals.
	 * @param object Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object object)
	{
		if (object == null)
		{
			return false;
		}
		
		if (object == this)
		{
			return true;
		}
		
		if (object == aggressor)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		aggressor = null;
		aggro = 0L;
		damage = 0L;
	}
	
	/**
	 * Method getAggressor.
	 * @return Character
	 */
	public Character getAggressor()
	{
		return aggressor;
	}
	
	/**
	 * Method getAggro.
	 * @return long
	 */
	public long getAggro()
	{
		return aggro;
	}
	
	/**
	 * Method getDamage.
	 * @return long
	 */
	public final long getDamage()
	{
		return damage;
	}
	
	/**
	 * Method hasAggressor.
	 * @return boolean
	 */
	public boolean hasAggressor()
	{
		return aggressor != null;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	/**
	 * Method setAggressor.
	 * @param aggressor Character
	 */
	public void setAggressor(Character aggressor)
	{
		this.aggressor = aggressor;
	}
	
	/**
	 * Method setAggro.
	 * @param aggro long
	 */
	public void setAggro(long aggro)
	{
		this.aggro = aggro;
	}
	
	/**
	 * Method setDamage.
	 * @param damage long
	 */
	public final void setDamage(long damage)
	{
		this.damage = damage;
	}
	
	/**
	 * Method subAggro.
	 * @param aggro long
	 */
	public void subAggro(long aggro)
	{
		this.aggro -= aggro;
	}
	
	/**
	 * Method subDamage.
	 * @param damage long
	 */
	public void subDamage(long damage)
	{
		this.damage -= damage;
	}
}
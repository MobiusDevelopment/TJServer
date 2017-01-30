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

import tera.gameserver.model.npc.Npc;

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class HateInfo implements Foldable
{
	private static final FoldablePool<HateInfo> pool = Pools.newConcurrentFoldablePool(HateInfo.class);
	
	/**
	 * Method newInstance.
	 * @param npc Npc
	 * @return HateInfo
	 */
	public static HateInfo newInstance(Npc npc)
	{
		HateInfo info = pool.take();
		
		if (info == null)
		{
			info = new HateInfo(npc);
		}
		else
		{
			info.npc = npc;
		}
		
		return info;
	}
	
	private Npc npc;
	private long hate;
	private long damage;
	
	/**
	 * Constructor for HateInfo.
	 * @param npc Npc
	 */
	private HateInfo(Npc npc)
	{
		super();
		this.npc = npc;
		hate = 0L;
		damage = 0L;
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
	 * Method addHate.
	 * @param hate long
	 */
	public void addHate(long hate)
	{
		this.hate += hate;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		npc = null;
		damage = 0;
		hate = 0;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getDamage.
	 * @return long
	 */
	public long getDamage()
	{
		return damage;
	}
	
	/**
	 * Method getHate.
	 * @return long
	 */
	public long getHate()
	{
		return hate;
	}
	
	/**
	 * Method getNpc.
	 * @return Npc
	 */
	public Npc getNpc()
	{
		return npc;
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
	 * Method setDamage.
	 * @param damage long
	 */
	public void setDamage(long damage)
	{
		this.damage = damage;
	}
	
	/**
	 * Method setHate.
	 * @param hate long
	 */
	public void setHate(long hate)
	{
		this.hate = hate;
	}
	
	/**
	 * Method setNpc.
	 * @param npc Npc
	 */
	public void setNpc(Npc npc)
	{
		this.npc = npc;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "HateInfo  hate = " + hate + ", damage = " + damage;
	}
}
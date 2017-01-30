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

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class ReuseSkill implements Foldable
{
	private static final FoldablePool<ReuseSkill> pool = Pools.newConcurrentFoldablePool(ReuseSkill.class);
	
	/**
	 * Method newInstance.
	 * @param skillId int
	 * @param reuse long
	 * @return ReuseSkill
	 */
	public static final ReuseSkill newInstance(int skillId, long reuse)
	{
		ReuseSkill reuseSkill = pool.take();
		
		if (reuseSkill == null)
		{
			reuseSkill = new ReuseSkill();
		}
		
		reuseSkill.skillId = skillId;
		reuseSkill.endTime = System.currentTimeMillis() + reuse;
		return reuseSkill;
	}
	
	private int skillId;
	
	private int itemId;
	
	private long endTime;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		itemId = 0;
		skillId = 0;
		endTime = 0;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getCurrentDelay.
	 * @return long
	 */
	public long getCurrentDelay()
	{
		final long rest = endTime - System.currentTimeMillis();
		
		if (rest > 0)
		{
			return rest;
		}
		
		return 0;
	}
	
	/**
	 * Method getEndTime.
	 * @return long
	 */
	public long getEndTime()
	{
		return endTime;
	}
	
	/**
	 * Method getItemId.
	 * @return int
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Method getSkillId.
	 * @return int
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Method isItemReuse.
	 * @return boolean
	 */
	public boolean isItemReuse()
	{
		return itemId > 0;
	}
	
	/**
	 * Method isUse.
	 * @return boolean
	 */
	public boolean isUse()
	{
		return System.currentTimeMillis() < endTime;
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
	 * Method setEndTime.
	 * @param endTime long
	 */
	public void setEndTime(long endTime)
	{
		this.endTime = endTime;
	}
	
	/**
	 * Method setItemId.
	 * @param itemId int
	 * @return ReuseSkill
	 */
	public ReuseSkill setItemId(int itemId)
	{
		this.itemId = itemId;
		return this;
	}
	
	/**
	 * Method setSkillId.
	 * @param skillId int
	 */
	public void setSkillId(int skillId)
	{
		this.skillId = skillId;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ReuseSkill  skillId = " + skillId + ", itemId = " + itemId + ", endTime = " + endTime;
	}
}

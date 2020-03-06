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
package tera.gameserver.scripts.items;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import rlib.logging.Loggers;

/**
 * @author Ronn
 * @created 04.03.2012
 */
public enum ItemExecutorType
{
	EVENT_REWARD_BOX(EventRewardBox.class, 0, 408, 409, 410, 411),
	SKILL_LEARN_ITEM(SkillLearnItem.class, 0, 20, 21, 41, 166, 167, 168, 169, 170, 306, 307, 336, 350, 351, 384, 385, 412, 413, 414, 415, 416, 417, 425),
	BARBECUE_ITEMS(BarbecueItem.class, 0, 5027);
	
	private Constructor<? extends ItemExecutor> constructor;
	
	private int access;
	
	private int[] itemIds;
	
	/**
	 * Constructor for ItemExecutorType.
	 * @param type Class<? extends ItemExecutor>
	 * @param access int
	 * @param itemIds int[]
	 */
	private ItemExecutorType(Class<? extends ItemExecutor> type, int access, int... itemIds)
	{
		this.access = access;
		this.itemIds = itemIds;
		
		try
		{
			constructor = type.getConstructor(int[].class, int.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Loggers.warning(this, e);
		}
	}
	
	/**
	 * Method getCount.
	 * @return int
	 */
	public int getCount()
	{
		return itemIds.length;
	}
	
	/**
	 * Method newInstance.
	 * @return ItemExecutor
	 */
	public ItemExecutor newInstance()
	{
		try
		{
			return constructor.newInstance(itemIds, access);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, e);
		}
		
		return null;
	}
}

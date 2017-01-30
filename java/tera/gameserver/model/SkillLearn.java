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

import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public final class SkillLearn
{
	
	private final int id;
	
	private int useId;
	
	private final int price;
	
	private final int replaceId;
	
	private int replaceUseId;
	
	private final int classId;
	
	private final int minLevel;
	
	private final boolean passive;
	
	private final boolean implemented;
	
	/**
	 * Constructor for SkillLearn.
	 * @param id int
	 * @param price int
	 * @param replaceId int
	 * @param minLevel int
	 * @param classId int
	 * @param passive boolean
	 */
	public SkillLearn(int id, int price, int replaceId, int minLevel, int classId, boolean passive)
	{
		this.id = id;
		this.price = price;
		this.replaceId = replaceId;
		this.minLevel = minLevel;
		this.classId = classId;
		useId = id;
		replaceUseId = replaceId;
		this.passive = passive;
		final SkillTable skillTable = SkillTable.getInstance();
		
		if (skillTable.getSkill(classId, id) == null)
		{
			useId += 67108864;
		}
		
		if (skillTable.getSkill(classId, replaceUseId) == null)
		{
			replaceUseId += 67108864;
		}
		
		final SkillTemplate template = skillTable.getSkill(classId, id);
		implemented = (template != null) && template.isImplemented();
	}
	
	/**
	 * Method getClassId.
	 * @return int
	 */
	public int getClassId()
	{
		return classId;
	}
	
	/**
	 * Method getId.
	 * @return int
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Method getMinLevel.
	 * @return int
	 */
	public int getMinLevel()
	{
		return minLevel;
	}
	
	/**
	 * Method getPrice.
	 * @return int
	 */
	public int getPrice()
	{
		return price;
	}
	
	/**
	 * Method getReplaceId.
	 * @return int
	 */
	public int getReplaceId()
	{
		return replaceId;
	}
	
	/**
	 * Method getReplaceUseId.
	 * @return int
	 */
	public int getReplaceUseId()
	{
		return replaceUseId;
	}
	
	/**
	 * Method getUseId.
	 * @return int
	 */
	public int getUseId()
	{
		return useId;
	}
	
	/**
	 * Method isImplemented.
	 * @return boolean
	 */
	public final boolean isImplemented()
	{
		return implemented;
	}
	
	/**
	 * Method isPassive.
	 * @return boolean
	 */
	public final boolean isPassive()
	{
		return passive;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "SkillLearn  id = " + id + ", price = " + price + ", replaceId = " + replaceId + ", classId = " + classId + ", minLevel = " + minLevel;
	}
}

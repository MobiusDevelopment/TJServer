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

import tera.gameserver.model.items.CommonType;
import tera.gameserver.tables.SkillTable;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public final class CommonTemplate extends ItemTemplate
{
	
	protected SkillTemplate activeSkill;
	
	/**
	 * Constructor for CommonTemplate.
	 * @param type CommonType
	 * @param vars VarTable
	 */
	public CommonTemplate(CommonType type, VarTable vars)
	{
		super(type, vars);
		final SkillTable skillTable = SkillTable.getInstance();
		activeSkill = skillTable.getSkill(getClassIdItemSkill(), vars.getInteger("activeSkill", 0));
		slotType = type.getSlot();
	}
	
	/**
	 * Method getActiveSkill.
	 * @return SkillTemplate
	 */
	@Override
	public final SkillTemplate getActiveSkill()
	{
		return activeSkill;
	}
	
	/**
	 * Method getType.
	 * @return CommonType
	 */
	@Override
	public CommonType getType()
	{
		return (CommonType) type;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return super.toString() + " activeSkill = " + (activeSkill != null ? activeSkill.getId() : "null");
	}
}

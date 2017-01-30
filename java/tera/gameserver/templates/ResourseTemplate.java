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

import tera.gameserver.IdFactory;
import tera.gameserver.model.drop.ResourseDrop;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.resourse.ResourseType;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public final class ResourseTemplate
{
	private ResourseDrop drop;
	private final ResourseType type;
	private final int id;
	private final int level;
	private final int req;
	private final int exp;
	
	/**
	 * Constructor for ResourseTemplate.
	 * @param vars VarTable
	 */
	public ResourseTemplate(VarTable vars)
	{
		id = vars.getInteger("id");
		level = vars.getInteger("level");
		req = vars.getInteger("req");
		exp = vars.getInteger("exp", 0);
		type = vars.getEnum("type", ResourseType.class);
	}
	
	/**
	 * Method getDrop.
	 * @return ResourseDrop
	 */
	public final ResourseDrop getDrop()
	{
		return drop;
	}
	
	/**
	 * Method getExp.
	 * @return int
	 */
	public int getExp()
	{
		return exp;
	}
	
	/**
	 * Method getId.
	 * @return int
	 */
	public final int getId()
	{
		return id;
	}
	
	/**
	 * Method getLevel.
	 * @return int
	 */
	public final int getLevel()
	{
		return level;
	}
	
	/**
	 * Method getReq.
	 * @return int
	 */
	public final int getReq()
	{
		return req;
	}
	
	/**
	 * Method getType.
	 * @return ResourseType
	 */
	public ResourseType getType()
	{
		return type;
	}
	
	/**
	 * Method newInstance.
	 * @return ResourseInstance
	 */
	public ResourseInstance newInstance()
	{
		final IdFactory idFactory = IdFactory.getInstance();
		return newInstance(idFactory.getNextResourseId());
	}
	
	/**
	 * Method newInstance.
	 * @param objectId int
	 * @return ResourseInstance
	 */
	public ResourseInstance newInstance(int objectId)
	{
		final ResourseInstance resourse = type.newInstance(this);
		resourse.setObjectId(objectId);
		return resourse;
	}
	
	/**
	 * Method setDrop.
	 * @param drop ResourseDrop
	 */
	public final void setDrop(ResourseDrop drop)
	{
		this.drop = drop;
	}
}
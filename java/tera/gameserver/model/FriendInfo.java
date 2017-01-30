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

/**
 * @author Ronn
 */
public final class FriendInfo implements Foldable
{
	private String name;
	private int objectId;
	private int level;
	private int raceId;
	private int classId;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
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
	 * Method getLevel.
	 * @return int
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getObjectId.
	 * @return int
	 */
	public final int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Method getRaceId.
	 * @return int
	 */
	public int getRaceId()
	{
		return raceId;
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
	 * Method setClassId.
	 * @param classId int
	 */
	public void setClassId(int classId)
	{
		this.classId = classId;
	}
	
	/**
	 * Method setLevel.
	 * @param level int
	 */
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	/**
	 * Method setName.
	 * @param name String
	 */
	public final void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Method setObjectId.
	 * @param objectId int
	 */
	public final void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Method setRaceId.
	 * @param raceId int
	 */
	public void setRaceId(int raceId)
	{
		this.raceId = raceId;
	}
}
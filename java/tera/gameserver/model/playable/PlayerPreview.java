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
package tera.gameserver.model.playable;

import tera.gameserver.model.equipment.Equipment;

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 * @created 12.04.2012
 */
public final class PlayerPreview implements Comparable<PlayerPreview>, Foldable
{
	private static final FoldablePool<PlayerPreview> pool = Pools.newConcurrentFoldablePool(PlayerPreview.class);
	
	/**
	 * Method newInstance.
	 * @param objectId int
	 * @return PlayerPreview
	 */
	public static PlayerPreview newInstance(int objectId)
	{
		PlayerPreview preview = pool.take();
		
		if (preview == null)
		{
			preview = new PlayerPreview();
		}
		
		preview.objectId = objectId;
		return preview;
	}
	
	private int objectId;
	
	private int sex;
	
	private int raceId;
	
	private int classId;
	
	private int level;
	
	private long onlineTime;
	
	private String name;
	
	private PlayerAppearance appearance;
	
	private Equipment equipment;
	
	private int hp;
	
	private int mp;
	
	private int posX;
	private int posY;
	private int posZ;
	private int zoneId;
	private int continentId;
	
	/**
	 * Method compareTo.
	 * @param playerPreview PlayerPreview
	 * @return int
	 */
	@Override
	public int compareTo(PlayerPreview playerPreview)
	{
		if ((playerPreview == null) || (onlineTime < playerPreview.getOnlineTime()))
		{
			return -1;
		}
		
		if (onlineTime == playerPreview.getOnlineTime())
		{
			return 0;
		}
		
		return 1;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		if (equipment != null)
		{
			equipment.fold();
		}
		
		equipment = null;
		
		if (appearance != null)
		{
			appearance.fold();
		}
		
		appearance = null;
		name = null;
	}
	
	public void fold()
	{
		pool.put(this);
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
	 * Method getEquipment.
	 * @return Equipment
	 */
	public Equipment getEquipment()
	{
		return equipment;
	}
	
	/**
	 * Method getAppearance.
	 * @return PlayerAppearance
	 */
	public PlayerAppearance getAppearance()
	{
		return appearance;
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
	public String getName()
	{
		return name;
	}
	
	/**
	 * Method getObjectId.
	 * @return int
	 */
	public int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Method getOnlineTime.
	 * @return long
	 */
	public long getOnlineTime()
	{
		return onlineTime;
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
	 * Method getSex.
	 * @return int
	 */
	public int getSex()
	{
		return sex;
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
	 * Method setEquipment.
	 * @param equipment Equipment
	 * @return PlayerPreview
	 */
	public PlayerPreview setEquipment(Equipment equipment)
	{
		this.equipment = equipment;
		return this;
	}
	
	/**
	 * Method setAppearance.
	 * @param appearance PlayerAppearance
	 * @return PlayerPreview
	 */
	public PlayerPreview setAppearance(PlayerAppearance appearance)
	{
		this.appearance = appearance;
		return this;
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
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Method setObjectId.
	 * @param objectId int
	 */
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Method setOnlineTime.
	 * @param onlineTime long
	 */
	public void setOnlineTime(long onlineTime)
	{
		this.onlineTime = onlineTime;
	}
	
	/**
	 * Method setRaceId.
	 * @param raceId byte
	 */
	public void setRaceId(byte raceId)
	{
		this.raceId = raceId;
	}
	
	/**
	 * Method setSex.
	 * @param sex int
	 */
	public void setSex(int sex)
	{
		this.sex = sex;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "PlayerPreview objectId = " + objectId + ", level = " + level + ", name = " + name;
	}
	
	public void setHp(int hp)
	{
		this.hp = hp;
	}
	
	public void setMp(int mp)
	{
		this.mp = mp;
	}
	
	public void setPosX(int posX)
	{
		this.posX = posX;
	}
	
	public void setPosY(int posY)
	{
		this.posY = posY;
	}
	
	public void setPosZ(int posZ)
	{
		this.posZ = posZ;
	}
	
	public void setZoneId(int zoneId)
	{
		this.zoneId = zoneId;
	}
	
	public void setContinentId(int continentId)
	{
		this.continentId = continentId;
	}
	
	public int getHp()
	{
		return hp;
	}
	
	public int getMp()
	{
		return mp;
	}
	
	public int getPosX()
	{
		return posX;
	}
	
	public int getPosY()
	{
		return posY;
	}
	
	public int getPosZ()
	{
		return posZ;
	}
	
	public int getZoneId()
	{
		return zoneId;
	}
	
	public int getContinentId()
	{
		return continentId;
	}
}
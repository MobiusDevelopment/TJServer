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

import tera.gameserver.model.playable.Player;

import rlib.util.Strings;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class GuildMember implements Foldable
{
	private static final FoldablePool<GuildMember> pool = Pools.newConcurrentFoldablePool(GuildMember.class);
	
	/**
	 * Method newInstance.
	 * @return GuildMember
	 */
	public static GuildMember newInstance()
	{
		final GuildMember member = pool.take();
		
		if (member == null)
		{
			return new GuildMember();
		}
		
		return member;
	}
	
	private String name;
	
	private String note;
	
	private int objectId;
	
	private int level;
	
	private int sex;
	
	private int raceId;
	
	private int classId;
	
	private int zoneId;
	
	private int lastOnline;
	
	private GuildRank rank;
	
	private boolean online;
	
	public GuildMember()
	{
		note = Strings.EMPTY;
	}
	
	/**
	 * Method equals.
	 * @param object Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Player)
		{
			final Player player = (Player) object;
			return objectId == player.getObjectId();
		}
		
		return super.equals(object);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		rank = null;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getClassId.
	 * @return int
	 */
	public final int getClassId()
	{
		return classId;
	}
	
	/**
	 * Method getLastOnline.
	 * @return int
	 */
	public final int getLastOnline()
	{
		return lastOnline;
	}
	
	/**
	 * Method getLawId.
	 * @return int
	 */
	public final int getLawId()
	{
		return rank.getLawId();
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
	 * Method getName.
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getNote.
	 * @return String
	 */
	public final String getNote()
	{
		return note;
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
	public final int getRaceId()
	{
		return raceId;
	}
	
	/**
	 * Method getRank.
	 * @return GuildRank
	 */
	public final GuildRank getRank()
	{
		return rank;
	}
	
	/**
	 * Method getRankId.
	 * @return int
	 */
	public final int getRankId()
	{
		return rank == null ? 0 : rank.getIndex();
	}
	
	/**
	 * Method getSex.
	 * @return int
	 */
	public final int getSex()
	{
		return sex;
	}
	
	/**
	 * Method getZoneId.
	 * @return int
	 */
	public final int getZoneId()
	{
		return zoneId;
	}
	
	/**
	 * Method isOnline.
	 * @return boolean
	 */
	public final boolean isOnline()
	{
		return online;
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
	public final void setClassId(int classId)
	{
		this.classId = classId;
	}
	
	/**
	 * Method setLastOnline.
	 * @param lastOnline int
	 */
	public final void setLastOnline(int lastOnline)
	{
		this.lastOnline = lastOnline;
	}
	
	/**
	 * Method setLevel.
	 * @param level int
	 */
	public final void setLevel(int level)
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
	 * Method setNote.
	 * @param note String
	 */
	public final void setNote(String note)
	{
		this.note = note;
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
	 * Method setOnline.
	 * @param online boolean
	 */
	public final void setOnline(boolean online)
	{
		this.online = online;
	}
	
	/**
	 * Method setRaceId.
	 * @param raceId int
	 */
	public final void setRaceId(int raceId)
	{
		this.raceId = raceId;
	}
	
	/**
	 * Method setRank.
	 * @param rank GuildRank
	 */
	public final void setRank(GuildRank rank)
	{
		this.rank = rank;
	}
	
	/**
	 * Method setSex.
	 * @param sex int
	 */
	public final void setSex(int sex)
	{
		this.sex = sex;
	}
	
	/**
	 * Method setZoneId.
	 * @param zoneId int
	 */
	public final void setZoneId(int zoneId)
	{
		this.zoneId = zoneId;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "GuildMember  name = " + name + ", note = " + note + ", objectId = " + objectId + ", level = " + level + ", sex = " + sex + ", raceId = " + raceId + ", classId = " + classId + ", zoneId = " + zoneId + ", lastOnline = " + lastOnline + ", rank = " + rank + ", online = " + online;
	}
}

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
public class GuildRank implements Foldable
{
	private static final FoldablePool<GuildRank> pool = Pools.newConcurrentFoldablePool(GuildRank.class);
	
	public static final int GUILD_MASTER = 1;
	
	public static final int GUILD_MEMBER = 3;
	
	/**
	 * Method newInstance.
	 * @param name String
	 * @param law GuildRankLaw
	 * @param index int
	 * @return GuildRank
	 */
	public static GuildRank newInstance(String name, GuildRankLaw law, int index)
	{
		GuildRank rank = pool.take();
		
		if (rank == null)
		{
			rank = new GuildRank();
		}
		
		rank.name = name;
		rank.law = law;
		rank.index = index;
		rank.prepare();
		return rank;
	}
	
	private String name;
	
	private GuildRankLaw law;
	
	private int index;
	
	private boolean changeLineUp;
	
	private boolean accessBank;
	
	private boolean changeTitle;
	
	private boolean guildWars;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
		law = GuildRankLaw.MEMBER;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getIndex.
	 * @return int
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Method getLawId.
	 * @return int
	 */
	public int getLawId()
	{
		return law.ordinal();
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
	 * Method isAccessBank.
	 * @return boolean
	 */
	public boolean isAccessBank()
	{
		return accessBank;
	}
	
	/**
	 * Method isChangeLineUp.
	 * @return boolean
	 */
	public boolean isChangeLineUp()
	{
		return changeLineUp;
	}
	
	/**
	 * Method isChangeTitle.
	 * @return boolean
	 */
	public boolean isChangeTitle()
	{
		return changeTitle;
	}
	
	/**
	 * Method isGuildMaster.
	 * @return boolean
	 */
	public boolean isGuildMaster()
	{
		return law == GuildRankLaw.GUILD_MASTER;
	}
	
	/**
	 * Method isGuildWars.
	 * @return boolean
	 */
	public boolean isGuildWars()
	{
		return guildWars;
	}
	
	public void prepare()
	{
		changeLineUp = false;
		accessBank = false;
		changeTitle = false;
		guildWars = false;
		
		switch (law)
		{
			case BANK:
				accessBank = true;
				break;
			
			case BANK_TITLE:
			{
				accessBank = true;
				changeTitle = true;
				break;
			}
			
			case BANK_TITLE_GVG:
			{
				accessBank = true;
				changeTitle = true;
				guildWars = true;
				break;
			}
			
			case GUILD_MASTER:
			{
				accessBank = true;
				changeTitle = true;
				changeLineUp = true;
				guildWars = true;
				break;
			}
			
			case GVG:
				guildWars = true;
				break;
			
			case LINE_UP:
				changeLineUp = true;
				break;
			
			case LINE_UP_BANK:
			{
				changeLineUp = true;
				accessBank = true;
				break;
			}
			
			case LINE_UP_BANK_GVG:
			{
				changeLineUp = true;
				accessBank = true;
				guildWars = true;
				break;
			}
			
			case LINE_UP_BANK_TITLE:
			{
				changeLineUp = true;
				accessBank = true;
				changeTitle = true;
				break;
			}
			
			case LINE_UP_BANK_TITLE_GVG:
			{
				changeLineUp = true;
				accessBank = true;
				changeTitle = true;
				guildWars = true;
				break;
			}
			
			case LINE_UP_GVG:
			{
				changeLineUp = true;
				guildWars = true;
				break;
			}
			
			case LINE_UP_TITLE:
			{
				changeLineUp = true;
				changeTitle = true;
				break;
			}
			
			case LINE_UP_TITLE_GVG:
			{
				changeLineUp = true;
				changeTitle = true;
				guildWars = true;
				break;
			}
			
			case TITLE:
				changeTitle = true;
				break;
			
			case TITLE_GVG:
			{
				guildWars = true;
				changeTitle = true;
			}
			
			default:
				break;
		}
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
	 * Method setLaw.
	 * @param law GuildRankLaw
	 */
	public void setLaw(GuildRankLaw law)
	{
		this.law = law;
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
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "GuildRank  name = " + name + ", law = " + law + ", index = " + index + ", changeLineUp = " + changeLineUp + ", accessBank = " + accessBank + ", changeTitle = " + changeTitle + ", guildWars = " + guildWars;
	}
}

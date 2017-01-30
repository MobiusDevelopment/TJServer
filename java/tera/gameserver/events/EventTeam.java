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
package tera.gameserver.events;

import tera.gameserver.model.playable.Player;

import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public class EventTeam implements Foldable
{
	private static final FoldablePool<EventTeam> pool = Pools.newConcurrentFoldablePool(EventTeam.class);
	
	/**
	 * Method newInstance.
	 * @return EventTeam
	 */
	public static EventTeam newInstance()
	{
		EventTeam team = pool.take();
		
		if (team == null)
		{
			team = new EventTeam();
		}
		
		return team;
	}
	
	private final Array<EventPlayer> players;
	private String name;
	private int level;
	
	private EventTeam()
	{
		name = Strings.EMPTY;
		players = Arrays.toConcurrentArray(EventPlayer.class);
	}
	
	/**
	 * Method addPlayer.
	 * @param eventPlayer EventPlayer
	 */
	public final void addPlayer(EventPlayer eventPlayer)
	{
		players.add(eventPlayer);
		
		if (players.isEmpty())
		{
			setName(Strings.EMPTY);
		}
		
		players.readLock();
		
		try
		{
			final EventPlayer[] array = players.array();
			final StringBuilder builder = new StringBuilder("{");
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				final Player player = array[i].getPlayer();
				builder.append(player.getName());
				
				if (i != (length - 1))
				{
					builder.append('-');
				}
			}
			
			builder.append('}');
			setName(builder.toString());
		}
		
		finally
		{
			players.readUnlock();
		}
	}
	
	public void decreaseLevel()
	{
		level--;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		if (!players.isEmpty())
		{
			final EventPlayer[] array = players.array();
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				array[i].fold();
			}
			
			players.clear();
		}
	}
	
	public void fold()
	{
		pool.put(this);
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
	 * Method getPlayers.
	 * @return EventPlayer[]
	 */
	public final EventPlayer[] getPlayers()
	{
		return players.array();
	}
	
	public void increaseLevel()
	{
		level++;
	}
	
	/**
	 * Method isDead.
	 * @return boolean
	 */
	public final boolean isDead()
	{
		if (players.isEmpty())
		{
			return true;
		}
		
		players.readLock();
		
		try
		{
			final EventPlayer[] array = players.array();
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				final Player player = array[i].getPlayer();
				
				if (!player.isDead())
				{
					return false;
				}
			}
			
			return true;
		}
		
		finally
		{
			players.readUnlock();
		}
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		level = 0;
	}
	
	/**
	 * Method removePlayer.
	 * @param player EventPlayer
	 */
	public void removePlayer(EventPlayer player)
	{
		players.fastRemove(player);
	}
	
	/**
	 * Method setName.
	 * @param string String
	 */
	public final void setName(String string)
	{
		name = string;
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	public final int size()
	{
		return players.size();
	}
}
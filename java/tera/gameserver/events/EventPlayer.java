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
import tera.util.Location;

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class EventPlayer implements Foldable
{
	public static final FoldablePool<EventPlayer> pool = Pools.newConcurrentFoldablePool(EventPlayer.class);
	
	/**
	 * Method newInstance.
	 * @param player Player
	 * @return EventPlayer
	 */
	public static EventPlayer newInstance(Player player)
	{
		EventPlayer eventPlayer = pool.take();
		
		if (eventPlayer == null)
		{
			eventPlayer = new EventPlayer();
		}
		
		eventPlayer.player = player;
		return eventPlayer;
	}
	
	private Player player;
	
	private final Location saveLoc;
	
	private int mp;
	private int hp;
	private int stamina;
	
	private int counter;
	
	private EventPlayer()
	{
		saveLoc = new Location();
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getPlayer.
	 * @return Player
	 */
	public final Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		mp = 0;
		hp = 0;
		stamina = 0;
		counter = 0;
	}
	
	/**
	 * Method getCounter.
	 * @return int
	 */
	public int getCounter()
	{
		return counter;
	}
	
	public void incrementCounter()
	{
		counter += 1;
	}
	
	public void restoreLoc()
	{
		player.teleToLocation(saveLoc);
	}
	
	public void restoreState()
	{
		player.setCurrentHp(hp);
		player.setCurrentMp(mp);
		player.setStamina(stamina);
		player.updateInfo();
	}
	
	public void saveLoc()
	{
		saveLoc.setXYZ(player.getX(), player.getY(), player.getZ());
		saveLoc.setContinentId(player.getContinentId());
	}
	
	public void saveState()
	{
		hp = player.getCurrentHp();
		mp = player.getCurrentMp();
		stamina = player.getStamina();
	}
}

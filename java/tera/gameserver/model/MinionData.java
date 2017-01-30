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

import tera.gameserver.model.npc.Minion;
import tera.gameserver.model.npc.MinionLeader;
import tera.gameserver.model.npc.spawn.MinionSpawn;
import tera.gameserver.model.npc.spawn.Spawn;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 14.03.2012
 */
public final class MinionData
{
	public MinionSpawn[] minions;
	private final int leaderId;
	private final int type;
	private int respawnDelay;
	private int total;
	
	/**
	 * Constructor for MinionData.
	 * @param minions MinionSpawn[]
	 * @param leaderId int
	 * @param type int
	 * @param respawnDelay int
	 */
	public MinionData(MinionSpawn[] minions, int leaderId, int type, int respawnDelay)
	{
		this.minions = minions;
		this.leaderId = leaderId;
		this.type = type;
		this.respawnDelay = respawnDelay;
		
		for (MinionSpawn info : minions)
		{
			total += info.getCount();
		}
	}
	
	/**
	 * Method containsMinion.
	 * @param spawn Spawn
	 * @param list Array<Minion>
	 * @return boolean
	 */
	public boolean containsMinion(Spawn spawn, Array<Minion> list)
	{
		return Arrays.contains(minions, spawn) && (list.size() < total);
	}
	
	/**
	 * Method getLeaderId.
	 * @return int
	 */
	public final int getLeaderId()
	{
		return leaderId;
	}
	
	/**
	 * Method getMinions.
	 * @return MinionSpawn[]
	 */
	public MinionSpawn[] getMinions()
	{
		return minions;
	}
	
	/**
	 * Method getRespawnDelay.
	 * @return int
	 */
	public final int getRespawnDelay()
	{
		return respawnDelay;
	}
	
	/**
	 * Method getType.
	 * @return int
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Method setRespawnDelay.
	 * @param respawnDelay int
	 */
	public final void setRespawnDelay(int respawnDelay)
	{
		this.respawnDelay = respawnDelay;
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	public final int size()
	{
		int counter = 0;
		final MinionSpawn[] minions = getMinions();
		
		for (MinionSpawn minion : minions)
		{
			counter += minion.getCount();
		}
		
		return counter;
	}
	
	/**
	 * Method spawnMinions.
	 * @param leader MinionLeader
	 * @return Array<Minion>
	 */
	public Array<Minion> spawnMinions(MinionLeader leader)
	{
		final Array<Minion> array = Arrays.toConcurrentArray(Minion.class, size());
		return spawnMinions(leader, array);
	}
	
	/**
	 * Method spawnMinions.
	 * @param leader MinionLeader
	 * @param array Array<Minion>
	 * @return Array<Minion>
	 */
	public Array<Minion> spawnMinions(MinionLeader leader, Array<Minion> array)
	{
		final MinionSpawn[] minions = getMinions();
		
		for (MinionSpawn minion : minions)
		{
			minion.start(leader, array);
		}
		
		return array;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "MinionData leaderId = " + leaderId + ", respawnDelay = " + respawnDelay;
	}
}
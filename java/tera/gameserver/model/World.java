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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import tera.Config;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.CharSay;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.util.Location;

import rlib.concurrent.Locks;
import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public abstract class World
{
	public static final int MAP_MIN_X = -294912;
	public static final int MAP_MAX_X = 229375;
	public static final int MAP_MIN_Y = -229376;
	public static final int MAP_MAX_Y = 294911;
	public static final int MAP_MIN_Z = -32768;
	public static final int MAP_MAX_Z = 32767;
	public static final int WORLD_SIZE_X = ((MAP_MAX_X - MAP_MIN_X) + 1) / 32768;
	public static final int WORLD_SIZE_Y = ((MAP_MAX_Y - MAP_MIN_Y) + 1) / 32768;
	public static final int REGION_WIDTH = Config.WORLD_WIDTH_REGION;
	public static final int REGION_HEIGHT = Config.WORLD_HEIGHT_REGION;
	public static final int OFFSET_X = Math.abs(MAP_MIN_X / REGION_WIDTH);
	public static final int OFFSET_Y = Math.abs(MAP_MIN_Y / REGION_WIDTH);
	public static final int OFFSET_Z = Math.abs(MAP_MIN_Z / REGION_HEIGHT);
	private static final int REGIONS_X = (MAP_MAX_X / REGION_WIDTH) + OFFSET_X;
	private static final int REGIONS_Y = (MAP_MAX_Y / REGION_WIDTH) + OFFSET_Y;
	private static final int REGIONS_Z = (MAP_MAX_Z / REGION_HEIGHT) + OFFSET_Z;
	private static final WorldRegion[][][][] worldRegions = new WorldRegion[Config.WORLD_CONTINENT_COUNT][REGIONS_X + 1][REGIONS_Y + 1][REGIONS_Z + 1];
	private static final Lock lock = Locks.newLock();
	private static final Array<WorldRegion> activeRegions = Arrays.toConcurrentArray(WorldRegion.class);
	private static final Map<String, Player> playerNames = new HashMap<>();
	private static final Array<Player> players = Arrays.toConcurrentArray(Player.class);
	private static final Table<IntKey, Player> playerTable = Tables.newIntegerTable();
	private static volatile long droppedItems;
	private static volatile long spawnedNpcs;
	private static volatile long killedNpcs;
	private static volatile long killedPlayers;
	
	/**
	 * Method addActiveRegion.
	 * @param region WorldRegion
	 */
	public static void addActiveRegion(WorldRegion region)
	{
		activeRegions.add(region);
	}
	
	public static void addDroppedItems()
	{
		droppedItems += 1;
	}
	
	public static void addKilledNpc()
	{
		killedNpcs += 1;
	}
	
	public static void addKilledPlayers()
	{
		killedPlayers += 1;
	}
	
	/**
	 * Method addNewPlayer.
	 * @param player Player
	 */
	public static void addNewPlayer(Player player)
	{
		players.writeLock();
		
		try
		{
			final Player[] array = players.array();
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				final Player target = array[i];
				final FriendList friendList = target.getFriendList();
				
				if (friendList.size() < 1)
				{
					continue;
				}
				
				friendList.onEnterGame(player);
			}
			
			playerNames.put(player.getName(), player);
			players.add(player);
			playerTable.put(player.getObjectId(), player);
		}
		
		finally
		{
			players.writeUnlock();
		}
	}
	
	public static void addSpawnedNpc()
	{
		spawnedNpcs += 1;
	}
	
	/**
	 * Method addVisibleObject.
	 * @param object TObject
	 */
	public static void addVisibleObject(TObject object)
	{
		if ((object == null) || !object.isVisible())
		{
			return;
		}
		
		final WorldRegion region = getRegion(object);
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if (region == null)
		{
			object.setXYZ(0, 0, 0);
			return;
		}
		
		region.activateTrap(object);
		
		if ((currentRegion != null) && (currentRegion == region))
		{
			return;
		}
		
		region.addObject(object);
		object.setCurrentRegion(region);
		
		if (currentRegion == null)
		{
			final WorldRegion[] newNeighbors = region.getNeighbors();
			
			for (WorldRegion newNeighbor : newNeighbors)
			{
				newNeighbor.addToPlayers(object);
			}
		}
		else
		{
			final WorldRegion[] oldNeighbors = currentRegion.getNeighbors();
			final WorldRegion[] newNeighbors = region.getNeighbors();
			
			for (WorldRegion oldNeighbor : oldNeighbors)
			{
				final WorldRegion neighbor = oldNeighbor;
				
				if (!Arrays.contains(newNeighbors, neighbor))
				{
					neighbor.removeFromPlayers(object, DeleteCharacter.DISAPPEARS);
				}
			}
			
			for (WorldRegion newNeighbor : newNeighbors)
			{
				final WorldRegion neighbor = newNeighbor;
				
				if (!Arrays.contains(oldNeighbors, neighbor))
				{
					neighbor.addToPlayers(object);
				}
			}
			
			currentRegion.removeObject(object);
		}
	}
	
	public static void clear()
	{
		for (WorldRegion[][][] regionsss : worldRegions)
		{
			for (WorldRegion[][] regionss : regionsss)
			{
				for (WorldRegion[] regions : regionss)
				{
					Arrays.clear(regions);
				}
			}
		}
		
		activeRegions.clear();
		playerNames.clear();
	}
	
	/**
	 * Method containsPlayer.
	 * @param name String
	 * @return boolean
	 */
	public static boolean containsPlayer(String name)
	{
		players.readLock();
		
		try
		{
			return playerNames.containsKey(name);
		}
		
		finally
		{
			players.readUnlock();
		}
	}
	
	/**
	 * Method getActiveRegions.
	 * @return Array<WorldRegion>
	 */
	public static Array<WorldRegion> getActiveRegions()
	{
		return activeRegions;
	}
	
	/**
	 * Method getAround.
	 * @param <T>
	 * @param <V>
	 * @param array Array<T>
	 * @param type Class<V>
	 * @param object TObject
	 * @return Array<T>
	 */
	public static <T extends TObject, V extends T> Array<T> getAround(Array<T> array, Class<V> type, TObject object)
	{
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if ((currentRegion == null) || !currentRegion.isActive())
		{
			return array;
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		final int objectId = object.getObjectId();
		final int subId = object.getSubId();
		
		for (WorldRegion region : regions)
		{
			region.addObject(array, type, objectId, subId);
		}
		
		return array;
	}
	
	/**
	 * Method getAround.
	 * @param <T>
	 * @param type Class<T>
	 * @param array Array<T>
	 * @param object TObject
	 * @return Array<T>
	 */
	public static <T extends TObject> Array<T> getAround(Class<T> type, Array<T> array, TObject object)
	{
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if (currentRegion == null)
		{
			return array;
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		final int objectId = object.getObjectId();
		final int subId = object.getSubId();
		
		for (WorldRegion region : regions)
		{
			region.addObject(array, type, objectId, subId);
		}
		
		return array;
	}
	
	/**
	 * Method getAround.
	 * @param <T>
	 * @param type Class<T>
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param objectId int
	 * @param subId int
	 * @param radius float
	 * @return Array<T>
	 */
	public static <T extends TObject> Array<T> getAround(Class<T> type, int continentId, float x, float y, float z, int objectId, int subId, float radius)
	{
		final WorldRegion[] regions = getRegion(continentId, x, y, z).getNeighbors();
		final Array<T> array = Arrays.toArray(type);
		
		for (WorldRegion region : regions)
		{
			region.addObjects(array, type, objectId, subId, x, y, z, radius);
		}
		
		return array;
	}
	
	/**
	 * Method getAround.
	 * @param <T>
	 * @param type Class<T>
	 * @param loc Location
	 * @param objectId int
	 * @param subId int
	 * @param radius float
	 * @return Array<T>
	 */
	public static <T extends TObject> Array<T> getAround(Class<T> type, Location loc, int objectId, int subId, float radius)
	{
		return getAround(type, loc.getContinentId(), loc.getX(), loc.getY(), loc.getZ(), objectId, subId, radius);
	}
	
	/**
	 * Method getAround.
	 * @param <T>
	 * @param type Class<T>
	 * @param object TObject
	 * @return Array<T>
	 */
	public static <T extends TObject> Array<T> getAround(Class<T> type, TObject object)
	{
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if (currentRegion == null)
		{
			return Arrays.toArray(type, 0);
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		final Array<T> array = Arrays.toArray(type);
		final int objectId = object.getObjectId();
		final int subId = object.getSubId();
		
		for (WorldRegion region : regions)
		{
			region.addObject(array, type, objectId, subId);
		}
		
		return array;
	}
	
	/**
	 * Method getAround.
	 * @param <T>
	 * @param type Class<T>
	 * @param object TObject
	 * @param radius float
	 * @return Array<T>
	 */
	public static <T extends TObject> Array<T> getAround(Class<T> type, TObject object, float radius)
	{
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if (currentRegion == null)
		{
			return Arrays.toArray(type, 0);
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		final Array<T> array = Arrays.toArray(type);
		final int objectId = object.getObjectId();
		final int subId = object.getSubId();
		
		for (WorldRegion region : regions)
		{
			region.addObjects(array, type, objectId, subId, object.getX(), object.getY(), object.getZ(), radius);
		}
		
		return array;
	}
	
	/**
	 * Method getAroundCount.
	 * @param <T>
	 * @param type Class<T>
	 * @param object TObject
	 * @param radius float
	 * @return int
	 */
	public static <T extends TObject> int getAroundCount(Class<T> type, TObject object, float radius)
	{
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if (currentRegion == null)
		{
			return 0;
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		final int objectId = object.getObjectId();
		final int subId = object.getSubId();
		int counter = 0;
		
		for (WorldRegion region : regions)
		{
			counter += region.getObjectCount(type, objectId, subId, object.getX(), object.getY(), object.getZ(), radius);
		}
		
		return counter;
	}
	
	/**
	 * Method getAround.
	 * @param <T>
	 * @param <V>
	 * @param type Class<V>
	 * @param array Array<T>
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param objectId int
	 * @param subId int
	 * @param radius float
	 * @return Array<T>
	 */
	public static <T extends TObject, V extends T> Array<T> getAround(Class<V> type, Array<T> array, int continentId, float x, float y, float z, int objectId, int subId, float radius)
	{
		final WorldRegion curreentRegion = getRegion(continentId, x, y, z);
		
		if (curreentRegion == null)
		{
			return array;
		}
		
		final WorldRegion[] regions = curreentRegion.getNeighbors();
		
		for (WorldRegion region : regions)
		{
			region.addObjects(array, type, objectId, subId, x, y, z, radius);
		}
		
		return array;
	}
	
	/**
	 * Method getAround.
	 * @param <T>
	 * @param <V>
	 * @param type Class<V>
	 * @param array Array<T>
	 * @param object TObject
	 * @param radius float
	 * @return Array<T>
	 */
	public static <T extends TObject, V extends T> Array<T> getAround(Class<V> type, Array<T> array, TObject object, float radius)
	{
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if (currentRegion == null)
		{
			return array;
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		final int objectId = object.getObjectId();
		final int subId = object.getSubId();
		
		for (WorldRegion region : regions)
		{
			region.addObjects(array, type, objectId, subId, object.getX(), object.getY(), object.getZ(), radius);
		}
		
		return array;
	}
	
	/**
	 * Method getAroundBarriers.
	 * @param array Array<Character>
	 * @param caster Character
	 * @param distance float
	 */
	public static void getAroundBarriers(Array<Character> array, Character caster, float distance)
	{
		final WorldRegion currentRegion = caster.getCurrentRegion();
		
		if (currentRegion == null)
		{
			return;
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		final float x = caster.getX();
		final float y = caster.getY();
		final float z = caster.getZ();
		
		for (WorldRegion region : regions)
		{
			region.addBarriers(array, caster, x, y, z, distance);
		}
	}
	
	/**
	 * Method getAroundById.
	 * @param <T>
	 * @param type Class<T>
	 * @param object TObject
	 * @param targetId int
	 * @param targetSubId int
	 * @return T
	 */
	public static <T extends TObject> T getAroundById(Class<T> type, TObject object, int targetId, int targetSubId)
	{
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if ((currentRegion == null) || !currentRegion.isActive())
		{
			return null;
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		
		for (WorldRegion region : regions)
		{
			final TObject target = region.getObject(targetId, targetSubId);
			
			if ((target != null) && type.isInstance(target))
			{
				return type.cast(target);
			}
		}
		
		return null;
	}
	
	/**
	 * Method getAroundByName.
	 * @param <T>
	 * @param type Class<T>
	 * @param object TObject
	 * @param name String
	 * @return T
	 */
	public static <T extends TObject> T getAroundByName(Class<T> type, TObject object, String name)
	{
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if ((currentRegion == null) || !currentRegion.isActive())
		{
			return null;
		}
		
		final WorldRegion[] regions = currentRegion.getNeighbors();
		
		for (WorldRegion region : regions)
		{
			final TObject target = region.getObject(name);
			
			if ((target != null) && type.isInstance(target))
			{
				return type.cast(target);
			}
		}
		
		return null;
	}
	
	/**
	 * Method getDroppedItems.
	 * @return long
	 */
	public static long getDroppedItems()
	{
		return droppedItems;
	}
	
	/**
	 * Method getKilledNpcs.
	 * @return long
	 */
	public static long getKilledNpcs()
	{
		return killedNpcs;
	}
	
	/**
	 * Method getKilledPlayers.
	 * @return long
	 */
	public static long getKilledPlayers()
	{
		return killedPlayers;
	}
	
	/**
	 * Method getNeighbors.
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param minZ float
	 * @param maxZ float
	 * @return Array<WorldRegion>
	 */
	public static Array<WorldRegion> getNeighbors(int continentId, float x, float y, float minZ, float maxZ)
	{
		final Array<WorldRegion> array = Arrays.toArray(WorldRegion.class, 27);
		final int newX = (int) ((x / REGION_WIDTH) + OFFSET_X);
		final int newY = (int) ((y / REGION_WIDTH) + OFFSET_Y);
		final int newMinZ = (int) ((minZ / REGION_HEIGHT) + OFFSET_Z);
		final int newMaxZ = (int) ((maxZ / REGION_HEIGHT) + OFFSET_Z);
		
		for (int a = -1; a <= 1; a++)
		{
			for (int b = -1; b <= 1; b++)
			{
				for (int c = newMinZ; c <= newMaxZ; c++)
				{
					if (validRegion(newX + a, newY + b, c))
					{
						if (worldRegions[continentId][newX + a][newY + b][c] == null)
						{
							lock.lock();
							
							try
							{
								if (worldRegions[continentId][newX + a][newY + b][c] == null)
								{
									worldRegions[continentId][newX + a][newY + b][c] = new WorldRegion(continentId, newX + a, newY + b, c);
								}
							}
							
							finally
							{
								lock.unlock();
							}
						}
						
						array.add(worldRegions[continentId][newX + a][newY + b][c]);
					}
				}
			}
		}
		
		return array;
	}
	
	/**
	 * Method getNeighbors.
	 * @param continentId int
	 * @param x int
	 * @param y int
	 * @param z int
	 * @return WorldRegion[]
	 */
	public static WorldRegion[] getNeighbors(int continentId, int x, int y, int z)
	{
		final Array<WorldRegion> array = Arrays.toArray(WorldRegion.class, 27);
		
		for (int a = -1; a <= 1; a++)
		{
			for (int b = -1; b <= 1; b++)
			{
				for (int c = -1; c <= 1; c++)
				{
					if (validRegion(x + a, y + b, z + c))
					{
						if (worldRegions[continentId][x + a][y + b][z + c] == null)
						{
							lock.lock();
							
							try
							{
								if (worldRegions[continentId][x + a][y + b][z + c] == null)
								{
									worldRegions[continentId][x + a][y + b][z + c] = new WorldRegion(continentId, x + a, y + b, z + c);
								}
							}
							
							finally
							{
								lock.unlock();
							}
						}
						
						array.add(worldRegions[continentId][x + a][y + b][z + c]);
					}
				}
			}
		}
		
		array.trimToSize();
		return array.array();
	}
	
	/**
	 * Method getPlayer.
	 * @param objectId int
	 * @return Player
	 */
	public static Player getPlayer(int objectId)
	{
		players.readLock();
		
		try
		{
			return playerTable.get(objectId);
		}
		
		finally
		{
			players.readUnlock();
		}
	}
	
	/**
	 * Method getPlayer.
	 * @param name String
	 * @return Player
	 */
	public static Player getPlayer(String name)
	{
		players.readLock();
		
		try
		{
			return playerNames.get(name);
		}
		
		finally
		{
			players.readUnlock();
		}
	}
	
	/**
	 * Method getPlayers.
	 * @return Array<Player>
	 */
	public static Array<Player> getPlayers()
	{
		return players;
	}
	
	/**
	 * Method getRegion.
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @return WorldRegion
	 */
	public static WorldRegion getRegion(int continentId, float x, float y, float z)
	{
		if (continentId > 2)
		{
			return null;
		}
		
		final int newX = ((int) x / REGION_WIDTH) + OFFSET_X;
		final int newY = ((int) y / REGION_WIDTH) + OFFSET_Y;
		final int newZ = ((int) z / REGION_HEIGHT) + OFFSET_Z;
		
		if (validRegion(newX, newY, newZ))
		{
			WorldRegion region = worldRegions[continentId][newX][newY][newZ];
			
			if (region == null)
			{
				lock.lock();
				
				try
				{
					region = worldRegions[continentId][newX][newY][newZ];
					
					if (region == null)
					{
						region = new WorldRegion(continentId, newX, newY, newZ);
						worldRegions[continentId][newX][newY][newZ] = region;
					}
				}
				
				finally
				{
					lock.unlock();
				}
			}
			
			return region;
		}
		
		return null;
	}
	
	/**
	 * Method getRegion.
	 * @param continentId int
	 * @param x int
	 * @param y int
	 * @param z int
	 * @return WorldRegion
	 */
	public static WorldRegion getRegion(int continentId, int x, int y, int z)
	{
		final int newX = (x / REGION_WIDTH) + OFFSET_X;
		final int newY = (y / REGION_WIDTH) + OFFSET_Y;
		final int newZ = (z / REGION_HEIGHT) + OFFSET_Z;
		
		if (validRegion(newX, newY, newZ))
		{
			WorldRegion region = worldRegions[continentId][newX][newY][newZ];
			
			if (region == null)
			{
				lock.lock();
				
				try
				{
					region = worldRegions[continentId][newX][newY][newZ];
					
					if (region == null)
					{
						region = new WorldRegion(continentId, newX, newY, newZ);
						worldRegions[continentId][newX][newY][newZ] = region;
					}
				}
				
				finally
				{
					lock.unlock();
				}
			}
			
			return region;
		}
		
		return null;
	}
	
	/**
	 * Method getRegion.
	 * @param location Location
	 * @return WorldRegion
	 */
	public static WorldRegion getRegion(Location location)
	{
		return getRegion(location.getContinentId(), location.getX(), location.getY(), location.getZ());
	}
	
	/**
	 * Method getRegion.
	 * @param object TObject
	 * @return WorldRegion
	 */
	public static WorldRegion getRegion(TObject object)
	{
		return getRegion(object.getContinentId(), object.getX(), object.getY(), object.getZ());
	}
	
	/**
	 * Method getRegions.
	 * @return WorldRegion[][][][]
	 */
	public static WorldRegion[][][][] getRegions()
	{
		return worldRegions;
	}
	
	/**
	 * Method getRegionsCount.
	 * @param active boolean
	 * @return long
	 */
	public static long getRegionsCount(boolean active)
	{
		long counter = 0;
		
		for (WorldRegion[][][] first : worldRegions)
		{
			if (first == null)
			{
				continue;
			}
			
			for (WorldRegion[][] second : first)
			{
				if (second == null)
				{
					continue;
				}
				
				for (WorldRegion[] thrid : second)
				{
					if (thrid == null)
					{
						continue;
					}
					
					for (WorldRegion four : thrid)
					{
						if (four == null)
						{
							continue;
						}
						
						if (four.isActive() != active)
						{
							continue;
						}
						
						counter++;
					}
				}
			}
		}
		
		return counter;
	}
	
	/**
	 * Method getSpawnedNpcs.
	 * @return long
	 */
	public static long getSpawnedNpcs()
	{
		return spawnedNpcs;
	}
	
	/**
	 * Method online.
	 * @return int
	 */
	public static int online()
	{
		return playerNames.size();
	}
	
	/**
	 * Method region.
	 * @param continentId int
	 * @param i int
	 * @param j int
	 * @param k int
	 * @return WorldRegion
	 */
	public static WorldRegion region(int continentId, int i, int j, int k)
	{
		WorldRegion region = worldRegions[continentId][i][j][k];
		
		if (region == null)
		{
			lock.lock();
			
			try
			{
				region = worldRegions[continentId][i][j][k];
				
				if (region == null)
				{
					region = new WorldRegion(continentId, i, j, k);
					worldRegions[continentId][i][j][k] = region;
				}
			}
			
			finally
			{
				lock.unlock();
			}
		}
		
		return region;
	}
	
	/**
	 * Method removeActiveRegion.
	 * @param region WorldRegion
	 */
	public static void removeActiveRegion(WorldRegion region)
	{
		activeRegions.fastRemove(region);
	}
	
	/**
	 * Method removeOldPlayer.
	 * @param player Player
	 */
	public static void removeOldPlayer(Player player)
	{
		players.writeLock();
		
		try
		{
			playerNames.remove(player.getName());
			players.fastRemove(player);
			playerTable.remove(player.getObjectId());
			final Player[] array = players.array();
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				final Player target = array[i];
				final FriendList friendList = target.getFriendList();
				
				if (friendList.size() < 1)
				{
					continue;
				}
				
				friendList.onExitGame(player);
			}
		}
		
		finally
		{
			players.writeUnlock();
		}
	}
	
	/**
	 * Method removeVisibleObject.
	 * @param object TObject
	 * @param type int
	 */
	public static void removeVisibleObject(TObject object, int type)
	{
		if ((object == null) || object.isVisible())
		{
			return;
		}
		
		final WorldRegion currentRegion = object.getCurrentRegion();
		
		if (currentRegion != null)
		{
			currentRegion.removeObject(object);
			
			for (WorldRegion neighbor : currentRegion.getNeighbors())
			{
				neighbor.removeFromPlayers(object, type);
			}
			
			object.setCurrentRegion(null);
		}
	}
	
	/**
	 * Method sendAnnounce.
	 * @param text String
	 */
	public static void sendAnnounce(String text)
	{
		final CharSay packet = CharSay.getInstance(Strings.EMPTY, text, SayType.NOTICE_CHAT, 0, 0);
		players.readLock();
		
		try
		{
			final Player[] array = players.array();
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				packet.increaseSends();
			}
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				array[i].sendPacket(packet, false);
			}
		}
		
		finally
		{
			players.readUnlock();
		}
	}
	
	/**
	 * Method validRegion.
	 * @param x int
	 * @param y int
	 * @param z int
	 * @return boolean
	 */
	public static boolean validRegion(int x, int y, int z)
	{
		return (x >= 0) && (x < REGIONS_X) && (y >= 0) && (y < REGIONS_Y) && (z >= 0) && (z < REGIONS_Z);
	}
}
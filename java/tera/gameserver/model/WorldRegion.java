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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import tera.gameserver.model.ai.CharacterAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.territory.Territory;
import tera.gameserver.model.traps.Trap;
import tera.gameserver.network.serverpackets.ServerPacket;

import rlib.concurrent.Locks;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 24.02.2012
 */
public final class WorldRegion
{
	private final Lock writeLock;
	private final Lock readLock;
	private final int tileX;
	private final int tileY;
	private final int tileZ;
	private final int continentId;
	private volatile TObject[] objects;
	private volatile WorldRegion[] neighbors;
	private volatile int ordinal;
	private volatile int sizeAll;
	private volatile int sizeNpcs;
	private volatile int sizePlayers;
	private volatile boolean active;
	private Territory[] territories;
	private WorldZone[] zones;
	private final Array<Trap> traps;
	
	/**
	 * Constructor for WorldRegion.
	 * @param continentId int
	 * @param tileX int
	 * @param tileY int
	 * @param tileZ int
	 */
	public WorldRegion(int continentId, int tileX, int tileY, int tileZ)
	{
		this.continentId = continentId;
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileZ = tileZ;
		sizeAll = 0;
		sizeNpcs = 0;
		sizePlayers = 0;
		ordinal = 0;
		active = false;
		traps = Arrays.toArray(Trap.class);
		final ReadWriteLock readWriteLock = Locks.newRWLock();
		writeLock = readWriteLock.writeLock();
		readLock = readWriteLock.readLock();
	}
	
	/**
	 * Method activateTrap.
	 * @param object TObject
	 */
	public void activateTrap(TObject object)
	{
		if (!object.isCharacter() || traps.isEmpty())
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final Trap[] array = traps.array();
			
			for (int i = 0, length = traps.size(); i < length; i++)
			{
				array[i].activate(object);
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method addBarriers.
	 * @param array Array<Character>
	 * @param caster Character
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param distance float
	 */
	public void addBarriers(Array<Character> array, Character caster, float x, float y, float z, float distance)
	{
		if ((objects == null) || (sizeAll == 0))
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final Character target = objects[i].getCharacter();
				
				if ((target == null) || (target == caster) || (target.getGeomDistance(x, y) > distance) || (Math.abs(z - target.getZ()) > 90F))
				{
					continue;
				}
				
				array.add(target);
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method addObject.
	 * @param <V>
	 * @param <T>
	 * @param container Array<T>
	 * @param type Class<V>
	 * @param exclude int
	 * @param subId int
	 * @return Array<T>
	 */
	public <V extends T, T extends TObject> Array<T> addObject(Array<T> container, Class<V> type, int exclude, int subId)
	{
		if ((objects == null) || (sizeAll == 0))
		{
			return container;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final TObject object = objects[i];
				
				if (!((object.getObjectId() == exclude) && (object.getSubId() == subId)) && type.isInstance(object))
				{
					container.add(type.cast(object));
				}
			}
			
			return container;
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method addObject.
	 * @param object TObject
	 */
	public void addObject(TObject object)
	{
		if (object == null)
		{
			return;
		}
		
		boolean changeStatus = false;
		writeLock.lock();
		
		try
		{
			if (object.isTrap())
			{
				traps.add(object.getTrap());
			}
			
			if (objects == null)
			{
				objects = new TObject[10];
			}
			
			objects[ordinal] = object;
			ordinal += 1;
			
			if (ordinal == objects.length)
			{
				objects = Arrays.copyOf(objects, 10);
			}
			
			final int oldPlayer = sizePlayers;
			
			if (object.isPlayer())
			{
				sizePlayers += 1;
			}
			else if (object.isNpc())
			{
				sizeNpcs += 1;
			}
			
			sizeAll += 1;
			changeStatus = (oldPlayer < 1) && (sizePlayers > 0);
		}
		
		finally
		{
			writeLock.unlock();
		}
		
		if (changeStatus)
		{
			changeStatus();
		}
	}
	
	/**
	 * Method addObjects.
	 * @param <T>
	 * @param container Array<T>
	 * @param type Class<T>
	 */
	public <T extends TObject> void addObjects(Array<T> container, Class<T> type)
	{
		if ((objects == null) || (sizeAll == 0))
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final TObject object = objects[i];
				
				if (!type.isInstance(object))
				{
					continue;
				}
				
				container.add(type.cast(object));
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method addObjects.
	 * @param <V>
	 * @param <T>
	 * @param container Array<T>
	 * @param type Class<V>
	 * @param exclude int
	 * @param subId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param radius float
	 * @return Array<T>
	 */
	public <V extends T, T extends TObject> Array<T> addObjects(Array<T> container, Class<V> type, int exclude, int subId, float x, float y, float z, float radius)
	{
		if ((objects == null) || (sizeAll == 0))
		{
			return container;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final TObject object = objects[i];
				
				if (((object.getObjectId() == exclude) && (object.getSubId() == subId)) || !type.isInstance(object) || !object.isInRange(x, y, z, radius))
				{
					continue;
				}
				
				container.add(type.cast(object));
			}
			
			return container;
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method getObjectCount.
	 * @param <V>
	 * @param <T>
	 * @param type Class<V>
	 * @param exclude int
	 * @param subId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param radius float
	 * @return int
	 */
	public <V extends T, T extends TObject> int getObjectCount(Class<V> type, int exclude, int subId, float x, float y, float z, float radius)
	{
		if ((objects == null) || (sizeAll == 0))
		{
			return 0;
		}
		
		int counter = 0;
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final TObject object = objects[i];
				
				if (((object.getObjectId() == exclude) && (object.getSubId() == subId)) || !type.isInstance(object) || !object.isInRange(x, y, z, radius))
				{
					continue;
				}
				
				counter++;
			}
			
			return counter;
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method addToPlayers.
	 * @param object TObject
	 */
	public void addToPlayers(TObject object)
	{
		if ((objects == null) || (sizeAll < 1))
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final Player player = object.getPlayer();
			final TObject[] objects = getObjects();
			
			if (player != null)
			{
				for (int i = 0, length = ordinal; i < length; i++)
				{
					player.addVisibleObject(objects[i]);
				}
			}
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final Player target = objects[i].getPlayer();
				
				if ((target == null) || (target == object))
				{
					continue;
				}
				
				target.addVisibleObject(object);
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method calcSendCount.
	 * @param sender TObject
	 * @param packet ServerPacket
	 */
	public void calcSendCount(TObject sender, ServerPacket packet)
	{
		if ((objects == null) || (sizePlayers == 0))
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final Player target = objects[i].getPlayer();
				
				if ((target == null) || (target == sender))
				{
					continue;
				}
				
				packet.increaseSends();
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	private void changeStatus()
	{
		final WorldRegion[] regions = getNeighbors();
		
		for (WorldRegion region : regions)
		{
			region.updateActive();
		}
	}
	
	/**
	 * Method equals.
	 * @param target Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object target)
	{
		return this == target;
	}
	
	/**
	 * Method getContinentId.
	 * @return int
	 */
	public int getContinentId()
	{
		return continentId;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName()
	{
		return "(" + tileX + ", " + tileY + ", " + tileZ + ")";
	}
	
	/**
	 * Method getNeighbors.
	 * @return WorldRegion[]
	 */
	public WorldRegion[] getNeighbors()
	{
		if (neighbors == null)
		{
			synchronized (this)
			{
				if (neighbors == null)
				{
					neighbors = World.getNeighbors(continentId, tileX, tileY, tileZ);
				}
			}
		}
		
		return neighbors;
	}
	
	/**
	 * Method getObject.
	 * @param objectId int
	 * @param subId int
	 * @return TObject
	 */
	public TObject getObject(int objectId, int subId)
	{
		if (objects == null)
		{
			return null;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final TObject object = objects[i];
				
				if ((object.getObjectId() == objectId) && (object.getSubId() == subId))
				{
					return object;
				}
			}
			
			return null;
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method getObject.
	 * @param name String
	 * @return TObject
	 */
	public TObject getObject(String name)
	{
		if ((name == null) || (objects == null))
		{
			return null;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final TObject object = objects[i];
				
				if (name.equals(object.getName()))
				{
					return object;
				}
			}
			
			return null;
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method getObjects.
	 * @return TObject[]
	 */
	private final TObject[] getObjects()
	{
		return objects;
	}
	
	/**
	 * Method getSizeAll.
	 * @return int
	 */
	public int getSizeAll()
	{
		return sizeAll;
	}
	
	/**
	 * Method getSizeNpcs.
	 * @return int
	 */
	public int getSizeNpcs()
	{
		return sizeNpcs;
	}
	
	/**
	 * Method getSizePlayers.
	 * @return int
	 */
	public int getSizePlayers()
	{
		return sizePlayers;
	}
	
	/**
	 * Method getTerritories.
	 * @return Territory[]
	 */
	public final Territory[] getTerritories()
	{
		return territories;
	}
	
	/**
	 * Method getZoneId.
	 * @param object TObject
	 * @return int
	 */
	public final int getZoneId(TObject object)
	{
		final WorldZone[] zones = getZones();
		
		if (zones == null)
		{
			return object.getContinentId() + 1;
		}
		
		if (zones.length == 1)
		{
			final WorldZone zone = zones[0];
			
			if (zone.contains((int) object.getX(), (int) object.getY(), (int) object.getZ()))
			{
				return zone.getZoneId();
			}
		}
		else
		{
			for (WorldZone zone : zones)
			{
				if (zone.contains((int) object.getX(), (int) object.getY(), (int) object.getZ()))
				{
					return zone.getZoneId();
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Method getZones.
	 * @return WorldZone[]
	 */
	public final WorldZone[] getZones()
	{
		return zones;
	}
	
	/**
	 * Method hashCode.
	 * @return int
	 */
	@Override
	public final int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + tileX;
		result = (prime * result) + tileY;
		result = (prime * result) + tileZ;
		return result;
	}
	
	/**
	 * Method hasTerritories.
	 * @return boolean
	 */
	public final boolean hasTerritories()
	{
		return territories != null;
	}
	
	/**
	 * Method isActive.
	 * @return boolean
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Method isEmpty.
	 * @return boolean
	 */
	public boolean isEmpty()
	{
		return sizeAll < 1;
	}
	
	/**
	 * Method removeFromPlayers.
	 * @param object TObject
	 * @param type int
	 */
	public void removeFromPlayers(TObject object, int type)
	{
		if ((objects == null) || (sizeAll == 0))
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final Player player = object.getPlayer();
			final TObject[] objects = getObjects();
			
			if (player != null)
			{
				for (int i = 0, length = ordinal; i < length; i++)
				{
					final TObject target = objects[i];
					
					if ((target == null) || (target == object))
					{
						continue;
					}
					
					player.removeVisibleObject(target, type);
				}
			}
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final Player target = objects[i].getPlayer();
				
				if ((target != null) && (target != object))
				{
					target.removeVisibleObject(object, type);
				}
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method removeObject.
	 * @param object TObject
	 */
	public void removeObject(TObject object)
	{
		if ((object == null) || (objects == null))
		{
			return;
		}
		
		boolean changeStatus = false;
		writeLock.lock();
		
		try
		{
			if (object.isTrap())
			{
				traps.fastRemove(object);
			}
			
			boolean removed = false;
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = objects.length; i < length; i++)
			{
				if (objects[i] == object)
				{
					objects[i] = objects[--ordinal];
					objects[ordinal] = null;
					removed = true;
					break;
				}
			}
			
			if (removed)
			{
				sizeAll -= 1;
				final int oldPlayers = sizePlayers;
				
				if (object.isPlayer())
				{
					sizePlayers -= 1;
				}
				else if (object.isNpc())
				{
					sizeNpcs -= 1;
				}
				
				changeStatus = (oldPlayers > 0) && (sizePlayers <= 0);
			}
		}
		
		finally
		{
			writeLock.unlock();
		}
		
		if (changeStatus)
		{
			changeStatus();
		}
	}
	
	/**
	 * Method sendPacket.
	 * @param sender TObject
	 * @param packet ServerPacket
	 */
	public void sendPacket(TObject sender, ServerPacket packet)
	{
		if ((objects == null) || (sizePlayers == 0))
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final Player target = objects[i].getPlayer();
				
				if ((target == null) || (target == sender))
				{
					continue;
				}
				
				target.sendPacket(packet, true);
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method setActive.
	 * @param newActive boolean
	 */
	private synchronized void setActive(boolean newActive)
	{
		active = newActive;
		
		if (newActive)
		{
			startAI();
			World.addActiveRegion(this);
		}
		else
		{
			stopAI();
			World.removeActiveRegion(this);
		}
	}
	
	/**
	 * Method setTerritories.
	 * @param territories Territory[]
	 */
	public void setTerritories(Territory[] territories)
	{
		this.territories = territories;
	}
	
	/**
	 * Method setZones.
	 * @param zones WorldZone[]
	 */
	public void setZones(WorldZone[] zones)
	{
		this.zones = zones;
	}
	
	public void startAI()
	{
		if ((objects == null) || (sizeNpcs < 1))
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final Npc npc = objects[i].getNpc();
				
				if (npc == null)
				{
					continue;
				}
				
				final CharacterAI ai = npc.getAI();
				
				if (ai.isGlobalAI())
				{
					continue;
				}
				
				ai.startAITask();
				npc.startEmotions();
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	public void stopAI()
	{
		if ((objects == null) || (sizeNpcs < 1))
		{
			return;
		}
		
		readLock.lock();
		
		try
		{
			final TObject[] objects = getObjects();
			
			for (int i = 0, length = ordinal; i < length; i++)
			{
				final Npc npc = objects[i].getNpc();
				
				if (npc == null)
				{
					continue;
				}
				
				final CharacterAI ai = npc.getAI();
				
				if (ai.isGlobalAI())
				{
					continue;
				}
				
				ai.stopAITask();
				npc.stopEmotions();
			}
		}
		
		finally
		{
			readLock.unlock();
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "WorldRegion sizeAll = " + sizeAll + ", sizeNpcs = " + sizeNpcs + ", sizePlayers = " + sizePlayers + ", continentId = " + continentId + ", tileX = " + tileX + ", tileY = " + tileY + ", tileZ = " + tileZ + ", active = " + active;
	}
	
	private void updateActive()
	{
		boolean current = false;
		final WorldRegion[] around = getNeighbors();
		
		for (WorldRegion region : around)
		{
			if (region.getSizePlayers() > 0)
			{
				current = true;
				break;
			}
		}
		
		if (current != active)
		{
			setActive(current);
		}
	}
}
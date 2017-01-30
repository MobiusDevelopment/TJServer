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
package tera.gameserver.model.npc.spawn;

import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.Minion;
import tera.gameserver.model.npc.MinionLeader;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Rnd;
import rlib.util.array.Array;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 * @created 12.04.2012
 */
public final class MinionSpawn implements Spawn
{
	private static final Logger log = Loggers.getLogger(MinionSpawn.class);
	private final FoldablePool<Minion> pool;
	private final NpcTemplate template;
	private ConfigAI config;
	private NpcAIClass aiClass;
	private int count;
	private int radius;
	
	/**
	 * Constructor for MinionSpawn.
	 * @param template NpcTemplate
	 */
	public MinionSpawn(NpcTemplate template)
	{
		this.template = template;
		pool = Pools.newConcurrentFoldablePool(Minion.class);
	}
	
	/**
	 * Method doDie.
	 * @param npc Npc
	 * @see tera.gameserver.model.npc.spawn.Spawn#doDie(Npc)
	 */
	@Override
	public void doDie(Npc npc)
	{
		pool.put((Minion) npc);
	}
	
	/**
	 * Method getCount.
	 * @return int
	 */
	public final int getCount()
	{
		return count;
	}
	
	/**
	 * Method getLocation.
	 * @return Location
	 * @see tera.gameserver.model.npc.spawn.Spawn#getLocation()
	 */
	@Override
	public Location getLocation()
	{
		throw new IllegalArgumentException("unsupported method.");
	}
	
	/**
	 * Method getRadius.
	 * @return int
	 */
	public int getRadius()
	{
		return radius;
	}
	
	/**
	 * Method getTemplate.
	 * @return NpcTemplate
	 */
	public NpcTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 * @see tera.gameserver.model.npc.spawn.Spawn#getTemplateId()
	 */
	@Override
	public int getTemplateId()
	{
		return template.getTemplateId();
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 * @see tera.gameserver.model.npc.spawn.Spawn#getTemplateType()
	 */
	@Override
	public int getTemplateType()
	{
		return template.getTemplateType();
	}
	
	/**
	 * Method setCount.
	 * @param count int
	 */
	public final void setCount(int count)
	{
		this.count = count;
	}
	
	/**
	 * Method setLocation.
	 * @param location Location
	 * @see tera.gameserver.model.npc.spawn.Spawn#setLocation(Location)
	 */
	@Override
	public void setLocation(Location location)
	{
		throw new IllegalArgumentException("unsupported method.");
	}
	
	/**
	 * Method setRadius.
	 * @param radius int
	 */
	public final void setRadius(int radius)
	{
		this.radius = radius;
	}
	
	/**
	 * Method start.
	 * @see tera.gameserver.model.npc.spawn.Spawn#start()
	 */
	@Override
	public void start()
	{
		log.warning(new Exception("unsupported method"));
	}
	
	/**
	 * Method start.
	 * @param leader MinionLeader
	 * @param array Array<Minion>
	 */
	public void start(MinionLeader leader, Array<Minion> array)
	{
		final float x = leader.getX();
		final float y = leader.getY();
		final float z = leader.getZ();
		final int continentId = leader.getContinentId();
		final FoldablePool<Minion> pool = getPool();
		final NpcTemplate template = getTemplate();
		
		for (int i = 0, length = getCount(); i < length; i++)
		{
			Minion newNpc = pool.take();
			
			if (newNpc == null)
			{
				newNpc = (Minion) template.newInstance();
				newNpc.setSpawn(this);
				Location spawnLoc = null;
				
				if (radius > 0)
				{
					spawnLoc = Coords.randomCoords(new Location(), x, y, z, 0, radius);
				}
				else
				{
					spawnLoc = new Location(x, y, z, Rnd.nextInt(32000));
				}
				
				spawnLoc.setContinentId(continentId);
				newNpc.setAi(aiClass.newInstance(newNpc, getConfig()));
				newNpc.spawnMe(spawnLoc, leader);
				array.add(newNpc);
			}
			else
			{
				Location spawnLoc = newNpc.getSpawnLoc();
				
				if (radius > 0)
				{
					spawnLoc = Coords.randomCoords(spawnLoc, x, y, z, 0, radius);
				}
				else
				{
					spawnLoc.setXYZH(x, y, z, Rnd.nextInt(32000));
				}
				
				spawnLoc.setContinentId(continentId);
				newNpc.spawnMe(spawnLoc, leader);
				array.add(newNpc);
			}
		}
	}
	
	/**
	 * Method stop.
	 * @see tera.gameserver.model.npc.spawn.Spawn#stop()
	 */
	@Override
	public void stop()
	{
		log.warning(new Exception("unsupported method"));
	}
	
	/**
	 * Method getPool.
	 * @return FoldablePool<Minion>
	 */
	public FoldablePool<Minion> getPool()
	{
		return pool;
	}
	
	/**
	 * Method setAiClass.
	 * @param aiClass NpcAIClass
	 */
	public void setAiClass(NpcAIClass aiClass)
	{
		this.aiClass = aiClass;
	}
	
	/**
	 * Method setConfig.
	 * @param config ConfigAI
	 */
	public void setConfig(ConfigAI config)
	{
		this.config = config;
	}
	
	/**
	 * Method getAiClass.
	 * @return NpcAIClass
	 */
	public NpcAIClass getAiClass()
	{
		return aiClass;
	}
	
	/**
	 * Method getConfig.
	 * @return ConfigAI
	 */
	public ConfigAI getConfig()
	{
		return config;
	}
	
	/**
	 * Method getRoute.
	 * @return Location[]
	 * @see tera.gameserver.model.npc.spawn.Spawn#getRoute()
	 */
	@Override
	public Location[] getRoute()
	{
		return null;
	}
}
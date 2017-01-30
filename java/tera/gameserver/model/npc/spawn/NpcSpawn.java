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

import java.util.concurrent.ScheduledFuture;

import org.w3c.dom.Node;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.RandomManager;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.random.Random;

/**
 * @author Ronn
 */
public class NpcSpawn extends SafeTask implements Spawn
{
	protected static final Logger log = Loggers.getLogger(Spawn.class);
	protected NpcTemplate template;
	protected Location location;
	protected Location[] route;
	protected Npc spawned;
	protected Npc dead;
	protected ConfigAI config;
	protected NpcAIClass aiClass;
	protected Random random;
	protected volatile ScheduledFuture<? extends Spawn> schedule;
	protected int respawnTime;
	protected int randomTime;
	protected int minRadius;
	protected int maxRadius;
	protected volatile boolean stoped;
	
	/**
	 * Constructor for NpcSpawn.
	 * @param template NpcTemplate
	 * @param location Location
	 * @param config ConfigAI
	 * @param aiClass NpcAIClass
	 */
	public NpcSpawn(NpcTemplate template, Location location, ConfigAI config, NpcAIClass aiClass)
	{
		this(null, null, template, location, 0, 0, 0, 0, config, aiClass);
	}
	
	/**
	 * Constructor for NpcSpawn.
	 * @param node Node
	 * @param vars VarTable
	 * @param template NpcTemplate
	 * @param location Location
	 * @param respawnTime int
	 * @param randomTime int
	 * @param minRadius int
	 * @param maxRadius int
	 * @param config ConfigAI
	 * @param aiClass NpcAIClass
	 */
	public NpcSpawn(Node node, VarTable vars, NpcTemplate template, Location location, int respawnTime, int randomTime, int minRadius, int maxRadius, ConfigAI config, NpcAIClass aiClass)
	{
		this.template = template;
		this.location = location;
		this.respawnTime = respawnTime;
		this.randomTime = randomTime;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.config = config;
		this.aiClass = aiClass;
		
		if (node != null)
		{
			for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
			{
				if (child.getNodeType() != Node.ELEMENT_NODE)
				{
					continue;
				}
				
				if ("route".equals(child.getNodeName()))
				{
					parseRoute(location, child);
				}
			}
		}
		
		final RandomManager manager = RandomManager.getInstance();
		random = manager.getNpcSpawnRandom();
	}
	
	/**
	 * Method doDie.
	 * @param npc Npc
	 * @see tera.gameserver.model.npc.spawn.Spawn#doDie(Npc)
	 */
	@Override
	public synchronized void doDie(Npc npc)
	{
		setSpawned(null);
		setDead(npc);
		doRespawn();
	}
	
	public synchronized void doRespawn()
	{
		if (isStoped())
		{
			return;
		}
		
		if (schedule != null)
		{
			return;
		}
		
		final int randomTime = getRandomTime();
		final int respawnTime = getRespawnTime();
		final ExecutorManager executor = ExecutorManager.getInstance();
		
		if (randomTime == 0)
		{
			schedule = executor.scheduleGeneral(this, respawnTime * 1000);
		}
		else
		{
			schedule = executor.scheduleGeneral(this, getRandom().nextInt(Math.max(0, respawnTime - randomTime), respawnTime + randomTime) * 1000);
		}
	}
	
	/**
	 * Method getRandom.
	 * @return Random
	 */
	public Random getRandom()
	{
		return random;
	}
	
	public synchronized void doSpawn()
	{
		if (isStoped())
		{
			return;
		}
		
		if (spawned != null)
		{
			return;
		}
		
		if (schedule != null)
		{
			schedule.cancel(false);
			schedule = null;
		}
		
		Npc newNpc = getDead();
		final Location location = getLocation();
		
		if (newNpc == null)
		{
			newNpc = template.newInstance();
			newNpc.setSpawn(this);
			newNpc.setAi(aiClass.newInstance(newNpc, config));
			Location spawnLoc = null;
			
			if (maxRadius > 0)
			{
				spawnLoc = Coords.randomCoords(new Location(), location.getX(), location.getY(), location.getZ(), location.getHeading() == -1 ? getRandom().nextInt(35000) : location.getHeading(), minRadius, maxRadius);
			}
			else
			{
				spawnLoc = new Location(location.getX(), location.getY(), location.getZ(), location.getHeading() == -1 ? getRandom().nextInt(0, 65000) : location.getHeading());
			}
			
			spawnLoc.setContinentId(location.getContinentId());
			newNpc.spawnMe(spawnLoc);
		}
		else
		{
			setDead(null);
			newNpc.reinit();
			Location spawnLoc = null;
			
			if (maxRadius > 0)
			{
				spawnLoc = Coords.randomCoords(newNpc.getSpawnLoc(), location.getX(), location.getY(), location.getZ(), location.getHeading() == -1 ? getRandom().nextInt(35000) : location.getHeading(), minRadius, maxRadius);
			}
			else
			{
				spawnLoc = newNpc.getSpawnLoc();
			}
			
			spawnLoc.setContinentId(location.getContinentId());
			newNpc.spawnMe(spawnLoc);
		}
		
		setSpawned(newNpc);
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
	public final ConfigAI getConfig()
	{
		return config;
	}
	
	/**
	 * Method getDead.
	 * @return Npc
	 */
	public final Npc getDead()
	{
		return dead;
	}
	
	/**
	 * Method getLocation.
	 * @return Location
	 * @see tera.gameserver.model.npc.spawn.Spawn#getLocation()
	 */
	@Override
	public Location getLocation()
	{
		return location;
	}
	
	/**
	 * Method getMaxRadius.
	 * @return int
	 */
	public final int getMaxRadius()
	{
		return maxRadius;
	}
	
	/**
	 * Method getMinRadius.
	 * @return int
	 */
	public final int getMinRadius()
	{
		return minRadius;
	}
	
	/**
	 * Method getRandomTime.
	 * @return int
	 */
	public final int getRandomTime()
	{
		return randomTime;
	}
	
	/**
	 * Method getRespawnTime.
	 * @return int
	 */
	public final int getRespawnTime()
	{
		return respawnTime;
	}
	
	/**
	 * Method getRoute.
	 * @return Location[]
	 * @see tera.gameserver.model.npc.spawn.Spawn#getRoute()
	 */
	@Override
	public Location[] getRoute()
	{
		return route;
	}
	
	/**
	 * Method getSpawned.
	 * @return Npc
	 */
	public final Npc getSpawned()
	{
		return spawned;
	}
	
	/**
	 * Method getTemplate.
	 * @return NpcTemplate
	 */
	public final NpcTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 * @see tera.gameserver.model.npc.spawn.Spawn#getTemplateId()
	 */
	@Override
	public final int getTemplateId()
	{
		return template.getTemplateId();
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 * @see tera.gameserver.model.npc.spawn.Spawn#getTemplateType()
	 */
	@Override
	public final int getTemplateType()
	{
		return template.getTemplateType();
	}
	
	/**
	 * Method hashCode.
	 * @return int
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Float.floatToIntBits(location.getX());
		result = (prime * result) + Float.floatToIntBits(location.getY());
		result = (prime * result) + Float.floatToIntBits(location.getZ());
		return result;
	}
	
	/**
	 * Method isRaid.
	 * @return boolean
	 */
	public final boolean isRaid()
	{
		return template.isRaid();
	}
	
	/**
	 * Method isStoped.
	 * @return boolean
	 */
	public final boolean isStoped()
	{
		return stoped;
	}
	
	/**
	 * Method parseRoute.
	 * @param location Location
	 * @param node Node
	 */
	protected void parseRoute(Location location, Node node)
	{
		final Array<Location> points = Arrays.toArray(Location.class);
		final VarTable vars = VarTable.newInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("point".equals(child.getNodeName()))
			{
				vars.parse(child);
				points.add(new Location(vars.getFloat("x"), vars.getFloat("y"), vars.getFloat("z"), 0, location.getContinentId()));
			}
		}
		
		points.trimToSize();
		setRoute(points.array());
	}
	
	@Override
	protected void runImpl()
	{
		doSpawn();
	}
	
	/**
	 * Method setDead.
	 * @param dead Npc
	 */
	public void setDead(Npc dead)
	{
		if (dead != null)
		{
			dead.finalyze();
		}
		
		this.dead = dead;
	}
	
	/**
	 * Method setLocation.
	 * @param location Location
	 * @see tera.gameserver.model.npc.spawn.Spawn#setLocation(Location)
	 */
	@Override
	public void setLocation(Location location)
	{
		this.location.set(location);
	}
	
	/**
	 * Method setMaxRadius.
	 * @param maxRadius int
	 */
	public final void setMaxRadius(int maxRadius)
	{
		this.maxRadius = maxRadius;
	}
	
	/**
	 * Method setMinRadius.
	 * @param minRadius int
	 */
	public final void setMinRadius(int minRadius)
	{
		this.minRadius = minRadius;
	}
	
	/**
	 * Method setOptions.
	 * @param config ConfigAI
	 */
	public final void setOptions(ConfigAI config)
	{
		this.config = config;
	}
	
	/**
	 * Method setRespawnTime.
	 * @param respawnTime int
	 */
	public final void setRespawnTime(int respawnTime)
	{
		this.respawnTime = respawnTime;
	}
	
	/**
	 * Method setRoute.
	 * @param route Location[]
	 */
	public void setRoute(Location[] route)
	{
		this.route = route;
	}
	
	/**
	 * Method setSpawned.
	 * @param spawned Npc
	 */
	public final void setSpawned(Npc spawned)
	{
		this.spawned = spawned;
	}
	
	/**
	 * Method setStoped.
	 * @param stoped boolean
	 */
	public final void setStoped(boolean stoped)
	{
		this.stoped = stoped;
	}
	
	/**
	 * Method setTemplate.
	 * @param template NpcTemplate
	 */
	public final void setTemplate(NpcTemplate template)
	{
		this.template = template;
	}
	
	/**
	 * Method start.
	 * @see tera.gameserver.model.npc.spawn.Spawn#start()
	 */
	@Override
	public void start()
	{
		setStoped(false);
		doSpawn();
	}
	
	/**
	 * Method stop.
	 * @see tera.gameserver.model.npc.spawn.Spawn#stop()
	 */
	@Override
	public synchronized void stop()
	{
		if (isStoped())
		{
			return;
		}
		
		setStoped(true);
		final Npc spawned = getSpawned();
		
		if (spawned != null)
		{
			spawned.deleteMe();
			setDead(spawned);
			setSpawned(null);
		}
		
		if (schedule != null)
		{
			schedule.cancel(false);
			schedule = null;
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "NpcSpawn  template = " + template + ", location = " + location + ", aiClass = " + aiClass;
	}
}
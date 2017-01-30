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

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public class SummonSpawn extends SafeTask implements Spawn
{
	
	private static final Logger LOGGER = Loggers.getLogger(SummonSpawn.class);
	
	private final NpcTemplate template;
	
	private final Location location;
	
	private final ConfigAI configAI;
	
	private final NpcAIClass aiClass;
	
	private final int lifeTime;
	
	private volatile Character owner;
	
	private volatile Summon spawned;
	
	private volatile Summon dead;
	
	private volatile ScheduledFuture<SummonSpawn> schedule;
	
	/**
	 * Constructor for SummonSpawn.
	 * @param template NpcTemplate
	 * @param configAI ConfigAI
	 * @param aiClass NpcAIClass
	 * @param lifeTime int
	 */
	public SummonSpawn(NpcTemplate template, ConfigAI configAI, NpcAIClass aiClass, int lifeTime)
	{
		this.template = template;
		this.configAI = configAI;
		this.aiClass = aiClass;
		this.lifeTime = lifeTime;
		location = new Location();
	}
	
	/**
	 * Method doDie.
	 * @param npc Npc
	 * @see tera.gameserver.model.npc.spawn.Spawn#doDie(Npc)
	 */
	@Override
	public void doDie(Npc npc)
	{
		if (!npc.isSummon())
		{
			return;
		}
		
		setDead((Summon) npc);
		setSpawned(null);
		deSpawn();
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
	 * Method start.
	 * @see tera.gameserver.model.npc.spawn.Spawn#start()
	 */
	@Override
	public synchronized void start()
	{
		final Summon spawned = getSpawned();
		
		if (spawned != null)
		{
			LOGGER.warning(this, "found duplicate spawn!");
			return;
		}
		
		final Character owner = getOwner();
		
		if (owner == null)
		{
			return;
		}
		
		final Location location = getLocation();
		Summon summon = getDead();
		
		if (summon != null)
		{
			summon.finishDead();
			summon.reinit();
			summon.setOwner(owner);
			summon.spawnMe(location);
		}
		else
		{
			summon = (Summon) template.newInstance();
			summon.setOwner(owner);
			summon.setSpawn(this);
			summon.setAi(aiClass.newInstance(summon, configAI));
			summon.spawnMe(location);
		}
		
		setDead(null);
		setSpawned(summon);
		owner.setSummon(summon);
		final ExecutorManager executorManager = ExecutorManager.getInstance();
		setSchedule(executorManager.scheduleGeneral(this, lifeTime));
	}
	
	/**
	 * Method stop.
	 * @see tera.gameserver.model.npc.spawn.Spawn#stop()
	 */
	@Override
	public synchronized void stop()
	{
		final ScheduledFuture<SummonSpawn> schedule = getSchedule();
		
		if (schedule != null)
		{
			schedule.cancel(true);
			setSchedule(null);
		}
		
		final Summon spawned = getSpawned();
		
		if (spawned != null)
		{
			spawned.remove();
		}
	}
	
	/**
	 * Method getOwner.
	 * @return Character
	 */
	public Character getOwner()
	{
		return owner;
	}
	
	/**
	 * Method setOwner.
	 * @param owner Character
	 */
	public void setOwner(Character owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Method setDead.
	 * @param dead Summon
	 */
	public void setDead(Summon dead)
	{
		this.dead = dead;
	}
	
	/**
	 * Method setSpawned.
	 * @param spawned Summon
	 */
	public void setSpawned(Summon spawned)
	{
		this.spawned = spawned;
	}
	
	/**
	 * Method getSpawned.
	 * @return Summon
	 */
	public Summon getSpawned()
	{
		return spawned;
	}
	
	/**
	 * Method getDead.
	 * @return Summon
	 */
	public Summon getDead()
	{
		return dead;
	}
	
	@Override
	protected void runImpl()
	{
		deSpawn();
	}
	
	private synchronized void deSpawn()
	{
		final Summon spawned = getSpawned();
		
		if (spawned != null)
		{
			spawned.remove();
		}
		
		final ScheduledFuture<SummonSpawn> schedule = getSchedule();
		
		if (schedule != null)
		{
			schedule.cancel(true);
			setSchedule(null);
		}
	}
	
	/**
	 * Method getSchedule.
	 * @return ScheduledFuture<SummonSpawn>
	 */
	public ScheduledFuture<SummonSpawn> getSchedule()
	{
		return schedule;
	}
	
	/**
	 * Method setSchedule.
	 * @param schedule ScheduledFuture<SummonSpawn>
	 */
	public void setSchedule(ScheduledFuture<SummonSpawn> schedule)
	{
		this.schedule = schedule;
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

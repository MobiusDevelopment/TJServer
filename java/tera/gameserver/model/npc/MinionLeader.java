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
package tera.gameserver.model.npc;

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MinionData;
import tera.gameserver.model.World;
import tera.gameserver.templates.NpcTemplate;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.idfactory.IdGenerator;
import rlib.idfactory.IdGenerators;
import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 14.03.2012
 */
public class MinionLeader extends Monster
{
	private static final IdGenerator ID_FACTORY = IdGenerators.newSimpleIdGenerator(800001, 1000000);
	private final Array<Minion> minions;
	private final SafeTask task;
	private final MinionData data;
	private volatile ScheduledFuture<SafeTask> schedule;
	
	/**
	 * Constructor for MinionLeader.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public MinionLeader(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		minions = Arrays.toConcurrentArray(Minion.class);
		data = getTemplate().getMinions();
		task = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				respawnMinions();
			}
		};
	}
	
	/**
	 * Method doDie.
	 * @param attacker Character
	 */
	@Override
	public void doDie(Character attacker)
	{
		synchronized (this)
		{
			final Array<Minion> minions = getMinions();
			
			if (!minions.isEmpty())
			{
				minions.readLock();
				
				try
				{
					final Minion[] array = minions.array();
					
					for (int i = 0, length = minions.size(); i < length; i++)
					{
						array[i].setLeader(null);
					}
				}
				
				finally
				{
					minions.readUnlock();
				}
				minions.clear();
			}
			
			final ScheduledFuture<SafeTask> schedule = getSchedule();
			
			if (schedule != null)
			{
				schedule.cancel(false);
				setSchedule(null);
			}
		}
		super.doDie(attacker);
	}
	
	/**
	 * Method getData.
	 * @return MinionData
	 */
	protected final MinionData getData()
	{
		return data;
	}
	
	/**
	 * Method getMinionLeader.
	 * @return MinionLeader
	 */
	@Override
	public MinionLeader getMinionLeader()
	{
		return this;
	}
	
	/**
	 * Method getMinions.
	 * @return Array<Minion>
	 */
	public final Array<Minion> getMinions()
	{
		return minions;
	}
	
	/**
	 * Method hasMinions.
	 * @return boolean
	 */
	public boolean hasMinions()
	{
		return minions.size() > 0;
	}
	
	/**
	 * Method isMinionLeader.
	 * @return boolean
	 */
	@Override
	public boolean isMinionLeader()
	{
		return true;
	}
	
	/**
	 * Method nextCastId.
	 * @return int
	 */
	@Override
	public int nextCastId()
	{
		return ID_FACTORY.getNextId();
	}
	
	/**
	 * Method onDie.
	 * @param minion Minion
	 */
	public void onDie(Minion minion)
	{
		final Array<Minion> minions = getMinions();
		final MinionData data = getData();
		
		if ((data == null) || minions.isEmpty())
		{
			return;
		}
		
		minions.fastRemove(minion);
		
		if (minions.isEmpty())
		{
			ScheduledFuture<SafeTask> schedule = getSchedule();
			
			if (schedule == null)
			{
				synchronized (this)
				{
					schedule = getSchedule();
					
					if (schedule == null)
					{
						final ExecutorManager executor = ExecutorManager.getInstance();
						setSchedule(executor.scheduleGeneral(task, data.getRespawnDelay() * 1000));
					}
				}
			}
		}
	}
	
	public final void respawnMinions()
	{
		final MinionData data = getData();
		
		if (data == null)
		{
			return;
		}
		
		final Array<Minion> minions = getMinions();
		data.spawnMinions(this, minions);
		updateMinionAggro();
		setSchedule(null);
	}
	
	/**
	 * Method spawnMe.
	 * @param loc Location
	 */
	@Override
	public void spawnMe(Location loc)
	{
		super.spawnMe(loc);
		final MinionData data = getData();
		
		if (data == null)
		{
			return;
		}
		
		final LocalObjects local = LocalObjects.get();
		final Array<Minion> around = World.getAround(local.getNextMinionList(), Minion.class, this);
		final Array<Minion> minions = getMinions();
		
		if (!around.isEmpty())
		{
			final Minion[] array = around.array();
			
			for (int i = 0, length = around.size(); i < length; i++)
			{
				final Minion minion = array[i];
				
				if ((minion.getMinionLeader() != null) || !data.containsMinion(minion.getSpawn(), minions))
				{
					continue;
				}
				
				minion.setLeader(this);
				minion.getSpawnLoc().set(spawnLoc);
				minions.add(minion);
			}
		}
		
		if (minions.isEmpty())
		{
			data.spawnMinions(this, minions);
		}
		else
		{
			updateMinionAggro();
		}
	}
	
	protected void updateMinionAggro()
	{
		final Array<AggroInfo> aggroList = getAggroList();
		
		if (!aggroList.isEmpty())
		{
			final Array<Minion> minions = getMinions();
			aggroList.readLock();
			
			try
			{
				final AggroInfo[] aggro = aggroList.array();
				minions.readLock();
				
				try
				{
					final Minion[] array = minions.array();
					
					for (int g = 0, size = aggroList.size(); g < size; g++)
					{
						final AggroInfo info = aggro[g];
						
						for (int i = 0, length = minions.size(); i < length; i++)
						{
							array[i].addAggro(info.getAggressor(), info.getAggro(), false);
						}
					}
				}
				
				finally
				{
					minions.readUnlock();
				}
			}
			
			finally
			{
				aggroList.readUnlock();
			}
		}
	}
	
	/**
	 * Method getSchedule.
	 * @return ScheduledFuture<SafeTask>
	 */
	public ScheduledFuture<SafeTask> getSchedule()
	{
		return schedule;
	}
	
	/**
	 * Method setSchedule.
	 * @param schedule ScheduledFuture<SafeTask>
	 */
	public void setSchedule(ScheduledFuture<SafeTask> schedule)
	{
		this.schedule = schedule;
	}
}
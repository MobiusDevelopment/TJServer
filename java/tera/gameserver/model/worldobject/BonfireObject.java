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
package tera.gameserver.model.worldobject;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import tera.gameserver.IdFactory;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Bonfire;
import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.StatFunc;
import tera.util.LocalObjects;

import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public class BonfireObject extends WorldObject implements Foldable, Bonfire
{
	
	private static final FoldablePool<BonfireObject> bonfirePool = Pools.newConcurrentFoldablePool(BonfireObject.class);
	
	/**
	 * Method startBonfire.
	 * @param regenPower float
	 * @param lifeTime int
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 */
	public static void startBonfire(float regenPower, int lifeTime, int continentId, float x, float y, float z)
	{
		BonfireObject bonfire = bonfirePool.take();
		
		if (bonfire != null)
		{
			bonfire.reinit(regenPower, lifeTime);
		}
		else
		{
			final IdFactory idFactory = IdFactory.getInstance();
			bonfire = new BonfireObject(idFactory.getNextObjectId(), regenPower, lifeTime);
		}
		
		bonfire.setContinentId(continentId);
		bonfire.spawnMe(x, y, z, 0);
	}
	
	private final StatFunc func;
	
	private Runnable lifeTask;
	
	private Runnable regenTask;
	
	private ScheduledFuture<Runnable> lifeSchedule;
	
	private ScheduledFuture<Runnable> regenSchedule;
	
	private final Array<Player> players;
	
	private float regenPower;
	
	private int lifeTime;
	
	/**
	 * Constructor for BonfireObject.
	 * @param objectId int
	 * @param regenPower float
	 * @param lifeTime int
	 */
	public BonfireObject(int objectId, float regenPower, int lifeTime)
	{
		super(objectId);
		this.regenPower = regenPower;
		this.lifeTime = lifeTime;
		players = Arrays.toArray(Player.class);
		func = new StatFunc()
		{
			@Override
			public void addFuncTo(Character owner)
			{
				owner.addStatFunc(this);
			}
			
			@Override
			public float calc(Character attacker, Character attacked, Skill skill, float val)
			{
				return val *= getRegenPower();
			}
			
			@Override
			public int compareTo(StatFunc func)
			{
				return 0x30 - func.getOrder();
			}
			
			@Override
			public int getOrder()
			{
				return 0x30;
			}
			
			@Override
			public StatType getStat()
			{
				return StatType.REGEN_HP;
			}
			
			@Override
			public void removeFuncTo(Character owner)
			{
				owner.removeStatFunc(this);
			}
		};
		lifeTask = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				if (isDeleted())
				{
					return;
				}
				
				deleteMe();
			}
		};
		final ExecutorManager executor = ExecutorManager.getInstance();
		lifeSchedule = executor.scheduleGeneral(lifeTask, lifeTime);
		regenTask = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				final LocalObjects local = LocalObjects.get();
				final Array<Player> players = getPlayers();
				final Array<Player> around = World.getAround(Player.class, local.getNextPlayerList(), BonfireObject.this, 200F);
				final StatFunc func = getFunc();
				final BonfireObject bonfire = getBonfire();
				
				if (!around.isEmpty())
				{
					final Player[] array = around.array();
					
					for (int i = 0, length = around.size(); i < length; i++)
					{
						final Player player = array[i];
						
						if (!players.contains(player) && player.addBonfire(bonfire))
						{
							func.addFuncTo(player);
							players.add(player);
						}
					}
				}
				
				if (!players.isEmpty())
				{
					final Player[] array = players.array();
					
					for (int i = 0, length = players.size(); i < length; i++)
					{
						final Player player = array[i];
						
						if (player == null)
						{
							continue;
						}
						
						if (!player.isInRangeZ(bonfire, 200) || player.isDead())
						{
							player.removeBonfire(bonfire);
							func.removeFuncTo(player);
							players.fastRemove(i--);
							length--;
							continue;
						}
						
						player.addStamina();
					}
				}
			}
		};
		regenSchedule = executor.scheduleGeneralAtFixedRate(regenTask, 3000, 3000);
	}
	
	@Override
	public void deleteMe()
	{
		try
		{
			super.deleteMe();
			
			if (lifeSchedule != null)
			{
				lifeSchedule.cancel(false);
				lifeSchedule = null;
			}
			
			if (regenSchedule != null)
			{
				regenSchedule.cancel(false);
				regenSchedule = null;
			}
			
			bonfirePool.put(this);
		}
		catch (Exception e)
		{
			log.warning(this, e);
		}
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		final Array<Player> players = getPlayers();
		
		if (!players.isEmpty())
		{
			final Player[] array = players.array();
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				final Player player = array[i];
				
				if (player != null)
				{
					func.removeFuncTo(player);
					player.removeBonfire(this);
				}
			}
		}
		
		players.clear();
	}
	
	/**
	 * Method getBonfire.
	 * @return BonfireObject
	 */
	protected BonfireObject getBonfire()
	{
		return this;
	}
	
	/**
	 * Method getFunc.
	 * @return StatFunc
	 */
	protected final StatFunc getFunc()
	{
		return func;
	}
	
	/**
	 * Method getLifeTime.
	 * @return long
	 */
	public long getLifeTime()
	{
		if (lifeSchedule != null)
		{
			return lifeSchedule.getDelay(TimeUnit.SECONDS);
		}
		
		return 0;
	}
	
	/**
	 * Method getPlayers.
	 * @return Array<Player>
	 */
	protected final Array<Player> getPlayers()
	{
		return players;
	}
	
	/**
	 * Method getRegenPower.
	 * @return float
	 */
	protected final float getRegenPower()
	{
		return regenPower;
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
	 * Method reinit.
	 * @param regenPower float
	 * @param lifeTime int
	 */
	public synchronized void reinit(float regenPower, int lifeTime)
	{
		this.regenPower = regenPower;
		this.lifeTime = lifeTime;
		final ExecutorManager executor = ExecutorManager.getInstance();
		lifeSchedule = executor.scheduleGeneral(lifeTask, lifeTime);
		regenSchedule = executor.scheduleGeneralAtFixedRate(regenTask, 3000, 3000);
	}
	
	public synchronized void restart()
	{
		if (lifeSchedule == null)
		{
			return;
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		lifeSchedule.cancel(false);
		lifeSchedule = executor.scheduleGeneral(lifeTask, lifeTime);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "BonfireObject [" + getLifeTime() + "]";
	}
}

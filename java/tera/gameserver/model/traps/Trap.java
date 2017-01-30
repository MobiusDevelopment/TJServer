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
package tera.gameserver.model.traps;

import java.util.concurrent.ScheduledFuture;

import tera.Config;
import tera.gameserver.IdFactory;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.CharObjectDelete;
import tera.gameserver.network.serverpackets.TrapInfo;
import tera.util.LocalObjects;

import rlib.geom.Coords;
import rlib.util.array.Array;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public class Trap extends TObject implements Foldable, Runnable
{
	private static final FoldablePool<Trap> pool = Pools.newConcurrentFoldablePool(Trap.class);
	
	/**
	 * Method newInstance.
	 * @param owner Character
	 * @param skill Skill
	 * @param range int
	 * @param lifeTime int
	 * @param radius int
	 * @return Trap
	 */
	public static Trap newInstance(Character owner, Skill skill, int range, int lifeTime, int radius)
	{
		Trap trap = pool.take();
		
		if (trap == null)
		{
			final IdFactory idFactory = IdFactory.getInstance();
			trap = new Trap(idFactory.getNextTrapId());
		}
		
		trap.setContinentId(owner.getContinentId());
		trap.spawnMe(owner, skill, range, lifeTime, radius);
		return trap;
	}
	
	protected Character owner;
	protected Skill skill;
	protected ScheduledFuture<? extends Runnable> lifeTask;
	protected int radius;
	
	/**
	 * Constructor for Trap.
	 * @param objectId int
	 */
	public Trap(int objectId)
	{
		super(objectId);
	}
	
	/**
	 * Method activate.
	 * @param object TObject
	 * @return boolean
	 */
	public boolean activate(TObject object)
	{
		if (!object.isCharacter())
		{
			return false;
		}
		
		final Character owner = getOwner();
		final Character target = object.getCharacter();
		
		if ((owner == null) || (owner == target) || !owner.checkTarget(target))
		{
			return false;
		}
		
		final float dist = target.getGeomDistance(x, y);
		
		if (dist > radius)
		{
			return false;
		}
		
		if (lifeTask != null)
		{
			lifeTask.cancel(false);
			lifeTask = null;
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleGeneral(this, 100);
		return true;
	}
	
	/**
	 * Method addMe.
	 * @param player Player
	 */
	@Override
	public void addMe(Player player)
	{
		player.sendPacket(TrapInfo.getInstance(this), true);
	}
	
	@Override
	public synchronized void deleteMe()
	{
		if (deleted)
		{
			return;
		}
		
		super.deleteMe();
		fold();
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		owner = null;
		skill = null;
		lifeTask = null;
	}
	
	public void fold()
	{
		pool.put(this);
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
	 * Method getSkill.
	 * @return Skill
	 */
	public Skill getSkill()
	{
		return skill;
	}
	
	/**
	 * Method getSubId.
	 * @return int
	 */
	@Override
	public int getSubId()
	{
		return Config.SERVER_TRAP_SUB_ID;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	@Override
	public int getTemplateId()
	{
		return skill != null ? skill.getIconId() : 0;
	}
	
	/**
	 * Method getTrap.
	 * @return Trap
	 */
	@Override
	public Trap getTrap()
	{
		return this;
	}
	
	/**
	 * Method isTrap.
	 * @return boolean
	 */
	@Override
	public boolean isTrap()
	{
		return true;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		final IdFactory idFactory = IdFactory.getInstance();
		objectId = idFactory.getNextTrapId();
	}
	
	/**
	 * Method removeMe.
	 * @param player Player
	 * @param type int
	 */
	@Override
	public void removeMe(Player player, int type)
	{
		player.sendPacket(CharObjectDelete.getInstance(this), true);
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		final Skill skill = getSkill();
		
		if ((skill != null) && (lifeTask == null))
		{
			skill.useSkill(owner, x, y, z);
		}
		
		deleteMe();
	}
	
	/**
	 * Method spawnMe.
	 * @param owner Character
	 * @param skill Skill
	 * @param range int
	 * @param lifeTime int
	 * @param radius int
	 */
	public void spawnMe(Character owner, Skill skill, int range, int lifeTime, int radius)
	{
		this.owner = owner;
		this.skill = skill;
		this.radius = radius;
		spawnMe(Coords.calcX(owner.getX(), range, owner.getHeading()), Coords.calcY(owner.getY(), range, owner.getHeading()), owner.getZ(), 0);
		final LocalObjects local = LocalObjects.get();
		final Array<Character> chars = World.getAround(Character.class, local.getNextCharList(), this, radius);
		final ExecutorManager executor = ExecutorManager.getInstance();
		
		if (chars.isEmpty())
		{
			lifeTask = executor.scheduleGeneral(this, lifeTime * 1000);
		}
		else
		{
			final Character[] array = chars.array();
			
			for (int i = 0, length = chars.size(); i < length; i++)
			{
				final Character target = array[i];
				
				if (owner.checkTarget(target))
				{
					executor.scheduleGeneral(this, 100);
					return;
				}
			}
			
			lifeTask = executor.scheduleGeneral(this, lifeTime * 1000);
		}
	}
}
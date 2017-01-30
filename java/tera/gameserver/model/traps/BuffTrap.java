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

import tera.gameserver.IdFactory;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.Party;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.skillengine.Skill;
import tera.util.LocalObjects;

import rlib.geom.Coords;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public class BuffTrap extends Trap
{
	private static final FoldablePool<BuffTrap> pool = Pools.newConcurrentFoldablePool(BuffTrap.class);
	
	/**
	 * Method newInstance.
	 * @param owner Character
	 * @param spawnSkill Skill
	 * @param skill Skill
	 * @param lifeTime int
	 * @param radius int
	 * @return Trap
	 */
	public static Trap newInstance(Character owner, Skill spawnSkill, Skill skill, int lifeTime, int radius)
	{
		BuffTrap trap = pool.take();
		
		if (trap == null)
		{
			final IdFactory idFactory = IdFactory.getInstance();
			trap = new BuffTrap(idFactory.getNextTrapId());
		}
		
		trap.setContinentId(owner.getContinentId());
		trap.spawnMe(spawnSkill, owner, skill, lifeTime, radius);
		return trap;
	}
	
	private final Array<Character> appled;
	
	/**
	 * Constructor for BuffTrap.
	 * @param objectId int
	 */
	public BuffTrap(int objectId)
	{
		super(objectId);
		appled = Arrays.toArray(Character.class);
	}
	
	/**
	 * Method activate.
	 * @param object TObject
	 * @return boolean
	 */
	@Override
	public boolean activate(TObject object)
	{
		if (!object.isCharacter())
		{
			return false;
		}
		
		final Character owner = getOwner();
		final Character target = (Character) object;
		
		if (owner == null)
		{
			return false;
		}
		
		final float dist = target.getGeomDistance(x, y);
		
		if ((dist > radius) || appled.contains(target))
		{
			return false;
		}
		
		boolean active = owner == target;
		
		if (!active)
		{
			final Party party = owner.getParty();
			
			if ((party != null) && (target.getParty() == party))
			{
				active = true;
			}
		}
		
		if (!active)
		{
			return false;
		}
		
		skill.applySkill(owner, target);
		appled.add(target);
		return false;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		super.finalyze();
		appled.clear();
	}
	
	@Override
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method spawnMe.
	 * @param spawnSkill Skill
	 * @param owner Character
	 * @param skill Skill
	 * @param lifeTime int
	 * @param radius int
	 */
	public void spawnMe(Skill spawnSkill, Character owner, Skill skill, int lifeTime, int radius)
	{
		this.owner = owner;
		this.skill = skill;
		this.radius = radius;
		final ExecutorManager executor = ExecutorManager.getInstance();
		lifeTask = executor.scheduleGeneral(this, lifeTime * 1000);
		final float newX = Coords.calcX(owner.getX(), spawnSkill.getRange(), owner.getHeading());
		final float newY = Coords.calcY(owner.getY(), spawnSkill.getRange(), owner.getHeading());
		spawnMe(newX, newY, owner.getZ(), 0);
		final LocalObjects local = LocalObjects.get();
		final Array<Character> chars = World.getAround(Character.class, local.getNextCharList(), owner);
		
		if (!chars.isEmpty())
		{
			final Character[] array = chars.array();
			
			for (int i = 0, length = chars.size(); i < length; i++)
			{
				activate(array[i]);
			}
		}
	}
}
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
package tera.gameserver.model.skillengine.shots;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;

import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class FastAutoShot extends AbstractAutoShot
{
	private static final FoldablePool<FastAutoShot> pool = Pools.newConcurrentFoldablePool(FastAutoShot.class);
	
	/**
	 * Method startShot.
	 * @param caster Character
	 * @param target Character
	 * @param skill Skill
	 */
	public static void startShot(Character caster, Character target, Skill skill)
	{
		FastAutoShot shot = pool.take();
		
		if (shot == null)
		{
			shot = new FastAutoShot();
		}
		
		shot.caster = caster;
		shot.target = target;
		shot.skill = skill;
		shot.start();
	}
	
	/**
	 * Method getType.
	 * @return ShotType
	 * @see tera.gameserver.model.skillengine.shots.Shot#getType()
	 */
	@Override
	public ShotType getType()
	{
		return ShotType.FAST_SHOT;
	}
	
	/**
	 * Method stop.
	 * @see tera.gameserver.model.skillengine.shots.Shot#stop()
	 */
	@Override
	public void stop()
	{
		super.stop();
		pool.put(this);
	}
}
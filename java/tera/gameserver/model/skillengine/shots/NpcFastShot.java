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
public class NpcFastShot extends AbstractShot
{
	
	private static FoldablePool<NpcFastShot> shotPool = Pools.newConcurrentFoldablePool(NpcFastShot.class);
	
	/**
	 * Method startShot.
	 * @param caster Character
	 * @param skill Skill
	 * @param target Character
	 */
	public static void startShot(Character caster, Skill skill, Character target)
	{
		NpcFastShot shot = shotPool.take();
		
		if (shot == null)
		{
			shot = new NpcFastShot();
		}
		
		shot.prepare(caster, skill, target);
		shot.start();
	}
	
	private Character target;
	
	public NpcFastShot()
	{
		setType(ShotType.FAST_SHOT);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		target = null;
		super.finalyze();
	}
	
	/**
	 * Method prepare.
	 * @param caster Character
	 * @param skill Skill
	 * @param target Character
	 */
	public void prepare(Character caster, Skill skill, Character target)
	{
		this.target = target;
		super.prepare(caster, skill, target.getX(), target.getY(), target.getZ());
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		targetX = target.getX();
		targetY = target.getY();
		targetZ = target.getZ();
		super.run();
	}
	
	/**
	 * Method stop.
	 * @see tera.gameserver.model.skillengine.shots.Shot#stop()
	 */
	@Override
	public void stop()
	{
		super.stop();
		shotPool.put(this);
	}
}

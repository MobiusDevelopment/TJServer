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
package tera.gameserver.model.ai.npc.taskfactory;

import org.w3c.dom.Node;

import tera.gameserver.manager.GeoManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.util.LocalObjects;

import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.util.Rnd;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class SummonRangeBattleTaskFactory extends SummonBattleTaskFactory
{
	protected final int runAwayRate;
	
	/**
	 * Constructor for SummonRangeBattleTaskFactory.
	 * @param node Node
	 */
	public SummonRangeBattleTaskFactory(Node node)
	{
		super(node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			runAwayRate = vars.getInteger("runAwayRate", 20);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Method addNewTask.
	 * @param ai NpcAI<A>
	 * @param actor A
	 * @param local LocalObjects
	 * @param config ConfigAI
	 * @param currentTime long
	 */
	@Override
	public <A extends Npc> void addNewTask(NpcAI<A> ai, A actor, LocalObjects local, ConfigAI config, long currentTime)
	{
		final Character target = ai.getTarget();
		
		if (target == null)
		{
			return;
		}
		
		final int shortRange = getShortRange();
		
		if (actor.isInRange(target, shortRange) && Rnd.chance(getRunAwayRate()))
		{
			final int newDist = shortRange * 2;
			final float radians = Angles.headingToRadians(target.calcHeading(actor.getX(), actor.getY()) + Rnd.nextInt(-6000, 6000));
			final float newX = Coords.calcX(target.getX(), newDist, radians);
			final float newY = Coords.calcY(target.getY(), newDist, radians);
			final GeoManager geoManager = GeoManager.getInstance();
			final float newZ = geoManager.getHeight(target.getContinentId(), newX, newY, target.getZ());
			ai.addMoveTask(newX, newY, newZ, true);
			return;
		}
		
		super.addNewTask(ai, actor, local, config, currentTime);
	}
	
	/**
	 * Method getRunAwayRate.
	 * @return int
	 */
	public int getRunAwayRate()
	{
		return runAwayRate;
	}
}
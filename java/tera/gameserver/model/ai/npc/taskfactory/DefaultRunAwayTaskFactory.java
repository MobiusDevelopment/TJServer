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

import rlib.geom.Coords;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class DefaultRunAwayTaskFactory extends AbstractTaskFactory
{
	protected final int offset;
	
	/**
	 * Constructor for DefaultRunAwayTaskFactory.
	 * @param node Node
	 */
	public DefaultRunAwayTaskFactory(Node node)
	{
		super(node);
		final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
		offset = vars.getInteger("offset", ConfigAI.DEFAULT_RUN_AWAY_OFFSET);
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
		
		if ((target == null) || target.isDead() || !actor.isInRange(target, getOffset()))
		{
			return;
		}
		
		final int heading = target.calcHeading(actor.getX(), actor.getY());
		final float targetX = Coords.calcX(target.getX(), getOffset(), heading);
		final float targetY = Coords.calcY(target.getY(), getOffset(), heading);
		final GeoManager geoManager = GeoManager.getInstance();
		final float targetZ = geoManager.getHeight(actor.getContinentId(), targetX, targetY, actor.getZ());
		
		if (actor.getRunSpeed() > 10)
		{
			ai.addMoveTask(targetX, targetY, targetZ, true);
		}
		else
		{
			actor.teleToLocation(actor.getContinentId(), targetX, targetY, targetZ);
		}
	}
	
	/**
	 * Method getOffset.
	 * @return int
	 */
	protected final int getOffset()
	{
		return offset;
	}
}
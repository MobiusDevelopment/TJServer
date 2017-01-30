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
package tera.gameserver.model.ai.npc.thinkaction;

import org.w3c.dom.Node;

import tera.gameserver.model.NpcAIState;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.util.LocalObjects;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class DefaultReturnAction extends AbstractThinkAction
{
	protected final int distanceToSpawnLoc;
	protected final int distanceToTeleport;
	
	/**
	 * Constructor for DefaultReturnAction.
	 * @param node Node
	 */
	public DefaultReturnAction(Node node)
	{
		super(node);
		final VarTable vars = VarTable.newInstance(node);
		distanceToSpawnLoc = vars.getInteger("distanceToSpawnLoc", ConfigAI.DEFAULT_DISTANCE_TO_SPAWN_LOC);
		distanceToTeleport = vars.getInteger("distanceToTeleport", ConfigAI.DEFAULT_DISTANCE_TO_TELEPORT);
	}
	
	/**
	 * Method getDistanceToSpawnLoc.
	 * @return int
	 */
	public int getDistanceToSpawnLoc()
	{
		return distanceToSpawnLoc;
	}
	
	/**
	 * Method getDistanceToTeleport.
	 * @return int
	 */
	public int getDistanceToTeleport()
	{
		return distanceToTeleport;
	}
	
	/**
	 * Method think.
	 * @param ai NpcAI<A>
	 * @param actor A
	 * @param local LocalObjects
	 * @param config ConfigAI
	 * @param currentTime long
	 */
	@Override
	public <A extends Npc> void think(NpcAI<A> ai, A actor, LocalObjects local, ConfigAI config, long currentTime)
	{
		if (actor.isDead())
		{
			ai.clearTaskList();
			actor.clearAggroList();
			ai.setNewState(NpcAIState.WAIT);
			return;
		}
		
		final int maxHp = actor.getMaxHp();
		
		if (actor.getCurrentHp() < maxHp)
		{
			actor.setCurrentHp(maxHp);
			actor.updateHp();
		}
		
		if (actor.isTurner() || actor.isCastingNow() || actor.isMoving() || actor.isStuned() || actor.isOwerturned())
		{
			return;
		}
		
		actor.clearAggroList();
		ai.clearTaskList();
		ai.setClearAggro(0);
		
		if (actor.isInRange(actor.getSpawnLoc(), getDistanceToSpawnLoc()))
		{
			ai.setNewState(NpcAIState.WAIT);
			return;
		}
		
		if (ai.isWaitingTask())
		{
			ai.doTask(actor, currentTime, local);
			return;
		}
		
		ai.getCurrentFactory().addNewTask(ai, actor, local, config, currentTime);
		
		if (ai.isWaitingTask())
		{
			ai.doTask(actor, currentTime, local);
		}
	}
}
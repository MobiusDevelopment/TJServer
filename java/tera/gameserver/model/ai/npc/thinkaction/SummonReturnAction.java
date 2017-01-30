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

import tera.gameserver.model.Character;
import tera.gameserver.model.NpcAIState;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.util.LocalObjects;

/**
 * @author Ronn
 */
public class SummonReturnAction extends DefaultReturnAction
{
	/**
	 * Constructor for SummonReturnAction.
	 * @param node Node
	 */
	public SummonReturnAction(Node node)
	{
		super(node);
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
		
		if (actor.isTurner() || actor.isCastingNow() || actor.isMoving() || actor.isStuned() || actor.isOwerturned())
		{
			return;
		}
		
		actor.clearAggroList();
		ai.clearTaskList();
		final Character owner = actor.getOwner();
		
		if ((owner == null) || actor.isInRange(owner, getDistanceToSpawnLoc()))
		{
			ai.setNewState(NpcAIState.WAIT);
			return;
		}
		
		if ((owner.getContinentId() != actor.getContinentId()) || !owner.isInRange(actor, getDistanceToTeleport()) || (actor.getRunSpeed() < 10))
		{
			actor.teleToLocation(owner.getContinentId(), owner.getX(), owner.getY(), owner.getZ(), 0);
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
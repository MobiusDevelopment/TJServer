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

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class DefaultRunAwayAction extends AbstractThinkAction
{
	protected final int lastAttackedTime;
	protected final int battleMaxRange;
	
	/**
	 * Constructor for DefaultRunAwayAction.
	 * @param node Node
	 */
	public DefaultRunAwayAction(Node node)
	{
		super(node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			lastAttackedTime = vars.getInteger("lastAttackedTime", ConfigAI.DEFAULT_LAST_ATTACKED_TIME);
			battleMaxRange = vars.getInteger("battleMaxRange", ConfigAI.DEFAULT_BATTLE_MAX_RANGE);
		}
		catch (Exception e)
		{
			log.warning(this, e);
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Method getBattleMaxRange.
	 * @return int
	 */
	protected final int getBattleMaxRange()
	{
		return battleMaxRange;
	}
	
	/**
	 * Method getLastAttackedTime.
	 * @return int
	 */
	protected final int getLastAttackedTime()
	{
		return lastAttackedTime;
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
		
		if ((currentTime - ai.getLastAttacked()) > getLastAttackedTime())
		{
			ai.clearTaskList();
			actor.clearAggroList();
			ai.setNewState(NpcAIState.WAIT);
			return;
		}
		
		final Character mostHated = actor.getMostHated();
		
		if (mostHated == null)
		{
			ai.clearTaskList();
			ai.setNewState(NpcAIState.WAIT);
			return;
		}
		
		if (mostHated.isDead() || !actor.isInRange(mostHated, getBattleMaxRange()))
		{
			actor.removeAggro(mostHated);
			return;
		}
		
		ai.setTarget(mostHated);
		ai.getCurrentFactory().addNewTask(ai, actor, local, config, currentTime);
		
		if (ai.isWaitingTask())
		{
			ai.doTask(actor, currentTime, local);
		}
	}
}
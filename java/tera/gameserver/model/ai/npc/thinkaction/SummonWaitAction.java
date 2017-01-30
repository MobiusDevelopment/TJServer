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

import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.NpcAIState;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.network.serverpackets.NotifyCharacter.NotifyType;
import tera.util.LocalObjects;

import rlib.util.Rnd;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class SummonWaitAction extends AbstractThinkAction
{
	protected final int maxDistance;
	protected final int randomWalkMinDelay;
	protected final int randomWalkMaxDelay;
	
	/**
	 * Constructor for SummonWaitAction.
	 * @param node Node
	 */
	public SummonWaitAction(Node node)
	{
		super(node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			maxDistance = vars.getInteger("maxDistance", ConfigAI.DEFAULT_RANDOM_MAX_WALK_RANGE);
			randomWalkMinDelay = vars.getInteger("randomWalkMinDelay", ConfigAI.DEFAULT_RANDOM_MIN_WALK_DELAY);
			randomWalkMaxDelay = vars.getInteger("randomWalkMaxDelay", ConfigAI.DEFAULT_RANDOM_MAX_WALK_DELAY);
		}
		catch (Exception e)
		{
			log.warning(this, e);
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Method getRandomWalkMaxDelay.
	 * @return int
	 */
	protected final int getRandomWalkMaxDelay()
	{
		return randomWalkMaxDelay;
	}
	
	/**
	 * Method getMaxDistance.
	 * @return int
	 */
	public int getMaxDistance()
	{
		return maxDistance;
	}
	
	/**
	 * Method getRandomWalkMinDelay.
	 * @return int
	 */
	protected final int getRandomWalkMinDelay()
	{
		return randomWalkMinDelay;
	}
	
	/**
	 * Method startAITask.
	 * @param ai NpcAI<A>
	 * @param actor A
	 * @param local LocalObjects
	 * @param config ConfigAI
	 * @param currentTime long
	 */
	@Override
	public <A extends Npc> void startAITask(NpcAI<A> ai, A actor, LocalObjects local, ConfigAI config, long currentTime)
	{
		ai.setNextRandomWalk(currentTime + Rnd.nextInt(getRandomWalkMinDelay(), getRandomWalkMaxDelay()));
		ai.setClearAggro(0);
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
		final Character owner = actor.getOwner();
		
		if ((owner == null) || actor.isDead() || actor.isCastingNow() || actor.isStuned() || actor.isOwerturned())
		{
			return;
		}
		
		final Character target = ai.getTarget();
		
		if (target != null)
		{
			actor.stopMove();
			ai.clearTaskList();
			ai.setNewState(NpcAIState.IN_BATTLE);
			PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_SUB_AGRRESSION);
			ai.setLastNotifyIcon(currentTime);
			return;
		}
		
		if (!owner.isInRange(actor, getMaxDistance()))
		{
			if (ai.getCurrentState() == NpcAIState.RETURN_TO_HOME)
			{
				return;
			}
			
			ai.setTarget(null);
			ai.clearTaskList();
			ai.setNewState(NpcAIState.RETURN_TO_HOME);
			PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_THINK);
			ai.setLastNotifyIcon(currentTime);
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
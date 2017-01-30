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
import tera.gameserver.model.ai.npc.MessagePackage;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.network.serverpackets.NotifyCharacter.NotifyType;
import tera.gameserver.tables.MessagePackageTable;
import tera.util.LocalObjects;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class SummonBattleAction extends AbstractThinkAction
{
	protected final MessagePackage switchTargetMessages;
	protected final int battleMaxRange;
	protected final int reactionMaxRange;
	
	/**
	 * Constructor for SummonBattleAction.
	 * @param node Node
	 */
	public SummonBattleAction(Node node)
	{
		super(node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			battleMaxRange = vars.getInteger("battleMaxRange", ConfigAI.DEFAULT_BATTLE_MAX_RANGE);
			reactionMaxRange = vars.getInteger("reactionMaxRange", ConfigAI.DEFAULT_REACTION_MAX_RANGE);
			final MessagePackageTable messageTable = MessagePackageTable.getInstance();
			switchTargetMessages = messageTable.getPackage(vars.getString("switchTargetMessages", ConfigAI.DEFAULT_SWITCH_TARGET_MESSAGES));
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
	 * Method getReactionMaxRange.
	 * @return int
	 */
	protected final int getReactionMaxRange()
	{
		return reactionMaxRange;
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
		
		if ((owner == null) || actor.isDead())
		{
			ai.clearTaskList();
			actor.clearAggroList();
			ai.setNewState(NpcAIState.WAIT);
			return;
		}
		
		if (actor.isTurner() || actor.isCastingNow() || actor.isStuned() || actor.isOwerturned())
		{
			return;
		}
		
		if (!actor.isInRangeZ(owner, getReactionMaxRange()))
		{
			ai.setTarget(null);
			ai.clearTaskList();
			actor.clearAggroList();
			ai.setNewState(NpcAIState.RETURN_TO_HOME);
			PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_THINK);
			ai.setLastNotifyIcon(currentTime);
			return;
		}
		
		final Character target = ai.getTarget();
		
		if (target == null)
		{
			ai.clearTaskList();
			actor.clearAggroList();
			ai.setNewState(NpcAIState.WAIT);
			PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_THINK);
			ai.setLastNotifyIcon(currentTime);
			return;
		}
		
		if (target.isDead() || !target.isInRange(owner, getBattleMaxRange()))
		{
			ai.abortAttack();
			return;
		}
		
		if (ai.isWaitingTask())
		{
			if (ai.doTask(actor, currentTime, local))
			{
				return;
			}
		}
		
		if (actor.isTurner() || actor.isCastingNow() || actor.isStuned() || actor.isOwerturned() || actor.isMoving())
		{
			return;
		}
		
		if ((currentTime - ai.getLastNotifyIcon()) > 15000)
		{
			PacketManager.showNotifyIcon(actor, NotifyType.YELLOW_QUESTION);
			ai.setLastNotifyIcon(currentTime);
		}
		
		ai.getCurrentFactory().addNewTask(ai, actor, local, config, currentTime);
		
		if (ai.isWaitingTask())
		{
			ai.doTask(actor, currentTime, local);
		}
	}
}
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
import tera.gameserver.model.World;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.MessagePackage;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.network.serverpackets.NotifyCharacter.NotifyType;
import tera.gameserver.tables.MessagePackageTable;
import tera.util.LocalObjects;

import rlib.util.Rnd;
import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class DefaultBattleAction extends AbstractThinkAction
{
	
	protected final MessagePackage switchTargetMessages;
	
	protected final int battleMaxRange;
	
	protected final int reactionMaxRange;
	
	protected final int criticalHp;
	
	protected final int rearRate;
	
	protected final int runAwayRate;
	
	protected final int maxMostHated;
	
	/**
	 * Constructor for DefaultBattleAction.
	 * @param node Node
	 */
	public DefaultBattleAction(Node node)
	{
		super(node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			battleMaxRange = vars.getInteger("battleMaxRange", ConfigAI.DEFAULT_BATTLE_MAX_RANGE);
			reactionMaxRange = vars.getInteger("reactionMaxRange", ConfigAI.DEFAULT_REACTION_MAX_RANGE);
			criticalHp = vars.getInteger("criticalHp", ConfigAI.DEFAULT_CRITICAL_HP);
			rearRate = vars.getInteger("rearRate", ConfigAI.DEFAULT_REAR_RATE);
			runAwayRate = vars.getInteger("runAwayRate", ConfigAI.DEFAULT_RUN_AWAY_RATE);
			maxMostHated = vars.getInteger("maxMostHated", ConfigAI.DEFAULT_MAX_MOST_HATED);
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
	 * Method getCriticalHp.
	 * @return int
	 */
	protected final int getCriticalHp()
	{
		return criticalHp;
	}
	
	/**
	 * Method getMaxMostHated.
	 * @return int
	 */
	protected final int getMaxMostHated()
	{
		return maxMostHated;
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
	 * Method getRearRate.
	 * @return int
	 */
	protected final int getRearRate()
	{
		return rearRate;
	}
	
	/**
	 * Method getRunAwayRate.
	 * @return int
	 */
	protected final int getRunAwayRate()
	{
		return runAwayRate;
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
		
		if (actor.isTurner() || actor.isCastingNow())
		{
			return;
		}
		
		if (actor.isStuned() || actor.isOwerturned())
		{
			if (ai.isWaitingTask())
			{
				ai.clearTaskList();
			}
			
			return;
		}
		
		if (!actor.isInRangeZ(actor.getSpawnLoc(), getReactionMaxRange()))
		{
			ai.clearTaskList();
			actor.clearAggroList();
			ai.setNewState(NpcAIState.RETURN_TO_HOME);
			PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_THINK);
			ai.setLastNotifyIcon(currentTime);
			return;
		}
		
		Character mostHated = actor.getMostHated();
		
		if ((mostHated == null) && actor.isAggressive())
		{
			final WorldRegion region = actor.getCurrentRegion();
			
			if (region != null)
			{
				final Array<Character> charList = local.getNextCharList();
				World.getAround(Character.class, charList, actor, actor.getAggroRange());
				final Character[] array = charList.array();
				
				for (int i = 0, length = charList.size(); i < length; i++)
				{
					final Character target = array[i];
					
					if (ai.checkAggression(target))
					{
						actor.addAggro(target, 1, false);
					}
				}
			}
		}
		
		mostHated = actor.getMostHated();
		
		if (mostHated == null)
		{
			ai.clearTaskList();
			actor.clearAggroList();
			ai.setNewState(NpcAIState.RETURN_TO_HOME);
			PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_THINK);
			ai.setLastNotifyIcon(currentTime);
			return;
		}
		
		final Character target = ai.getTarget();
		
		if (mostHated.isDead() || !mostHated.isInRange(actor.getSpawnLoc(), getBattleMaxRange()))
		{
			actor.removeAggro(mostHated);
			ai.clearTaskList();
			return;
		}
		
		if (actor.getCurrentHpPercent() < getCriticalHp())
		{
			final int rate = Rnd.nextInt(0, 100000);
			
			if (rate < getRearRate())
			{
				ai.clearTaskList();
				ai.setNewState(NpcAIState.IN_RAGE);
				PacketManager.showNotifyIcon(actor, NotifyType.READ_REAR);
				ai.setLastNotifyIcon(currentTime);
				return;
			}
		}
		
		if (mostHated != target)
		{
			ai.setTarget(mostHated);
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

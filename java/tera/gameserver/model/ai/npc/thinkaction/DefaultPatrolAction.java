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
import tera.gameserver.model.ai.CharacterAI;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.network.serverpackets.NotifyCharacter.NotifyType;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.util.VarTable;
import rlib.util.array.Array;

public class DefaultPatrolAction extends AbstractThinkAction
{
	protected final int waitDialog;
	protected final int maxMostHated;
	
	/**
	 * Constructor for DefaultPatrolAction.
	 * @param node Node
	 */
	public DefaultPatrolAction(Node node)
	{
		super(node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			waitDialog = vars.getInteger("waitDialog", 0);
			maxMostHated = vars.getInteger("maxMostHated", ConfigAI.DEFAULT_MAX_MOST_HATED);
		}
		catch (Exception e)
		{
			log.warning(this, e);
			throw new IllegalArgumentException(e);
		}
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
		if (ai.isActiveDialog() || actor.isDead() || actor.isCastingNow() || actor.isStuned() || actor.isOwerturned())
		{
			return;
		}
		
		final Character hated = actor.getMostHated();
		
		if (hated != null)
		{
			actor.stopMove();
			ai.clearTaskList();
			ai.setNewState(NpcAIState.IN_BATTLE);
			PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_SUB_AGRRESSION);
			ai.setLastNotifyIcon(currentTime);
			return;
		}
		
		if (actor.isAggressive())
		{
			final WorldRegion region = actor.getCurrentRegion();
			boolean find = false;
			
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
						final Array<Npc> hateList = target.getHateList();
						
						if (!actor.isMinion() && (hateList.size() > getMaxMostHated()))
						{
							int counter = 0;
							final Npc[] hatenpcs = hateList.array();
							
							for (int g = 0, size = hateList.size(); g < size; g++)
							{
								final Npc npc = hatenpcs[g];
								
								if ((npc == null) || (npc.getTemplate() != actor.getTemplate()))
								{
									continue;
								}
								
								final CharacterAI npcAI = npc.getAI();
								
								if (npcAI.getClass() != ai.getClass())
								{
									continue;
								}
								
								@SuppressWarnings("unchecked")
								final NpcAI<A> targetAI = (NpcAI<A>) npcAI;
								
								if (targetAI.getTarget() == target)
								{
									counter++;
								}
							}
							
							if (counter > getMaxMostHated())
							{
								continue;
							}
						}
						
						actor.addAggro(target, 1, false);
						find = true;
					}
				}
				
				if (find)
				{
					actor.stopMove();
					ai.clearTaskList();
					ai.setNewState(NpcAIState.IN_BATTLE);
					PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_AGRRESION);
					ai.setLastNotifyIcon(currentTime);
					return;
				}
			}
		}
		
		final Location[] route = actor.getRoute();
		
		if ((route == null) || (route.length < 2))
		{
			actor.stopMove();
			ai.clearTaskList();
			ai.setNewState(NpcAIState.WAIT);
			PacketManager.showNotifyIcon(actor, NotifyType.YELLOW_QUESTION);
			ai.setLastNotifyIcon(currentTime);
			return;
		}
		
		if (actor.isMoving())
		{
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
	
	/**
	 * Method getMaxMostHated.
	 * @return int
	 */
	protected final int getMaxMostHated()
	{
		return maxMostHated;
	}
}
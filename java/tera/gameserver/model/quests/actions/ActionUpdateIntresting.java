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
package tera.gameserver.model.quests.actions;

import org.w3c.dom.Node;

import tera.gameserver.model.World;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.util.LocalObjects;

import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class ActionUpdateIntresting extends AbstractQuestAction
{
	private final int npcId;
	private final int npcType;
	
	/**
	 * Constructor for ActionUpdateIntresting.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionUpdateIntresting(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		final VarTable vars = VarTable.newInstance(node);
		npcId = vars.getInteger("npcId", 0);
		npcType = vars.getInteger("npcType", 0);
	}
	
	/**
	 * Method apply.
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.QuestAction#apply(QuestEvent)
	 */
	@Override
	public void apply(QuestEvent event)
	{
		final Player player = event.getPlayer();
		
		if (player == null)
		{
			log.warning(this, "not found player");
			return;
		}
		
		if ((npcId == 0) && (npcType == 0))
		{
			final Npc npc = event.getNpc();
			
			if (npc == null)
			{
				log.warning(this, "not found npc for quest " + quest.getId());
				return;
			}
			
			npc.updateQuestInteresting(player, true);
		}
		else
		{
			final LocalObjects local = LocalObjects.get();
			final Array<Npc> around = World.getAround(Npc.class, local.getNextNpcList(), player);
			final Npc[] array = around.array();
			
			for (int i = 0, length = around.size(); i < length; i++)
			{
				final Npc npc = array[i];
				
				if ((npc == null) || (npc.getTemplateId() != npcId) || (npc.getTemplateType() != npcType))
				{
					continue;
				}
				
				npc.updateQuestInteresting(player, true);
			}
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ActionUpdateIntresting ";
	}
}
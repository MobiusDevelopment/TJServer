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

import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestState;
import tera.gameserver.network.serverpackets.QuestStarted;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class ActionStateQuest extends AbstractQuestAction
{
	private int npcId;
	private int npcType;
	private float x;
	private float y;
	private float z;
	
	/**
	 * Constructor for ActionStateQuest.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionStateQuest(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			npcId = vars.getInteger("npcId");
			npcType = vars.getInteger("npcType");
			x = vars.getFloat("x");
			y = vars.getFloat("y");
			z = vars.getFloat("z");
		}
		catch (Exception e)
		{
			log.warning(this, e);
		}
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
		final Quest quest = event.getQuest();
		
		if ((player == null) || (quest == null))
		{
			log.warning(this, "not found player or quest");
			return;
		}
		
		final QuestList questList = player.getQuestList();
		
		if (questList == null)
		{
			log.warning(this, "not found quest list");
			return;
		}
		
		final QuestState state = questList.getQuestState(quest);
		
		if (state != null)
		{
			player.sendPacket(QuestStarted.getInstance(state, npcType, npcId, x, y, z), true);
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ActionStateQuest npcId = " + npcId + ", npcType = " + npcType + ", x = " + x + ", y = " + y + ", z = " + z;
	}
}
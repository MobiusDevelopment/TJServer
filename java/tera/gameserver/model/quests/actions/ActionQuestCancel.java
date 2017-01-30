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

import tera.gameserver.manager.QuestManager;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class ActionQuestCancel extends AbstractQuestAction
{
	private final int id;
	
	/**
	 * Constructor for ActionQuestCancel.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionQuestCancel(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		final VarTable vars = VarTable.newInstance(node);
		id = vars.getInteger("id", 0);
	}
	
	/**
	 * Method apply.
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.QuestAction#apply(QuestEvent)
	 */
	@Override
	public void apply(QuestEvent event)
	{
		if (id == 0)
		{
			quest.cancel(event, false);
		}
		else
		{
			final QuestManager questManager = QuestManager.getInstance();
			final Quest quest = questManager.getQuest(id);
			
			if (quest == null)
			{
				log.warning(this, "not found quest");
				return;
			}
			
			quest.cancel(event, false);
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ActionQuestStart id = " + id;
	}
}
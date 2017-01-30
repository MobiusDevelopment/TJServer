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
package tera.gameserver.model.quests.events;

import org.w3c.dom.Node;

import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestAction;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestEventType;

/**
 * @author Ronn
 */
public class AcceptedQuestListener extends AbstractQuestEventListener
{
	/**
	 * Constructor for AcceptedQuestListener.
	 * @param type QuestEventType
	 * @param actions QuestAction[]
	 * @param quest Quest
	 * @param node Node
	 */
	public AcceptedQuestListener(QuestEventType type, QuestAction[] actions, Quest quest, Node node)
	{
		super(type, actions, quest, node);
	}
	
	/**
	 * Method notifyQuest.
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.QuestEventListener#notifyQuest(QuestEvent)
	 */
	@Override
	public void notifyQuest(QuestEvent event)
	{
		if (event.getQuest().getId() == quest.getId())
		{
			super.notifyQuest(event);
		}
	}
}
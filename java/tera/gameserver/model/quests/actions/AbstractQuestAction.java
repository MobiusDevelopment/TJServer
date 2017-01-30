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

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestAction;
import tera.gameserver.model.quests.QuestActionType;

import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public abstract class AbstractQuestAction implements QuestAction
{
	protected static final Logger log = Loggers.getLogger(AbstractQuestAction.class);
	protected QuestActionType type;
	protected Quest quest;
	protected Condition condition;
	
	/**
	 * Constructor for AbstractQuestAction.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public AbstractQuestAction(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		this.type = type;
		this.quest = quest;
		this.condition = condition;
	}
	
	/**
	 * Method getType.
	 * @return QuestActionType
	 * @see tera.gameserver.model.quests.QuestAction#getType()
	 */
	@Override
	public QuestActionType getType()
	{
		return type;
	}
	
	/**
	 * Method test.
	 * @param npc Npc
	 * @param player Player
	 * @return boolean
	 * @see tera.gameserver.model.quests.QuestAction#test(Npc, Player)
	 */
	@Override
	public boolean test(Npc npc, Player player)
	{
		return (condition == null) || condition.test(npc, player);
	}
}
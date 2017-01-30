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
package tera.gameserver.model.quests;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Node;

import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.quests.actions.ActionAddExp;
import tera.gameserver.model.quests.actions.ActionAddItem;
import tera.gameserver.model.quests.actions.ActionAddReward;
import tera.gameserver.model.quests.actions.ActionAddVar;
import tera.gameserver.model.quests.actions.ActionClearVar;
import tera.gameserver.model.quests.actions.ActionDropItem;
import tera.gameserver.model.quests.actions.ActionEventMessage;
import tera.gameserver.model.quests.actions.ActionFinishQuest;
import tera.gameserver.model.quests.actions.ActionMoveToPanel;
import tera.gameserver.model.quests.actions.ActionQuestCancel;
import tera.gameserver.model.quests.actions.ActionQuestFinish;
import tera.gameserver.model.quests.actions.ActionQuestMovie;
import tera.gameserver.model.quests.actions.ActionQuestStart;
import tera.gameserver.model.quests.actions.ActionRemoveItem;
import tera.gameserver.model.quests.actions.ActionSetNpcIcon;
import tera.gameserver.model.quests.actions.ActionSetQuestState;
import tera.gameserver.model.quests.actions.ActionShowQuestInfo;
import tera.gameserver.model.quests.actions.ActionStateQuest;
import tera.gameserver.model.quests.actions.ActionSubVar;
import tera.gameserver.model.quests.actions.ActionSystemMessage;
import tera.gameserver.model.quests.actions.ActionUpdateCounter;
import tera.gameserver.model.quests.actions.ActionUpdateIntresting;
import tera.gameserver.model.quests.actions.ActionUpdateItemCounter;

import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public enum QuestActionType
{
	QUEST_START(ActionStateQuest.class),
	QUEST_MOVE_TO_PANEL(ActionMoveToPanel.class),
	QUEST_MOVIE(ActionQuestMovie.class),
	QUEST_FINISH(ActionFinishQuest.class),
	EVENT_MESSAGE(ActionEventMessage.class),
	SYSTEM_MESSAGE(ActionSystemMessage.class),
	START_QUEST(ActionQuestStart.class),
	FINISH_QUEST(ActionQuestFinish.class),
	CANCEL_QUEST(ActionQuestCancel.class),
	ADD_VAR(ActionAddVar.class),
	ADD_EXP(ActionAddExp.class),
	ADD_ITEM(ActionAddItem.class),
	ADD_REWARD(ActionAddReward.class),
	SUB_VAR(ActionSubVar.class),
	DROP_ITEM(ActionDropItem.class),
	REMOVE_ITEM(ActionRemoveItem.class),
	QUEST_STATE(ActionSetQuestState.class),
	SET_NPC_ICON(ActionSetNpcIcon.class),
	UPDATE_INTRESTING(ActionUpdateIntresting.class),
	UPDATE_COUNTER(ActionUpdateCounter.class),
	UPDATE_ITEM_COUNTER(ActionUpdateItemCounter.class),
	CLEAR_VAR(ActionClearVar.class),
	SHOW_QUEST_INFO(ActionShowQuestInfo.class);
	private Constructor<? extends QuestAction> constructor;
	
	/**
	 * Constructor for QuestActionType.
	 * @param eventClass Class<? extends QuestAction>
	 */
	private QuestActionType(Class<? extends QuestAction> eventClass)
	{
		try
		{
			constructor = eventClass.getConstructor(getClass(), Quest.class, Condition.class, Node.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Loggers.warning(this, e);
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Method newInstance.
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 * @return QuestAction
	 */
	public QuestAction newInstance(Quest quest, Condition condition, Node node)
	{
		try
		{
			return constructor.newInstance(this, quest, condition, node);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, " quest id " + quest.getId());
			Loggers.warning(this, e);
			return null;
		}
	}
}
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

import tera.gameserver.model.quests.events.AcceptedQuestListener;
import tera.gameserver.model.quests.events.AddNpcListener;
import tera.gameserver.model.quests.events.CanceledQuestListener;
import tera.gameserver.model.quests.events.CollectResourseListener;
import tera.gameserver.model.quests.events.EmptyListener;
import tera.gameserver.model.quests.events.FinishedQuestListener;
import tera.gameserver.model.quests.events.InventoryAddItemListener;
import tera.gameserver.model.quests.events.InventoryRemoveItemListener;
import tera.gameserver.model.quests.events.KillNpcListener;
import tera.gameserver.model.quests.events.LinkSelectListener;
import tera.gameserver.model.quests.events.PickUpItemListener;
import tera.gameserver.model.quests.events.QuestMovieListener;
import tera.gameserver.model.quests.events.SkillLearnListener;
import tera.gameserver.model.quests.events.UseItemListener;

import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public enum QuestEventType
{
	FINISHED_QUEST(FinishedQuestListener.class),
	ACCEPTED_QUEST(AcceptedQuestListener.class),
	CANCELED_QUEST(CanceledQuestListener.class),
	QUEST_MOVIE_ENDED(QuestMovieListener.class),
	KILL_NPC(KillNpcListener.class),
	COLLECT_RESOURSE(CollectResourseListener.class),
	CHANGED_HEART(EmptyListener.class),
	USE_ITEM(UseItemListener.class),
	SELECT_BUTTON(LinkSelectListener.class),
	PICK_UP_ITEM(PickUpItemListener.class),
	INVENTORY_ADD_ITEM(InventoryAddItemListener.class),
	INVENTORY_REMOVE_ITEM(InventoryRemoveItemListener.class),
	PLAYER_SPAWN(EmptyListener.class),
	ADD_NPC(AddNpcListener.class),
	SKILL_LEARNED(SkillLearnListener.class),
	SELECT_LINK(LinkSelectListener.class);
	private Constructor<? extends QuestEventListener> constructor;
	
	/**
	 * Constructor for QuestEventType.
	 * @param eventClass Class<? extends QuestEventListener>
	 */
	private QuestEventType(Class<? extends QuestEventListener> eventClass)
	{
		try
		{
			constructor = eventClass.getConstructor(getClass(), QuestAction[].class, Quest.class, Node.class);
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
	 * @param actions QuestAction[]
	 * @param node Node
	 * @return QuestEventListener
	 */
	public QuestEventListener newInstance(Quest quest, QuestAction[] actions, Node node)
	{
		try
		{
			return constructor.newInstance(this, actions, quest, node);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			return null;
		}
	}
}
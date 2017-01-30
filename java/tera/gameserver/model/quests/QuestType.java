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

import tera.gameserver.model.quests.classes.DealyQuest;
import tera.gameserver.model.quests.classes.LevelUpQuest;
import tera.gameserver.model.quests.classes.StoryQuest;
import tera.gameserver.model.quests.classes.ZoneQuest;

import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public enum QuestType
{
	ZONE_QUEST(ZoneQuest.class, true),
	GUILD_QUEST(StoryQuest.class, true),
	DEALY_QUEST(DealyQuest.class, true),
	LEVEL_UP_QUEST(LevelUpQuest.class, false),
	STORY_QUEST(StoryQuest.class, true);
	private Constructor<? extends Quest> constructor;
	private boolean cancelable;
	
	/**
	 * Constructor for QuestType.
	 * @param questClass Class<? extends Quest>
	 * @param cancelable boolean
	 */
	private QuestType(Class<? extends Quest> questClass, boolean cancelable)
	{
		try
		{
			constructor = questClass.getConstructor(QuestType.class, Node.class);
			this.cancelable = cancelable;
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Loggers.warning(this, e);
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Method isCancelable.
	 * @return boolean
	 */
	public boolean isCancelable()
	{
		return cancelable;
	}
	
	/**
	 * Method newInstance.
	 * @param node Node
	 * @return Quest
	 */
	public Quest newInstance(Node node)
	{
		try
		{
			return constructor.newInstance(this, node);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, e);
		}
		
		return null;
	}
}
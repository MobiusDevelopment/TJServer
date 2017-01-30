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
package tera.gameserver.manager;

import java.io.File;

import tera.Config;
import tera.gameserver.document.DocumentQuest;
import tera.gameserver.model.quests.Quest;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.array.Array;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class QuestManager
{
	protected static final Logger log = Loggers.getLogger(QuestManager.class);
	
	private static QuestManager instance;
	
	/**
	 * Method getInstance.
	 * @return QuestManager
	 */
	public static QuestManager getInstance()
	{
		if (instance == null)
		{
			instance = new QuestManager();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Quest> allquests;
	
	private QuestManager()
	{
		allquests = Tables.newIntegerTable();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/quests"));
		
		for (File file : files)
		{
			if (!file.getName().endsWith(".xml"))
			{
				continue;
			}
			
			final Array<Quest> quests = new DocumentQuest(file).parse();
			
			for (Quest quest : quests)
			{
				allquests.put(quest.getId(), quest);
			}
		}
		
		log.info("loaded " + allquests.size() + " quests.");
	}
	
	/**
	 * Method getQuest.
	 * @param id int
	 * @return Quest
	 */
	public Quest getQuest(int id)
	{
		return allquests.get(id);
	}
	
	public void reload()
	{
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/quests"));
		
		for (File file : files)
		{
			if (!file.getName().endsWith(".xml"))
			{
				continue;
			}
			
			final Array<Quest> quests = new DocumentQuest(file).parse();
			
			for (Quest quest : quests)
			{
				final Quest old = allquests.get(quest.getId());
				
				if (old == null)
				{
					allquests.put(quest.getId(), quest);
				}
				else
				{
					old.reload(quest);
				}
			}
		}
		
		log.info("reloaded.");
	}
}

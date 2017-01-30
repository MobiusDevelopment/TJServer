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
package tera.gameserver.tables;

import java.io.File;

import tera.Config;
import tera.gameserver.document.DocumentMessagePackage;
import tera.gameserver.model.ai.npc.MessagePackage;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.array.Array;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class MessagePackageTable
{
	private static final Logger log = Loggers.getLogger(MessagePackageTable.class);
	private static MessagePackageTable instance;
	
	/**
	 * Method getInstance.
	 * @return MessagePackageTable
	 */
	public static MessagePackageTable getInstance()
	{
		if (instance == null)
		{
			instance = new MessagePackageTable();
		}
		
		return instance;
	}
	
	private final Table<String, MessagePackage> table;
	
	private MessagePackageTable()
	{
		table = Tables.newObjectTable();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/messages_ai"), "xml");
		
		for (File file : files)
		{
			final Array<MessagePackage> result = new DocumentMessagePackage(file).parse();
			
			for (MessagePackage pckg : result)
			{
				if (table.containsKey(pckg.getName()))
				{
					log.warning(new Exception("found duplicate message package " + pckg.getName()));
					continue;
				}
				
				table.put(pckg.getName(), pckg);
			}
		}
		
		log.info("loaded " + table.size() + " message packages.");
	}
	
	/**
	 * Method getPackage.
	 * @param name String
	 * @return MessagePackage
	 */
	public MessagePackage getPackage(String name)
	{
		return table.get(name);
	}
}
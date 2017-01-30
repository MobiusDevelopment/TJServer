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
import tera.gameserver.document.DocumentNpcConfigAI;
import tera.gameserver.model.ai.npc.ConfigAI;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.array.Array;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class ConfigAITable
{
	private static final Logger log = Loggers.getLogger(ConfigAITable.class);
	private static ConfigAITable instance;
	
	/**
	 * Method getInstance.
	 * @return ConfigAITable
	 */
	public static ConfigAITable getInstance()
	{
		if (instance == null)
		{
			instance = new ConfigAITable();
		}
		
		return instance;
	}
	
	private final Table<String, ConfigAI> configs;
	
	private ConfigAITable()
	{
		configs = Tables.newObjectTable();
		
		for (File file : Files.getFiles(new File(Config.SERVER_DIR + "/data/config_ai"), "xml"))
		{
			if (!file.getName().endsWith(".xml"))
			{
				log.warning("detected once the file " + file);
				continue;
			}
			
			final Array<ConfigAI> result = new DocumentNpcConfigAI(file).parse();
			
			for (ConfigAI config : result)
			{
				if (configs.containsKey(config.getName()))
				{
					log.warning(new Exception("found duplicate config " + config.getName()));
					continue;
				}
				
				configs.put(config.getName(), config);
			}
		}
		
		log.info("loaded " + configs.size() + " npc ai configs.");
	}
	
	/**
	 * Method getConfig.
	 * @param name String
	 * @return ConfigAI
	 */
	public ConfigAI getConfig(String name)
	{
		return configs.get(name);
	}
}
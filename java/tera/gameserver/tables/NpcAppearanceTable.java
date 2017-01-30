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
import tera.gameserver.document.DocumentNpcAppearance;
import tera.gameserver.model.npc.playable.NpcAppearance;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class NpcAppearanceTable
{
	private static final Logger log = Loggers.getLogger(NpcAppearanceTable.class);
	private static NpcAppearanceTable instance;
	
	/**
	 * Method getInstance.
	 * @return NpcAppearanceTable
	 */
	public static NpcAppearanceTable getInstance()
	{
		if (instance == null)
		{
			instance = new NpcAppearanceTable();
		}
		
		return instance;
	}
	
	private final Table<String, NpcAppearance> table;
	
	private NpcAppearanceTable()
	{
		table = Tables.newObjectTable();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/npc_appearance"), "xml");
		
		for (File file : files)
		{
			final DocumentNpcAppearance document = new DocumentNpcAppearance(file, table);
			document.parse();
		}
		
		log.info("initialized. Loaded " + table.size() + " appearances.");
	}
	
	/**
	 * Method getAppearance.
	 * @param name String
	 * @return NpcAppearance
	 */
	public NpcAppearance getAppearance(String name)
	{
		return table.get(name);
	}
}
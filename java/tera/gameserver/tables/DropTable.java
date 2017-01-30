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
import java.util.Set;

import tera.Config;
import tera.gameserver.document.DocumentDrop;
import tera.gameserver.model.drop.Drop;
import tera.gameserver.model.drop.NpcDrop;
import tera.gameserver.model.drop.ResourseDrop;

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
public final class DropTable
{
	private static final Logger log = Loggers.getLogger(DropTable.class);
	private static DropTable instance;
	
	/**
	 * Method getInstance.
	 * @return DropTable
	 */
	public static DropTable getInstance()
	{
		if (instance == null)
		{
			instance = new DropTable();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Table<IntKey, NpcDrop>> npcDrop;
	private final Table<IntKey, ResourseDrop> resourseDrop;
	
	private DropTable()
	{
		npcDrop = Tables.newIntegerTable();
		resourseDrop = Tables.newIntegerTable();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/drops"));
		
		for (File file : files)
		{
			if (!file.getName().endsWith(".xml"))
			{
				log.warning("detected once the file " + file.getPath());
				continue;
			}
			
			if (file.getName().startsWith("example"))
			{
				continue;
			}
			
			final Array<Drop> parsed = new DocumentDrop(file).parse();
			
			for (Drop drop : parsed)
			{
				if (drop.getTemplateType() < 0)
				{
					resourseDrop.put(drop.getTemplateId(), (ResourseDrop) drop);
				}
				else
				{
					Table<IntKey, NpcDrop> table = npcDrop.get(drop.getTemplateId());
					
					if (table == null)
					{
						table = Tables.newIntegerTable();
						npcDrop.put(drop.getTemplateId(), table);
					}
					
					table.put(drop.getTemplateType(), (NpcDrop) drop);
				}
			}
		}
		
		int counter = 0;
		
		for (Table<IntKey, NpcDrop> table : npcDrop)
		{
			counter += table.size();
		}
		
		final Set<Integer> filter = DocumentDrop.filter;
		
		if (!filter.isEmpty())
		{
			log.warning("not found items " + filter);
		}
		
		log.info("load drop for " + counter + " npcs and " + resourseDrop.size() + " for resourse.");
	}
	
	/**
	 * Method getNpcDrop.
	 * @param templateId int
	 * @param templateType int
	 * @return NpcDrop
	 */
	public NpcDrop getNpcDrop(int templateId, int templateType)
	{
		final Table<IntKey, NpcDrop> table = npcDrop.get(templateId);
		return table == null ? null : table.get(templateType);
	}
	
	/**
	 * Method getResourseDrop.
	 * @param templateId int
	 * @return ResourseDrop
	 */
	public ResourseDrop getResourseDrop(int templateId)
	{
		return resourseDrop.get(templateId);
	}
}
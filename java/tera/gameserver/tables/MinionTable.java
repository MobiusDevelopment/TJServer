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
import tera.gameserver.document.DocumentMinion;
import tera.gameserver.model.MinionData;
import tera.gameserver.templates.NpcTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.array.Array;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 * @created 14.03.2012
 */
public final class MinionTable
{
	private static final Logger log = Loggers.getLogger(MinionTable.class);
	private static MinionTable instance;
	
	/**
	 * Method getInstance.
	 * @return MinionTable
	 */
	public static MinionTable getInstance()
	{
		if (instance == null)
		{
			instance = new MinionTable();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Table<IntKey, MinionData>> minions;
	
	private MinionTable()
	{
		minions = Tables.newIntegerTable();
		final NpcTable npcTable = NpcTable.getInstance();
		int counter = 0;
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/minions"));
		
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
			
			final Array<MinionData> parsed = new DocumentMinion(file).parse();
			
			for (MinionData minion : parsed)
			{
				Table<IntKey, MinionData> table = minions.get(minion.getLeaderId());
				
				if (table == null)
				{
					table = Tables.newIntegerTable();
					minions.put(minion.getLeaderId(), table);
				}
				
				table.put(minion.getType(), minion);
				counter += minion.size();
				final NpcTemplate template = npcTable.getTemplate(minion.getLeaderId(), minion.getType());
				
				if (template == null)
				{
					log.warning("not found npc template for " + minion);
					continue;
				}
				
				template.setMinions(minion);
			}
		}
		
		log.info("loaded " + counter + " minions for " + minions.size() + " npcs.");
	}
}
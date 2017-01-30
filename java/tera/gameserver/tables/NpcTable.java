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
import tera.gameserver.document.DocumentNpc;
import tera.gameserver.model.drop.NpcDrop;
import tera.gameserver.templates.NpcTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 * @created 05.03.2012
 */
public final class NpcTable
{
	private static final Logger log = Loggers.getLogger(NpcTable.class);
	
	public static final byte NPC_SKILL_CLASS_ID = -8;
	
	private static NpcTable instance;
	
	/**
	 * Method getInstance.
	 * @return NpcTable
	 */
	public static NpcTable getInstance()
	{
		if (instance == null)
		{
			instance = new NpcTable();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Table<IntKey, NpcTemplate>> templates;
	
	private NpcTable()
	{
		templates = Tables.newIntegerTable();
		final DropTable dropTable = DropTable.getInstance();
		final NpcDialogTable dialogTable = NpcDialogTable.getInstance();
		final Array<NpcTemplate> array = Arrays.toArray(NpcTemplate.class);
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/npcs"));
		
		for (File file : files)
		{
			if (!file.getName().endsWith(".xml"))
			{
				log.warning("detected once the file " + file.getPath());
				continue;
			}
			
			final Array<NpcTemplate> parsed = new DocumentNpc(file).parse();
			
			for (NpcTemplate template : parsed)
			{
				final NpcDrop drop = dropTable.getNpcDrop(template.getTemplateId(), template.getTemplateType());
				template.setCanDrop(drop != null);
				template.setDrop(drop);
				template.setDialog(dialogTable.getDialog(template.getTemplateId(), template.getTemplateType()));
				Table<IntKey, NpcTemplate> table = templates.get(template.getTemplateId());
				
				if (table == null)
				{
					table = Tables.newIntegerTable();
					templates.put(template.getTemplateId(), table);
				}
				
				if (table.containsKey(template.getTemplateType()))
				{
					log.warning("found duplicate npc template " + template + " in file " + file);
					continue;
				}
				
				table.put(template.getTemplateType(), template);
				array.add(template);
			}
		}
		
		log.info("loaded  " + templates.size() + " npcs.");
	}
	
	/**
	 * Method getTemplate.
	 * @param templateId int
	 * @param templateType int
	 * @return NpcTemplate
	 */
	public NpcTemplate getTemplate(int templateId, int templateType)
	{
		final Table<IntKey, NpcTemplate> table = templates.get(templateId);
		return table == null ? null : table.get(templateType);
	}
	
	public synchronized void reload()
	{
		final DropTable dropTable = DropTable.getInstance();
		final NpcDialogTable dialogTable = NpcDialogTable.getInstance();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/npcs"));
		
		for (File file : files)
		{
			if (!file.getName().endsWith(".xml"))
			{
				log.warning("detected once the file " + file.getPath());
				continue;
			}
			
			final Array<NpcTemplate> parsed = new DocumentNpc(file).parse();
			
			for (NpcTemplate template : parsed)
			{
				final NpcDrop drop = dropTable.getNpcDrop(template.getTemplateId(), template.getTemplateType());
				template.setCanDrop(drop != null);
				template.setDrop(drop);
				template.setDialog(dialogTable.getDialog(template.getTemplateId(), template.getTemplateType()));
				Table<IntKey, NpcTemplate> table = templates.get(template.getTemplateId());
				
				if (table == null)
				{
					table = Tables.newIntegerTable();
					templates.put(template.getTemplateId(), table);
				}
				
				final NpcTemplate old = table.get(template.getTemplateType());
				
				if (old != null)
				{
					old.reload(template);
				}
				else
				{
					table.put(template.getTemplateType(), template);
				}
			}
		}
		
		log.info("npcs reloaded.");
	}
}
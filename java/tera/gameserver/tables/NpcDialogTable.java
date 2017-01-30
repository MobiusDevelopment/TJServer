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
import tera.gameserver.document.DocumentDialog;
import tera.gameserver.model.npc.interaction.DialogData;
import tera.gameserver.model.npc.interaction.Link;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.Objects;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class NpcDialogTable
{
	private static final Logger log = Loggers.getLogger(NpcDialogTable.class);
	private static NpcDialogTable instance;
	
	/**
	 * Method getInstance.
	 * @return NpcDialogTable
	 */
	public static NpcDialogTable getInstance()
	{
		if (instance == null)
		{
			instance = new NpcDialogTable();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Table<IntKey, DialogData>> dialogs;
	
	private NpcDialogTable()
	{
		dialogs = Tables.newIntegerTable();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/dialogs"));
		
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
			
			final Table<IntKey, Table<IntKey, DialogData>> newDialogs = new DocumentDialog(file).parse();
			
			if (newDialogs == null)
			{
				continue;
			}
			
			for (Table<IntKey, DialogData> table : newDialogs)
			{
				for (DialogData dialog : table)
				{
					final Table<IntKey, DialogData> current = dialogs.get(dialog.getNpcId());
					
					if (current == null)
					{
						dialogs.put(dialog.getNpcId(), table);
						continue;
					}
					
					final DialogData old = current.get(dialog.getType());
					
					if (old != null)
					{
						old.setLinks(Arrays.combine(old.getLinks(), dialog.getLinks(), Link.class));
						continue;
					}
					
					current.put(dialog.getType(), dialog);
				}
			}
		}
		
		log.info("loaded " + dialogs.size() + " dialogs.");
	}
	
	/**
	 * Method getDialog.
	 * @param npcId int
	 * @param type int
	 * @return DialogData
	 */
	public DialogData getDialog(int npcId, int type)
	{
		final Table<IntKey, DialogData> table = dialogs.get(npcId);
		return table == null ? null : table.get(type);
	}
	
	public synchronized void reload()
	{
		final File[] files = Files.getFiles(new File("./data/dialogs"));
		
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
			
			final Table<IntKey, Table<IntKey, DialogData>> newDialogs = new DocumentDialog(file).parse();
			
			if (newDialogs == null)
			{
				continue;
			}
			
			for (Table<IntKey, DialogData> table : newDialogs)
			{
				for (DialogData dialog : table)
				{
					final Table<IntKey, DialogData> current = dialogs.get(dialog.getNpcId());
					
					if (current == null)
					{
						dialogs.put(dialog.getNpcId(), table);
						continue;
					}
					
					final DialogData old = current.get(dialog.getType());
					
					if (old != null)
					{
						Objects.reload(old, dialog);
					}
				}
			}
		}
		
		log.info("reloaded.");
	}
}
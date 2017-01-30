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
import tera.gameserver.document.DocumentItem;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.templates.ItemTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class ItemTable
{
	private static final Logger log = Loggers.getLogger(ItemTable.class);
	
	private static ItemTable instance;
	
	/**
	 * Method createItem.
	 * @param templateId int
	 * @param count int
	 * @return ItemInstance[]
	 */
	public static final ItemInstance[] createItem(int templateId, int count)
	{
		if (Arrays.contains(Config.WORLD_DONATE_ITEMS, templateId))
		{
			log.warning(new Exception("not create donate item for id " + templateId));
			return null;
		}
		
		if (count < 1)
		{
			return new ItemInstance[0];
		}
		
		final ItemTable table = getInstance();
		final ItemTemplate template = table.getItem(templateId);
		
		if (template == null)
		{
			return new ItemInstance[0];
		}
		
		final ItemInstance[] items = new ItemInstance[template.isStackable() ? 1 : count];
		
		for (int i = 0; i < items.length; i++)
		{
			items[i] = template.newInstance();
			
			if (items[i] == null)
			{
				return new ItemInstance[0];
			}
			
			if (template.isStackable())
			{
				items[i].setItemCount(count);
			}
		}
		
		return items;
	}
	
	/**
	 * Method createItem.
	 * @param templateId int
	 * @param count long
	 * @return ItemInstance
	 */
	public static final ItemInstance createItem(int templateId, long count)
	{
		if (Arrays.contains(Config.WORLD_DONATE_ITEMS, templateId))
		{
			log.warning(new Exception("not create donate item for id " + templateId));
			return null;
		}
		
		if (count < 1)
		{
			return null;
		}
		
		final ItemTable table = getInstance();
		final ItemTemplate template = table.getItem(templateId);
		
		if (template == null)
		{
			return null;
		}
		
		final ItemInstance item = template.newInstance();
		
		if (item == null)
		{
			return null;
		}
		
		item.setItemCount(item.isStackable() ? count : 1);
		return item;
	}
	
	/**
	 * Method getInstance.
	 * @return ItemTable
	 */
	public static ItemTable getInstance()
	{
		if (instance == null)
		{
			instance = new ItemTable();
		}
		
		return instance;
	}
	
	/**
	 * Method templateId.
	 * @param item ItemInstance
	 * @return int
	 */
	public static final int templateId(ItemInstance item)
	{
		if (item == null)
		{
			return 0;
		}
		
		return item.getItemId();
	}
	
	private final Table<IntKey, ItemTemplate> items;
	
	private ItemTable()
	{
		items = Tables.newIntegerTable();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/items"));
		
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
			
			for (ItemTemplate item : new DocumentItem(file).parse())
			{
				if ((item.getSellPrice() != 0) && (item.getSellPrice() > item.getBuyPrice()))
				{
					log.warning("found incorrect price for item " + item + " in file " + file);
					item.setSellPrice(0);
				}
				
				if (items.containsKey(item.getItemId()))
				{
					log.warning("found duplicate item " + item);
				}
				
				items.put(item.getItemId(), item);
			}
		}
		
		log.info("loaded " + items.size() + " items.");
	}
	
	/**
	 * Method getItem.
	 * @param <T>
	 * @param type Class<T>
	 * @param id int
	 * @return T
	 */
	public final <T extends ItemTemplate> T getItem(Class<T> type, int id)
	{
		final ItemTemplate item = items.get(id);
		
		if ((item == null) || !type.isInstance(item))
		{
			return null;
		}
		
		return type.cast(item);
	}
	
	/**
	 * Method getItem.
	 * @param id int
	 * @return ItemTemplate
	 */
	public final ItemTemplate getItem(int id)
	{
		return items.get(id);
	}
	
	public synchronized void reload()
	{
		final Table<IntKey, ItemTemplate> newTemplates = Tables.newIntegerTable();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/items"));
		
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
			
			for (ItemTemplate template : new DocumentItem(file).parse())
			{
				newTemplates.put(template.getItemId(), template);
			}
		}
		
		for (ItemTemplate template : newTemplates)
		{
			if (template == null)
			{
				continue;
			}
			
			final ItemTemplate old = items.get(template.getItemId());
			
			if (old == null)
			{
				items.put(template.getItemId(), template);
				continue;
			}
			
			old.reload(template);
		}
		
		log.info("reloaded items.");
	}
}

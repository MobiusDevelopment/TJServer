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
import tera.gameserver.document.DocumentResourse;
import tera.gameserver.model.drop.ResourseDrop;
import tera.gameserver.templates.ResourseTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.Objects;
import rlib.util.array.Array;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 * @created 05.03.2012
 */
public final class ResourseTable
{
	private static final Logger log = Loggers.getLogger(ResourseTable.class);
	
	private static ResourseTable instance;
	
	/**
	 * Method getInstance.
	 * @return ResourseTable
	 */
	public static ResourseTable getInstance()
	{
		if (instance == null)
		{
			instance = new ResourseTable();
		}
		
		return instance;
	}
	
	private final Table<IntKey, ResourseTemplate> templates;
	
	private ResourseTable()
	{
		templates = Tables.newIntegerTable();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/resourses"));
		final DropTable dropTable = DropTable.getInstance();
		
		for (File file : files)
		{
			if (!file.getName().endsWith(".xml"))
			{
				log.warning("detected once the file " + file.getPath());
				continue;
			}
			
			final Array<ResourseTemplate> parsed = new DocumentResourse(file).parse();
			
			for (ResourseTemplate template : parsed)
			{
				final ResourseDrop drop = dropTable.getResourseDrop(template.getId());
				template.setDrop(drop);
				templates.put(template.getId(), template);
			}
		}
		
		log.info("loaded  " + templates.size() + " resourses.");
	}
	
	/**
	 * Method getTemplate.
	 * @param templateId int
	 * @return ResourseTemplate
	 */
	public ResourseTemplate getTemplate(int templateId)
	{
		return templates.get(templateId);
	}
	
	public synchronized void reload()
	{
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/resourses"));
		final DropTable dropTable = DropTable.getInstance();
		
		for (File file : files)
		{
			if (!file.getName().endsWith(".xml"))
			{
				log.warning("detected once the file " + file.getPath());
				continue;
			}
			
			final Array<ResourseTemplate> parsed = new DocumentResourse(file).parse();
			
			for (ResourseTemplate template : parsed)
			{
				final ResourseDrop drop = dropTable.getResourseDrop(template.getId());
				template.setDrop(drop);
				final ResourseTemplate old = templates.get(template.getId());
				
				if (old != null)
				{
					Objects.reload(old, template);
				}
				else
				{
					templates.put(template.getId(), template);
				}
			}
		}
		
		log.info("resourses reloaded.");
	}
}
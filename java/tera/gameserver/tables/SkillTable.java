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
import tera.gameserver.document.DocumentSkill;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.templates.SkillTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class SkillTable
{
	private static final Logger log = Loggers.getLogger(SkillTable.class);
	
	private static SkillTable instance;
	
	/**
	 * Method create.
	 * @param templates SkillTemplate[]
	 * @return Skill[]
	 */
	public static Skill[] create(SkillTemplate[] templates)
	{
		final Skill[] skills = new Skill[templates.length];
		
		for (int i = 0, length = templates.length; i < length; i++)
		{
			skills[i] = templates[i].newInstance();
		}
		
		return skills;
	}
	
	/**
	 * Method getInstance.
	 * @return SkillTable
	 */
	public static SkillTable getInstance()
	{
		if (instance == null)
		{
			instance = new SkillTable();
		}
		
		return instance;
	}
	
	/**
	 * Method parseSkills.
	 * @param text String
	 * @return Array<SkillTemplate>
	 */
	public static Array<SkillTemplate> parseSkills(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return Arrays.toArray(SkillTemplate.class, 0);
		}
		
		final String[] strings = text.split(";");
		
		if (strings.length < 1)
		{
			return Arrays.toArray(SkillTemplate.class, 0);
		}
		
		final Array<SkillTemplate> array = Arrays.toArray(SkillTemplate.class, strings.length);
		final SkillTable table = getInstance();
		
		for (String string : strings)
		{
			String[] vals = null;
			boolean negative = false;
			
			if (string.startsWith("-"))
			{
				string = string.replaceFirst("-", ":");
				negative = true;
			}
			
			vals = string.split("-");
			
			if (vals.length < 2)
			{
				continue;
			}
			
			try
			{
				final int classId = Integer.parseInt(negative ? vals[0].replaceFirst(":", "-") : vals[0]);
				final int templateId = Integer.parseInt(vals[1]);
				final SkillTemplate skill = table.getSkill(classId, templateId);
				
				if (skill != null)
				{
					array.add(skill);
				}
			}
			catch (NumberFormatException | ArrayIndexOutOfBoundsException e)
			{
				log.warning(e);
			}
		}
		
		return array;
	}
	
	/**
	 * Method parseSkills.
	 * @param text String
	 * @param classId int
	 * @return Array<SkillTemplate>
	 */
	public static Array<SkillTemplate> parseSkills(String text, int classId)
	{
		if ((text == null) || (text == Strings.EMPTY) || text.isEmpty())
		{
			return Arrays.toArray(SkillTemplate.class, 0);
		}
		
		final String[] strings = text.split(";");
		
		if (strings.length < 1)
		{
			return Arrays.toArray(SkillTemplate.class, 0);
		}
		
		final Array<SkillTemplate> array = Arrays.toArray(SkillTemplate.class, strings.length);
		final SkillTable table = getInstance();
		
		for (String string : strings)
		{
			if (string.isEmpty())
			{
				continue;
			}
			
			try
			{
				final int templateId = Integer.parseInt(string);
				final SkillTemplate skill = table.getSkill(classId, templateId);
				
				if (skill != null)
				{
					array.add(skill);
				}
			}
			catch (NumberFormatException | ArrayIndexOutOfBoundsException e)
			{
				log.warning(e);
			}
		}
		
		array.trimToSize();
		return array;
	}
	
	private final Table<IntKey, Table<IntKey, SkillTemplate>> skills;
	
	private final Table<IntKey, Table<IntKey, SkillTemplate[]>> allskills;
	
	private SkillTable()
	{
		skills = Tables.newIntegerTable();
		allskills = Tables.newIntegerTable();
		int counter = 0;
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/skills"));
		
		for (File file : files)
		{
			if (file == null)
			{
				continue;
			}
			
			if (!file.getName().endsWith(".xml"))
			{
				log.warning("detected once the file " + file.getPath());
				continue;
			}
			
			final Array<SkillTemplate[]> parsed = new DocumentSkill(file).parse();
			
			for (SkillTemplate[] temps : parsed)
			{
				if (temps.length == 0)
				{
					continue;
				}
				
				final SkillTemplate first = temps[0];
				{
					Table<IntKey, SkillTemplate> subTable = skills.get(first.getClassId());
					
					if (subTable == null)
					{
						subTable = Tables.newIntegerTable();
						skills.put(first.getClassId(), subTable);
					}
					
					for (SkillTemplate temp : temps)
					{
						counter++;
						final SkillTemplate old = subTable.put(temp.getId(), temp);
						
						if (old != null)
						{
							log.warning("found duplicate skill " + temp.getId() + ", old " + old.getId() + " in file " + file);
						}
					}
				}
				{
					Table<IntKey, SkillTemplate[]> subTable = allskills.get(first.getClassId());
					
					if (subTable == null)
					{
						subTable = Tables.newIntegerTable();
						allskills.put(first.getClassId(), subTable);
					}
					
					if (subTable.put(first.getId(), temps) != null)
					{
						log.warning("found duplicate skill " + Arrays.toString(temps));
					}
				}
			}
		}
		
		log.info("SkillTable", "loaded " + counter + " skills for " + skills.size() + " classes.");
	}
	
	/**
	 * Method getSkill.
	 * @param classId int
	 * @param templateId int
	 * @return SkillTemplate
	 */
	public SkillTemplate getSkill(int classId, int templateId)
	{
		final Table<IntKey, SkillTemplate> table = skills.get(classId);
		
		if (table == null)
		{
			return null;
		}
		
		return table.get(templateId);
	}
	
	/**
	 * Method getSkills.
	 * @param classId int
	 * @param templateId int
	 * @return SkillTemplate[]
	 */
	public SkillTemplate[] getSkills(int classId, int templateId)
	{
		final Table<IntKey, SkillTemplate[]> table = allskills.get(classId);
		
		if (table == null)
		{
			return null;
		}
		
		return table.get(templateId);
	}
	
	public synchronized void reload()
	{
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/skills"));
		
		for (File file : files)
		{
			if (file == null)
			{
				continue;
			}
			
			if (!file.getName().endsWith(".xml"))
			{
				log.warning("detected once the file " + file.getPath());
				continue;
			}
			
			final Array<SkillTemplate[]> parsed = new DocumentSkill(file).parse();
			
			for (SkillTemplate[] temps : parsed)
			{
				if (temps.length == 0)
				{
					continue;
				}
				
				final SkillTemplate first = temps[0];
				final Table<IntKey, SkillTemplate[]> table = allskills.get(first.getClassId());
				
				if (table == null)
				{
					continue;
				}
				
				final SkillTemplate[] old = table.get(first.getId());
				
				if (old == null)
				{
					table.put(first.getId(), temps);
				}
				else
				{
					for (int i = 0; i < old.length; i++)
					{
						try
						{
							final SkillTemplate oldSkill = old[i];
							final SkillTemplate newSkill = temps[i];
							oldSkill.reload(newSkill);
						}
						catch (Exception e)
						{
							log.warning(e);
						}
					}
				}
			}
		}
		
		log.info("skills reloaded.");
	}
}

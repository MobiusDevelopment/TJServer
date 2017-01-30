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
import tera.gameserver.document.DocumentNpcSpawn;
import tera.gameserver.document.DocumentResourseSpawn;
import tera.gameserver.model.npc.spawn.NpcSpawn;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.model.resourse.ResourseSpawn;
import tera.util.Location;

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
 */
public final class SpawnTable
{
	private static final Logger log = Loggers.getLogger(SpawnTable.class);
	private static SpawnTable instance;
	
	/**
	 * Method getInstance.
	 * @return SpawnTable
	 */
	public static SpawnTable getInstance()
	{
		if (instance == null)
		{
			instance = new SpawnTable();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Table<IntKey, Array<Spawn>>> npcSpawnTable;
	private final Table<IntKey, Array<ResourseSpawn>> resourseSpawnTable;
	
	private SpawnTable()
	{
		npcSpawnTable = Tables.newIntegerTable();
		resourseSpawnTable = Tables.newIntegerTable();
		int counterNpc = 0;
		int counterResourses = 0;
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/spawns"));
		
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
			
			final Array<Spawn> spawnsNpc = new DocumentNpcSpawn(file).parse();
			counterNpc += spawnsNpc.size();
			
			for (Spawn spawn : spawnsNpc)
			{
				Table<IntKey, Array<Spawn>> table = npcSpawnTable.get(spawn.getTemplateId());
				
				if (table == null)
				{
					table = Tables.newIntegerTable();
					npcSpawnTable.put(spawn.getTemplateId(), table);
				}
				
				Array<Spawn> array = table.get(spawn.getTemplateType());
				
				if (array == null)
				{
					array = Arrays.toArray(Spawn.class);
					table.put(spawn.getTemplateType(), array);
				}
				
				array.add(spawn);
			}
			
			final Array<ResourseSpawn> spawnsResourses = new DocumentResourseSpawn(file).parse();
			counterResourses += spawnsResourses.size();
			
			for (ResourseSpawn spawn : spawnsResourses)
			{
				Array<ResourseSpawn> spawns = resourseSpawnTable.get(spawn.getTemplateId());
				
				if (spawns == null)
				{
					spawns = Arrays.toArray(ResourseSpawn.class);
					resourseSpawnTable.put(spawn.getTemplateId(), spawns);
				}
				
				spawns.add(spawn);
			}
		}
		
		for (Table<IntKey, Array<Spawn>> table : npcSpawnTable)
		{
			for (Array<Spawn> spawns : table)
			{
				spawns.trimToSize();
			}
		}
		
		for (Array<ResourseSpawn> spawns : resourseSpawnTable)
		{
			spawns.trimToSize();
		}
		
		startSpawns();
		log.info("loaded " + counterNpc + " spawns for " + npcSpawnTable.size() + " npcs and " + counterResourses + " spawns for " + resourseSpawnTable.size() + " resourses.");
	}
	
	/**
	 * Method getNpcSpawnLoc.
	 * @param templateId int
	 * @param templateType int
	 * @return Location
	 */
	public Location getNpcSpawnLoc(int templateId, int templateType)
	{
		final Table<IntKey, Array<Spawn>> table = npcSpawnTable.get(templateId);
		
		if (table == null)
		{
			return null;
		}
		
		final Array<Spawn> spawns = table.get(templateType);
		
		if ((spawns == null) || spawns.isEmpty())
		{
			return null;
		}
		
		final Spawn spawn = spawns.first();
		
		if (spawn instanceof NpcSpawn)
		{
			final NpcSpawn npcSpawn = (NpcSpawn) spawn;
			return npcSpawn.getLocation();
		}
		
		return null;
	}
	
	public synchronized void reload()
	{
		stopSpawns();
		npcSpawnTable.clear();
		resourseSpawnTable.clear();
		final File[] files = Files.getFiles(new File(Config.SERVER_DIR + "/data/spawns"));
		
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
			
			final Array<Spawn> spawnsNpc = new DocumentNpcSpawn(file).parse();
			
			for (Spawn spawn : spawnsNpc)
			{
				Table<IntKey, Array<Spawn>> table = npcSpawnTable.get(spawn.getTemplateId());
				
				if (table == null)
				{
					table = Tables.newIntegerTable();
					npcSpawnTable.put(spawn.getTemplateId(), table);
				}
				
				Array<Spawn> array = table.get(spawn.getTemplateType());
				
				if (array == null)
				{
					array = Arrays.toArray(Spawn.class);
					table.put(spawn.getTemplateType(), array);
				}
				
				array.add(spawn);
			}
			
			final Array<ResourseSpawn> spawnsResourses = new DocumentResourseSpawn(file).parse();
			
			for (ResourseSpawn spawn : spawnsResourses)
			{
				Array<ResourseSpawn> spawns = resourseSpawnTable.get(spawn.getTemplateId());
				
				if (spawns == null)
				{
					spawns = Arrays.toArray(ResourseSpawn.class);
					resourseSpawnTable.put(spawn.getTemplateId(), spawns);
				}
				
				spawns.add(spawn);
			}
		}
		
		for (Table<IntKey, Array<Spawn>> table : npcSpawnTable)
		{
			for (Array<Spawn> spawns : table)
			{
				spawns.trimToSize();
			}
		}
		
		for (Array<ResourseSpawn> spawns : resourseSpawnTable)
		{
			spawns.trimToSize();
		}
		
		startSpawns();
		log.info("reloaded.");
	}
	
	public void startSpawns()
	{
		for (Table<IntKey, Array<Spawn>> table : npcSpawnTable)
		{
			for (Array<Spawn> spawns : table)
			{
				for (Spawn spawn : spawns)
				{
					spawn.start();
				}
			}
		}
		
		for (Array<ResourseSpawn> spawns : resourseSpawnTable)
		{
			for (ResourseSpawn spawn : spawns)
			{
				spawn.start();
			}
		}
	}
	
	public void stopSpawns()
	{
		for (Table<IntKey, Array<Spawn>> table : npcSpawnTable)
		{
			for (Array<Spawn> spawns : table)
			{
				for (Spawn spawn : spawns)
				{
					spawn.stop();
				}
			}
		}
		
		for (Array<ResourseSpawn> spawns : resourseSpawnTable)
		{
			for (ResourseSpawn spawn : spawns)
			{
				spawn.stop();
			}
		}
	}
}
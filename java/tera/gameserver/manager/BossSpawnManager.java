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
package tera.gameserver.manager;

import tera.gameserver.model.npc.spawn.BossSpawn;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.templates.NpcTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;
import rlib.util.wraps.Wrap;
import rlib.util.wraps.Wraps;

/**
 * @author Ronn
 */
public final class BossSpawnManager
{
	private static final Logger log = Loggers.getLogger(BossSpawnManager.class);
	private static BossSpawnManager instance;
	
	/**
	 * Method getInstance.
	 * @return BossSpawnManager
	 */
	public static BossSpawnManager getInstance()
	{
		if (instance == null)
		{
			instance = new BossSpawnManager();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Table<IntKey, Wrap>> spawnTable;
	private final Array<Spawn> spawns;
	
	private BossSpawnManager()
	{
		spawnTable = Tables.newIntegerTable();
		final DataBaseManager manager = DataBaseManager.getInstance();
		manager.loadBossSpawns(spawnTable);
		spawns = Arrays.toArray(BossSpawn.class);
		int count = 0;
		
		for (Table<IntKey, Wrap> table : spawnTable)
		{
			count += table.size();
		}
		
		log.info("loaded " + count + " boss spawns.");
	}
	
	/**
	 * Method addSpawn.
	 * @param spawn Spawn
	 * @return boolean
	 */
	public synchronized final boolean addSpawn(Spawn spawn)
	{
		if (spawns.contains(spawn))
		{
			return false;
		}
		
		spawns.add(spawn);
		return true;
	}
	
	/**
	 * Method getSpawn.
	 * @param template NpcTemplate
	 * @return long
	 */
	public synchronized final long getSpawn(NpcTemplate template)
	{
		final Table<IntKey, Wrap> table = spawnTable.get(template.getTemplateId());
		
		if (table == null)
		{
			return -1;
		}
		
		final Wrap wrap = table.get(template.getTemplateType());
		return wrap == null ? -1 : wrap.getLong();
	}
	
	/**
	 * Method updateSpawn.
	 * @param template NpcTemplate
	 * @param spawn long
	 */
	public synchronized final void updateSpawn(NpcTemplate template, long spawn)
	{
		Table<IntKey, Wrap> table = spawnTable.get(template.getTemplateId());
		final DataBaseManager manager = DataBaseManager.getInstance();
		
		if (table == null)
		{
			table = Tables.newIntegerTable();
			spawnTable.put(template.getTemplateId(), table);
			table.put(template.getTemplateType(), Wraps.newLongWrap(spawn, true));
			manager.insertBossSpawns(template, spawn);
		}
		else
		{
			final Wrap wrap = table.get(template.getTemplateType());
			
			if (wrap != null)
			{
				wrap.setLong(spawn);
				manager.updateBossSpawns(template, spawn);
			}
			else
			{
				table.put(template.getTemplateType(), Wraps.newLongWrap(spawn, true));
				manager.insertBossSpawns(template, spawn);
			}
		}
	}
}
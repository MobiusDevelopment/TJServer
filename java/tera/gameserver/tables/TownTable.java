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
import tera.gameserver.document.DocumentTown;
import tera.gameserver.model.TownInfo;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class TownTable
{
	private static final Logger log = Loggers.getLogger(TownTable.class);
	private static TownTable instance;
	
	/**
	 * Method getInstance.
	 * @return TownTable
	 */
	public static TownTable getInstance()
	{
		if (instance == null)
		{
			instance = new TownTable();
		}
		
		return instance;
	}
	
	private final Table<IntKey, TownInfo> townIds;
	private final Table<String, TownInfo> townNames;
	
	private TownTable()
	{
		townIds = Tables.newIntegerTable();
		townNames = Tables.newObjectTable();
		final Array<TownInfo> towns = new DocumentTown(new File(Config.SERVER_DIR + "/data/towns.xml")).parse();
		
		for (TownInfo town : towns)
		{
			townIds.put(town.getId(), town);
			townNames.put(town.getName(), town);
		}
		
		log.info("initialized.");
	}
	
	/**
	 * Method getTown.
	 * @param id int
	 * @return TownInfo
	 */
	public TownInfo getTown(int id)
	{
		return townIds.get(id);
	}
	
	/**
	 * Method getTown.
	 * @param name String
	 * @return TownInfo
	 */
	public TownInfo getTown(String name)
	{
		return townNames.get(name);
	}
}
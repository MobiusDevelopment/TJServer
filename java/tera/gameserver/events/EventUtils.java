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
package tera.gameserver.events;

import tera.gameserver.model.EffectList;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.spawn.NpcSpawn;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.model.playable.Player;
import tera.gameserver.tables.ConfigAITable;
import tera.gameserver.tables.NpcTable;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class EventUtils
{
	private static final Array<Location> LOCATION_POOL = Arrays.toConcurrentArray(Location.class);
	
	private static final NpcTemplate GUARD;
	
	private static final ConfigAI GUARD_AI_CONFIG;
	
	static
	{
		final NpcTable npcTable = NpcTable.getInstance();
		GUARD = npcTable.getTemplate(1139, 63);
		final ConfigAITable configTable = ConfigAITable.getInstance();
		GUARD_AI_CONFIG = configTable.getConfig("DefaultFriendly");
	}
	
	public static final int SLEEP_ID = 701100;
	
	public static final Spawn[] guards =
	{
		new NpcSpawn(GUARD, new Location(10607, 8118, 976, 55235), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(10799, 8250, 976, 55235), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11003, 8394, 976, 55235), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11205, 8541, 976, 55235), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11408, 8686, 976, 55235), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11606, 8839, 976, 55235), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11812, 8980, 976, 55235), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12014, 9127, 976, 55235), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		
		new NpcSpawn(GUARD, new Location(12438, 9098, 976, 38447), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12584, 8895, 976, 38447), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12729, 8691, 976, 38447), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12874, 8488, 976, 38447), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(13022, 8286, 976, 38447), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(13170, 8085, 976, 38447), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(13318, 7883, 976, 38447), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(13466, 7682, 976, 38447), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		
		new NpcSpawn(GUARD, new Location(13332, 7335, 976, 23419), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(13131, 7187, 976, 23419), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12929, 7039, 976, 23419), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12726, 6893, 976, 23419), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12524, 6746, 976, 23419), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12323, 6599, 976, 23419), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(12120, 6453, 976, 23419), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11919, 6304, 976, 23419), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		
		new NpcSpawn(GUARD, new Location(11530, 6397, 976, 6768), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11382, 6598, 976, 6768), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11231, 6797, 976, 6768), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(11080, 6997, 976, 6768), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(10931, 7197, 976, 6768), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(10782, 7397, 976, 6768), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(10628, 7594, 976, 6768), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
		new NpcSpawn(GUARD, new Location(10478, 6304, 976, 6768), GUARD_AI_CONFIG, NpcAIClass.DEFAULT),
	};
	
	/**
	 * Method clearEffects.
	 * @param player Player
	 */
	public static void clearEffects(Player player)
	{
		final EffectList list = player.getEffectList();
		
		if ((list == null) || (list.size() < 1))
		{
			return;
		}
		
		list.clear();
	}
	
	/**
	 * Method putLocation.
	 * @param loc Location
	 */
	public static void putLocation(Location loc)
	{
		LOCATION_POOL.add(loc);
	}
	
	/**
	 * Method takeLocation.
	 * @return Location
	 */
	public static Location takeLocation()
	{
		if (LOCATION_POOL.isEmpty())
		{
			return new Location();
		}
		
		LOCATION_POOL.writeLock();
		
		try
		{
			Location loc = LOCATION_POOL.poll();
			
			if (loc == null)
			{
				loc = new Location();
			}
			
			return loc;
		}
		
		finally
		{
			LOCATION_POOL.writeUnlock();
		}
	}
}

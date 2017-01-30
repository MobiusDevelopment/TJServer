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

import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;

import rlib.util.SafeTask;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public final class AutoSaveManager extends SafeTask
{
	private static AutoSaveManager instance;
	
	/**
	 * Method getInstance.
	 * @return AutoSaveManager
	 */
	public static AutoSaveManager getInstance()
	{
		if (instance == null)
		{
			instance = new AutoSaveManager();
		}
		
		return instance;
	}
	
	private AutoSaveManager()
	{
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleGeneralAtFixedRate(this, 900000, 900000);
	}
	
	@Override
	protected void runImpl()
	{
		final Array<Player> players = World.getPlayers();
		players.readLock();
		
		try
		{
			final Player[] array = players.array();
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				array[i].store(false);
			}
		}
		
		finally
		{
			players.readUnlock();
		}
	}
}

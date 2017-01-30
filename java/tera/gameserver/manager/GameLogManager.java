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

import rlib.logging.GameLogger;
import rlib.logging.GameLoggers;
import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public final class GameLogManager
{
	private static final Logger log = Loggers.getLogger(GameLogManager.class);
	private static GameLogManager instance;
	
	/**
	 * Method getInstance.
	 * @return GameLogManager
	 */
	public static GameLogManager getInstance()
	{
		if (instance == null)
		{
			instance = new GameLogManager();
		}
		
		return instance;
	}
	
	private final GameLogger itemLog;
	private final GameLogger questLog;
	private final GameLogger expLog;
	
	private GameLogManager()
	{
		itemLog = GameLoggers.getLogger("Item Log");
		questLog = GameLoggers.getLogger("Quest Log");
		expLog = GameLoggers.getLogger("Exp Log");
		log.info("initialized.");
	}
	
	/**
	 * Method writeExpLog.
	 * @param log String
	 */
	public void writeExpLog(String log)
	{
		expLog.write(log);
	}
	
	/**
	 * Method writeItemLog.
	 * @param log String
	 */
	public void writeItemLog(String log)
	{
		itemLog.write(log);
	}
	
	/**
	 * Method writeQuestLog.
	 * @param log String
	 */
	public void writeQuestLog(String log)
	{
		questLog.write(log);
	}
}
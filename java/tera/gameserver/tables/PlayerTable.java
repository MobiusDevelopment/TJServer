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
import tera.gameserver.document.DocumentPlayer;
import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.base.Race;
import tera.gameserver.model.base.Sex;
import tera.gameserver.templates.PlayerTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public final class PlayerTable
{
	private static final Logger log = Loggers.getLogger(PlayerTable.class);
	
	private static PlayerTable instance;
	
	/**
	 * Method getInstance.
	 * @return PlayerTable
	 */
	public static PlayerTable getInstance()
	{
		if (instance == null)
		{
			instance = new PlayerTable();
		}
		
		return instance;
	}
	
	private final PlayerTemplate[][][] templates;
	
	private PlayerTable()
	{
		templates = new PlayerTemplate[Sex.SIZE][Race.SIZE][PlayerClass.length];
		int counter = 0;
		final Array<PlayerTemplate> parsed = new DocumentPlayer(new File(Config.SERVER_DIR + "/data/player_templates.xml")).parse();
		
		for (PlayerTemplate temp : parsed)
		{
			templates[temp.getSex().ordinal()][temp.getRace().ordinal()][temp.getPlayerClass().ordinal()] = temp;
		}
		
		for (PlayerTemplate[][] matrix : templates)
		{
			for (PlayerTemplate[] array : matrix)
			{
				for (PlayerTemplate template : array)
				{
					if (template != null)
					{
						counter++;
					}
				}
			}
		}
		
		log.info("loaded " + counter + " player templates.");
	}
	
	/**
	 * Method getTemplate.
	 * @param playerClass PlayerClass
	 * @param race Race
	 * @param sex Sex
	 * @return PlayerTemplate
	 */
	public final PlayerTemplate getTemplate(PlayerClass playerClass, Race race, Sex sex)
	{
		return templates[sex.ordinal()][race.ordinal()][playerClass.getId()];
	}
}

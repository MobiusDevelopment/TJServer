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
package tera.gameserver.scripts.commands;

import java.lang.reflect.Field;

import tera.Config;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class ConfigCommand extends AbstractCommand
{
	/**
	 * Constructor for ConfigCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public ConfigCommand(int access, String[] commands)
	{
		super(access, commands);
	}
	
	/**
	 * Method execution.
	 * @param command String
	 * @param player Player
	 * @param values String
	 * @see tera.gameserver.scripts.commands.Command#execution(String, Player, String)
	 */
	@Override
	public void execution(String command, Player player, String values)
	{
		switch (command)
		{
			case "config_reload":
				Config.reload();
				break;
			
			case "config_set":
			{
				final String[] vals = values.split(" ");
				
				if (vals.length < 3)
				{
					player.sendMessage("Missing arguments.");
					return;
				}
				
				try
				{
					final Field field = Config.class.getField(vals[0]);
					Object val = null;
					
					switch (vals[1])
					{
						case "int":
							val = Integer.valueOf(vals[2]);
							break;
						
						case "boolean":
							val = Boolean.valueOf(vals[2]);
							break;
						
						case "string":
							val = String.valueOf(vals[2]);
							break;
						
						case "float":
							val = Float.valueOf(vals[2]);
							break;
					}
					
					field.set(null, val);
					player.sendMessage("The new value: " + field.get(null));
				}
				catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					player.sendMessage(e.getClass().getSimpleName());
				}
			}
		}
	}
}
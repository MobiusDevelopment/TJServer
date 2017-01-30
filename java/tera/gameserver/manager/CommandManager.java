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

import tera.gameserver.model.playable.Player;
import tera.gameserver.scripts.commands.Command;
import tera.gameserver.scripts.commands.CommandType;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class CommandManager
{
	private static final Logger log = Loggers.getLogger(CommandManager.class);
	private static CommandManager instance;
	
	/**
	 * Method getInstance.
	 * @return CommandManager
	 */
	public static CommandManager getInstance()
	{
		if (instance == null)
		{
			instance = new CommandManager();
		}
		
		return instance;
	}
	
	private final Table<String, Command> commands;
	
	private CommandManager()
	{
		commands = Tables.newObjectTable();
		
		for (CommandType cmds : CommandType.values())
		{
			registerCommands(cmds.newInstance());
		}
		
		log.info("loaded " + commands.size() + " commands.");
	}
	
	/**
	 * Method execute.
	 * @param player Player
	 * @param cmd String
	 * @param values String
	 * @return boolean
	 */
	public final boolean execute(Player player, String cmd, String values)
	{
		if (cmd == null)
		{
			return false;
		}
		
		final Command command = commands.get(cmd);
		
		if (command == null)
		{
			return false;
		}
		
		if (player.getAccessLevel() < command.getAccess())
		{
			return false;
		}
		
		try
		{
			command.execution(cmd, player, values);
		}
		catch (Exception e)
		{
			log.warning(e);
		}
		
		return true;
	}
	
	/**
	 * Method registerCommands.
	 * @param command Command
	 */
	public final void registerCommands(Command command)
	{
		final String[] cmmds = command.getCommands();
		
		for (String cmd : cmmds)
		{
			if (commands.containsKey(cmd))
			{
				log.warning("found a duplicate command " + cmd + ".");
			}
			else
			{
				commands.put(cmd, command);
			}
		}
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	public final int size()
	{
		return commands.size();
	}
}
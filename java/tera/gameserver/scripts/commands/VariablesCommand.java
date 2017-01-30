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

import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class VariablesCommand extends AbstractCommand
{
	/**
	 * Constructor for VariablesCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public VariablesCommand(int access, String[] commands)
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
			case "set_player_var":
			{
				final String[] vals = values.split(" ");
				
				switch (vals[0])
				{
					case "int":
						player.setVar(vals[1], Integer.parseInt(vals[2]));
						break;
				}
				
				break;
			}
			
			case "get_player_var":
			{
				final String[] vals = values.split(" ");
				
				switch (vals[0])
				{
					case "int":
						player.sendMessage("var: " + String.valueOf(player.getVar(vals[1], -1)));
						break;
					
					default:
						player.sendMessage("var: " + String.valueOf(player.getVar(values)));
				}
				
				break;
			}
		}
	}
}
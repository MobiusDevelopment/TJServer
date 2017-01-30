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

import java.text.SimpleDateFormat;
import java.util.Date;

import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class CensoreCommand extends AbstractCommand
{
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private static final Date date = new Date();
	
	/**
	 * Constructor for CensoreCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public CensoreCommand(int access, String[] commands)
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
			case "chat_ban":
			{
				final String[] vals = values.split(" ", 3);
				
				if (vals.length < 3)
				{
					player.sendMessage("Incorrect values.");
					return;
				}
				
				final String name = vals[0];
				final String comment = vals[2];
				
				if (comment.isEmpty())
				{
					player.sendMessage("You did not enter a comment.");
					return;
				}
				
				final long time = Integer.parseInt(vals[1]) * 60 * 1000;
				Player target = null;
				
				if (player.getName().equals(name))
				{
					target = player;
				}
				else if ((target = World.getAroundByName(Player.class, player, name)) == null)
				{
					target = World.getPlayer(name);
				}
				
				if (target == null)
				{
					player.sendMessage("Player is not in game.");
					return;
				}
				
				final long endTime = System.currentTimeMillis() + time;
				target.setEndChatBan(endTime);
				String stringDate = null;
				synchronized (date)
				{
					date.setTime(endTime);
					stringDate = timeFormat.format(date);
				}
				World.sendAnnounce(player.getName() + " blocked chat for player " + name + " until " + stringDate + " cause: " + comment);
				break;
			}
			
			case "chat_unban":
			{
				Player target = null;
				
				if (player.getName().equals(values))
				{
					target = player;
				}
				else if ((target = World.getAroundByName(Player.class, player, values)) == null)
				{
					target = World.getPlayer(values);
				}
				
				if (target == null)
				{
					player.sendMessage("Player is not in game.");
					return;
				}
				
				if (target.getEndChatBan() < 1)
				{
					player.sendMessage("The player is not chat banned.");
					return;
				}
				
				target.setEndChatBan(0);
				World.sendAnnounce(player.getName() + " unbanned chat of player " + values);
			}
		}
	}
}

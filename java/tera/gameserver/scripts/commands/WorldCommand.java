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

import tera.gameserver.model.TownInfo;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.territory.Territory;
import tera.gameserver.tables.TownTable;
import tera.util.Location;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class WorldCommand extends AbstractCommand
{
	/**
	 * Constructor for WorldCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public WorldCommand(int access, String[] commands)
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
			case "loc":
				player.sendMessage("Char: " + player.getName() + " Location X: " + player.getX() + "  Y: " + player.getY() + "  Z: " + player.getZ() + " heading: " + player.getHeading());
				break;
			
			case "region":
				player.sendMessage("Region: " + player.getCurrentRegion() + ", id = " + player.getCurrentRegion().hashCode());
				break;
			
			case "territory":
			{
				final Array<Territory> terrs = player.getTerritories();
				
				if (terrs.isEmpty())
				{
					player.sendMessage("You are not on any territory.");
					break;
				}
				
				final StringBuilder text = new StringBuilder("Territory:");
				
				for (Territory terr : terrs)
				{
					if (terr != null)
					{
						text.append(terr).append(", ");
					}
				}
				
				if (!player.getTerritories().isEmpty())
				{
					text.replace(text.length() - 2, text.length(), ".");
				}
				
				player.sendMessage(text.toString());
				break;
			}
			
			case "goto":
			{
				final String[] args = values.split(" ");
				
				if ((args.length > 1) && "-p".equals(args[1]))
				{
					final Player target = World.getPlayer(args[0]);
					
					if (target != null)
					{
						player.teleToLocation(target.getLoc());
					}
					
					break;
				}
				
				if (args.length == 4)
				{
					final float x = Float.parseFloat(args[0]);
					final float y = Float.parseFloat(args[1]);
					final float z = Float.parseFloat(args[2]);
					final int continent = Integer.parseInt(args[3]);
					player.teleToLocation(continent, x, y, z);
				}
				else
				{
					final TownTable townTable = TownTable.getInstance();
					final TownInfo town = townTable.getTown(values);
					player.setZoneId(town.getZone());
					player.teleToLocation(town.getCenter());
				}
				
				break;
			}
			
			case "recall":
			{
				final Player target = World.getPlayer(values);
				
				if (target == null)
				{
					return;
				}
				
				final Location loc = player.getLoc();
				loc.setContinentId(player.getContinentId());
				target.teleToLocation(loc);
				break;
			}
		}
	}
}

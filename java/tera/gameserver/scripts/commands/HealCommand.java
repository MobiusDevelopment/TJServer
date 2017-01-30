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

import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.PlayerCurrentHp;
import tera.gameserver.network.serverpackets.PlayerCurrentMp;

import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public class HealCommand extends AbstractCommand
{
	/**
	 * Constructor for HealCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public HealCommand(int access, String[] commands)
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
		try
		{
			switch (command)
			{
				case "set_hp":
				{
					Player target = null;
					final String[] vals = values.split(" ");
					
					if (vals.length == 1)
					{
						target = player;
					}
					else
					{
						target = World.getAroundByName(Player.class, player, vals[1]);
					}
					
					if (target == null)
					{
						return;
					}
					
					target.setCurrentHp(Integer.parseInt(vals[0]));
					target.sendPacket(PlayerCurrentHp.getInstance(target, null, 0, PlayerCurrentHp.INCREASE), true);
					break;
				}
				
				case "set_mp":
				{
					Player target = null;
					final String[] vals = values.split(" ");
					
					if (vals.length == 1)
					{
						target = player;
					}
					else
					{
						target = World.getAroundByName(Player.class, player, vals[1]);
					}
					
					if (target == null)
					{
						return;
					}
					
					target.setCurrentMp(Integer.parseInt(vals[0]));
					target.sendPacket(PlayerCurrentMp.getInstance(target, null, 0, PlayerCurrentMp.INCREASE), true);
					break;
				}
				
				case "heal":
				{
					Player target = null;
					
					if ((values == null) || values.isEmpty())
					{
						target = player;
					}
					else
					{
						target = World.getAroundByName(Player.class, player, values);
					}
					
					if (target == null)
					{
						return;
					}
					
					target.setCurrentHp(target.getMaxHp());
					target.setCurrentMp(target.getMaxMp());
					target.sendPacket(PlayerCurrentHp.getInstance(target, null, 0, PlayerCurrentHp.INCREASE), true);
					target.sendPacket(PlayerCurrentMp.getInstance(player, null, 0, PlayerCurrentMp.INCREASE), true);
				}
			}
		}
		catch (NumberFormatException e)
		{
			Loggers.warning(getClass(), "parsing error of " + values);
		}
	}
}

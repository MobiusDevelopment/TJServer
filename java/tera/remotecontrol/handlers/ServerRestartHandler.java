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
package tera.remotecontrol.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import tera.Config;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;

import rlib.logging.Loggers;

public class ServerRestartHandler implements PacketHandler
{
	/**
	 * Method processing.
	 * @param packet Packet
	 * @return Packet
	 * @see tera.remotecontrol.PacketHandler#processing(Packet)
	 */
	@Override
	public Packet processing(Packet packet)
	{
		Loggers.info("ServerRestartHandler", "start save all players...");
		
		for (Player player : World.getPlayers())
		{
			Loggers.info(this, "store " + player.getName());
			player.store(false);
		}
		
		Loggers.info("ServerRestartHandler", "done.");
		
		if (!Config.SERVER_ONLINE_FILE.isEmpty())
		{
			try (PrintWriter out = new PrintWriter(new File(Config.SERVER_ONLINE_FILE)))
			{
				out.print(0);
			}
			catch (FileNotFoundException e)
			{
				Loggers.warning(this, e);
			}
		}
		
		Loggers.info(this, "start restart...");
		System.exit(2);
		return null;
	}
}

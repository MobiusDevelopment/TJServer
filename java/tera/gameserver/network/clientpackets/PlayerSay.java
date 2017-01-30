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
package tera.gameserver.network.clientpackets;

import tera.Config;
import tera.gameserver.manager.CommandManager;
import tera.gameserver.model.SayType;
import tera.gameserver.model.playable.Player;
import tera.remotecontrol.handlers.LoadChatHandler;

import rlib.logging.GameLogger;
import rlib.logging.GameLoggers;

/**
 * @author Ronn
 */
public class PlayerSay extends ClientPacket
{
	private static final GameLogger log = GameLoggers.getLogger("Chat");
	private Player player;
	private String text;
	private SayType type;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
		text = null;
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.ReadeablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	public void readImpl()
	{
		readShort();
		type = SayType.valueOf(readInt());
		text = readString();
		player = owner.getOwner();
	}
	
	@Override
	public void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final String message = player.getName() + ": " + text;
		
		if (Config.DIST_CONTROL_ENABLED)
		{
			LoadChatHandler.add(message);
		}
		
		log.write(message);
		
		if (text.indexOf("--") == 6)
		{
			text = text.substring(8, text.length() - 7);
			final String[] commands = text.split(" ", 2);
			final String command = commands[0];
			String values = null;
			
			if (commands.length > 1)
			{
				values = commands[1];
			}
			
			final CommandManager commandManager = CommandManager.getInstance();
			
			if (!commandManager.execute(player, command, values))
			{
				player.getAI().startSay(text, type);
			}
			
			return;
		}
		
		player.getAI().startSay(text, type);
	}
}
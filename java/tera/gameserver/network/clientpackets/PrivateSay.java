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

import tera.gameserver.model.MessageType;
import tera.gameserver.model.SayType;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.CharSay;

/**
 * @author Ronn
 */
public class PrivateSay extends ClientPacket
{
	
	private Player player;
	
	private String name;
	
	private String text;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
		text = null;
		player = null;
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
	protected void readImpl()
	{
		player = owner.getOwner();
		readShort();
		readByte();
		readByte();
		name = readString();
		text = readString();
	}
	
	@Override
	protected void runImpl()
	{
		if ((player == null) || player.getName().equals(name))
		{
			return;
		}
		
		Player enemy = World.getAroundByName(Player.class, player, name);
		
		if (enemy == null)
		{
			enemy = World.getPlayer(name);
		}
		
		if ((enemy == null) || !enemy.isConnected())
		{
			player.sendMessage(MessageType.THAT_CHARACTER_ISNT_ONLINE);
			return;
		}
		
		player.sendPacket(CharSay.getInstance(player.getName(), text, SayType.PRIVATE_CHAT, player.getObjectId(), player.getSubId()), true);
		enemy.sendPacket(CharSay.getInstance(player.getName(), text, SayType.WHISHPER_CHAT, player.getObjectId(), player.getSubId()), true);
	}
}

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

import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class UpdateHotKey extends ClientPacket
{
	private Player player;
	private byte[] hotkey;
	
	@Override
	public void finalyze()
	{
		player = null;
		hotkey = null;
	}
	
	@Override
	public void readImpl()
	{
		player = owner.getOwner();
		
		int size = buffer.limit() - buffer.position();
		
		if (buffer.capacity() < 4096)
		{
			log.warning(this, "this is small read buffer " + buffer.capacity() + ".");
			return;
		}
		
		if (player != null)
		{
			byte[] old = player.getHotkey();
			
			if ((old != null) && (old.length == size))
			{
				hotkey = old;
			}
			else
			{
				hotkey = new byte[size];
			}
			
			buffer.get(hotkey);
		}
	}
	
	@Override
	public void runImpl()
	{
		if ((player != null) && (hotkey != null))
		{
			player.setHotkey(hotkey, true);
		}
	}
}
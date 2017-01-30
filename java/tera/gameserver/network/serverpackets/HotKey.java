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
package tera.gameserver.network.serverpackets;

import java.nio.ByteBuffer;

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class HotKey extends ServerPacket
{
	private static final ServerPacket instance = new HotKey();
	
	public static HotKey getInstance(Player player)
	{
		HotKey packet = (HotKey) instance.newInstance();
		
		packet.hotkey = player.getHotkey();
		
		return packet;
	}
	
	private byte[] hotkey;
	
	@Override
	public void finalyze()
	{
		hotkey = null;
	}
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.HOT_KEY;
	}
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		if (hotkey != null)
		{
			writeOpcode(buffer);
			
			buffer.put(hotkey);
		}
	}
}

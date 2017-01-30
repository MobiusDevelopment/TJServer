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

import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class HotKeyChanger extends ServerPacket
{
	/**
	 */
	public static enum ChangeType
	{
		NONE,
		REPLACE,
	}
	
	private static final ServerPacket instance = new HotKeyChanger();
	
	/**
	 * Method getInstance.
	 * @param type ChangeType
	 * @param vals int[]
	 * @return HotKeyChanger
	 */
	public static final HotKeyChanger getInstance(ChangeType type, int... vals)
	{
		final HotKeyChanger packet = (HotKeyChanger) instance.newInstance();
		packet.type = type;
		packet.vals = vals;
		return packet;
	}
	
	private ChangeType type;
	private int[] vals;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.HOT_KEY_CHANGED;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeShort(type.ordinal());
		
		for (int val : vals)
		{
			writeInt(val);
		}
	}
}
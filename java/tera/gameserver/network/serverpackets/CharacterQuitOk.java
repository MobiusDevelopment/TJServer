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
public class CharacterQuitOk extends ServerPacket
{
	private static final ServerPacket instance = new CharacterQuitOk();
	
	/**
	 * Method getInstance.
	 * @param state int
	 * @return CharacterQuitOk
	 */
	public static CharacterQuitOk getInstance(int state)
	{
		final CharacterQuitOk packet = (CharacterQuitOk) instance.newInstance();
		packet.state = state;
		return packet;
	}
	
	private int state;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_EXIT_OK;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		
		if (state == 1)
		{
			writeInt(0x0A000000);
		}
	}
}
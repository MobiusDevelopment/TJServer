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

import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 * @created 31.03.2012
 */
public class PlayerSelected extends ServerConstPacket
{
	private static final PlayerSelected instance = new PlayerSelected();
	
	public static PlayerSelected getInstance()
	{
		return instance;
	}
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_SELECTED;
	}
	
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeByte(buffer, 1);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
	}
}
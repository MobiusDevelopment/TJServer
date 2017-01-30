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

public class Structure extends ServerConstPacket
{
	private static final Structure instance = new Structure();
	
	public static Structure getInstance()
	{
		return instance;
	}
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.STRUCTURE;
	}
	
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		
		writeShort(buffer, 6);
		
		writeShort(buffer, 25);
		writeInt(buffer, 0);
		writeByte(buffer, 0);
		writeInt(buffer, 0);
		
		writeInt(buffer, 410);
		writeInt(buffer, 13);
		
		writeShort(buffer, 25);
		writeShort(buffer, 41);
		writeInt(buffer, 1);
		writeInt(buffer, 1);
		writeInt(buffer, 1);
		
		writeShort(buffer, 41);
		writeShort(buffer, 57);
		writeInt(buffer, 2);
		writeInt(buffer, 2);
		writeInt(buffer, 1);
		
		writeShort(buffer, 57);
		writeShort(buffer, 73);
		writeInt(buffer, 3);
		writeInt(buffer, 3);
		writeInt(buffer, 1);
		
		writeShort(buffer, 73);
		writeShort(buffer, 89);
		writeInt(buffer, 4);
		writeInt(buffer, 4);
		writeInt(buffer, 1);
		
		writeShort(buffer, 89);
		writeShort(buffer, 105);
		writeInt(buffer, 5);
		writeInt(buffer, 5);
		writeInt(buffer, 1);
		
		writeShort(buffer, 105);
		writeShort(buffer, 0);
		writeInt(buffer, 6);
		writeInt(buffer, 6);
		writeInt(buffer, 1);
	}
}
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

public class FFStructure extends ServerConstPacket
{
	private static final FFStructure instance = new FFStructure();
	
	public static FFStructure getInstance()
	{
		return instance;
	}
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.FF_STRUCTURE;
	}
	
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		
		writeShort(buffer, 5);
		writeShort(buffer, 22);
		writeInt(buffer, 1);
		writeInt(buffer, 0);
		
		writeInt(buffer, 60);
		writeShort(buffer, 0);
		
		writeShort(buffer, 22);
		writeShort(buffer, 34);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		
		writeShort(buffer, 34);
		writeShort(buffer, 46); // 2E00
		writeInt(buffer, -1);
		writeInt(buffer, 0);
		
		writeShort(buffer, 46);
		writeShort(buffer, 58);
		writeInt(buffer, -1);
		writeInt(buffer, 0);
		
		writeShort(buffer, 58); // 3A00
		writeShort(buffer, 70);
		writeInt(buffer, -1);
		writeInt(buffer, 0);
		
		writeShort(buffer, 70); // 46000000
		writeShort(buffer, 0); // 46000000
		writeInt(buffer, -1);
		writeInt(buffer, 0);
	}
}
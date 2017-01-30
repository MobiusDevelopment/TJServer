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
 */
public class CheckServerResult extends ServerPacket
{
	private static final ServerPacket instance = new CheckServerResult();
	
	/**
	 * Method getInstance.
	 * @param vals int[]
	 * @return CheckServerResult
	 */
	public static CheckServerResult getInstance(int[] vals)
	{
		final CheckServerResult packet = (CheckServerResult) instance.newInstance();
		packet.vals[0] = vals[0];
		packet.vals[1] = vals[1];
		packet.vals[2] = vals[2];
		return packet;
	}
	
	private final int[] vals;
	
	public CheckServerResult()
	{
		vals = new int[3];
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.CHECK_SERVER_RESULT;
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.SendablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	/**
	 * Method writeImpl.
	 * @param buffer ByteBuffer
	 */
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeByte(buffer, 0);
		writeInt(buffer, vals[0]);
		writeInt(buffer, vals[1]);
		writeInt(buffer, vals[2]);
	}
}
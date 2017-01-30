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
public class ResultCheckName extends ServerPacket
{
	private static final ServerPacket instance = new ResultCheckName();
	
	/**
	 * Method getInstance.
	 * @param name String
	 * @param type int
	 * @return ResultCheckName
	 */
	public static ResultCheckName getInstance(String name, int type)
	{
		final ResultCheckName packet = (ResultCheckName) instance.newInstance();
		packet.name = name;
		packet.type = type;
		return packet;
	}
	
	private String name;
	
	private int type;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.RESULT_CHECK_NAME;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeShort(1);
		writeShort(8);
		writeInt(8);
		writeShort(22);
		writeInt(type);
		writeInt(0);
		writeString(name);
		writeByte(0);
	}
}

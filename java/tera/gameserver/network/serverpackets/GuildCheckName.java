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
public class GuildCheckName extends ServerPacket
{
	private static final ServerPacket instance = new GuildCheckName();
	
	/**
	 * Method getInstance.
	 * @param name String
	 * @return GuildCheckName
	 */
	public static GuildCheckName getInstance(String name)
	{
		final GuildCheckName packet = (GuildCheckName) instance.newInstance();
		packet.name = name;
		return packet;
	}
	
	private String name;
	
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
		return ServerPacketType.GUILD_CHECK_NAME;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeShort(1);
		writeShort(8);
		writeInt(8);
		writeShort(22);
		writeShort(2);
		writeInt(0);
		writeByte(0);
		writeS(name);
		writeByte(0);
	}
}

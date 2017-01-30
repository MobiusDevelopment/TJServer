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
import java.nio.ByteOrder;

import tera.gameserver.model.GuildIcon;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;

/**
 * @author Ronn
 */
public class GuildIconInfo extends ServerPacket
{
	private static final ServerPacket instance = new GuildIconInfo();
	
	/**
	 * Method getInstance.
	 * @param icon GuildIcon
	 * @return GuildIconInfo
	 */
	public static GuildIconInfo getInstance(GuildIcon icon)
	{
		final GuildIconInfo packet = (GuildIconInfo) instance.newInstance();
		
		if ((icon == null) || !icon.hasIcon())
		{
			return packet;
		}
		
		final ByteBuffer buffer = packet.getPrepare();
		final byte[] bytes = icon.getIcon();
		packet.writeShort(buffer, 10);
		packet.writeShort(buffer, Strings.length(icon.getName()) + 10);
		packet.writeShort(buffer, bytes.length);
		packet.writeString(buffer, icon.getName());
		buffer.put(bytes);
		buffer.flip();
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public GuildIconInfo()
	{
		prepare = ByteBuffer.allocate(1024000).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		prepare.clear();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.GUILD_ICON_INFO;
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
		final ByteBuffer prepare = getPrepare();
		buffer.put(prepare.array(), 0, prepare.limit());
	}
	
	/**
	 * Method getPrepare.
	 * @return ByteBuffer
	 */
	public ByteBuffer getPrepare()
	{
		return prepare;
	}
}

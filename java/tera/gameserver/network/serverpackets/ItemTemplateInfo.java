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
public class ItemTemplateInfo extends ServerPacket
{
	private static final ServerPacket instance = new ItemTemplateInfo();
	
	/**
	 * Method getInstance.
	 * @param id int
	 * @return ItemTemplateInfo
	 */
	public static ItemTemplateInfo getInstance(int id)
	{
		final ItemTemplateInfo packet = (ItemTemplateInfo) instance.newInstance();
		packet.id = id;
		return packet;
	}
	
	private int id;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.ITEM_TEMPLATE_INFO;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeInt(0x00000000);
		writeInt(0x0121011F);
		writeInt(0x00000013);
		writeInt(0);
		writeInt(0);
		writeInt(id);
		writeInt(0xE05C9F08);
		writeInt(0);
		writeInt(0);
		writeInt(0x00000000);
		writeInt(0);
		writeInt(0x00000000);
		writeInt(1);
		writeInt(1);
		writeInt(0x00000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeInt(0x00000000);
		writeInt(3);
		writeInt(0x00000000);
		writeInt(0x00000000);
		writeShort(0x0000);
		writeByte(0x00);
	}
}

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

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;
import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 */
public class ShopReplyPacket extends ServerPacket
{
	private static final ServerPacket instance = new ShopReplyPacket();
	
	/**
	 * Method getInstance.
	 * @param sections ItemTemplate[][]
	 * @param player Player
	 * @param sectionId int
	 * @return ShopReplyPacket
	 */
	public static ShopReplyPacket getInstance(ItemTemplate[][] sections, Player player, int sectionId)
	{
		final ShopReplyPacket packet = (ShopReplyPacket) instance.newInstance();
		packet.buffer = packet.prepare;
		packet.writeInt(0x00200002);
		packet.writeInt(player.getObjectId());
		packet.writeInt(player.getSubId());
		packet.writeInt(0x0009AFC2);
		packet.writeInt(0x000000D3);
		packet.writeLong(0x3FA99999999999AL);
		int orderSection = 32;
		int beginItem = 44;
		
		for (int i = 0, sectionLength = sections.length - 1; i < sections.length; i++)
		{
			packet.writeShort(orderSection);
			final ItemTemplate[] items = sections[i];
			
			if (items.length < 1)
			{
				break;
			}
			
			beginItem = orderSection + 12;
			
			if (i == sectionLength)
			{
				orderSection = 0;
			}
			else
			{
				orderSection = orderSection + 12 + (items.length * 8);
			}
			
			packet.writeShort(orderSection);
			packet.writeShort(items.length);
			packet.writeShort(beginItem);
			packet.writeInt(sectionId++);
			
			for (int g = 0, length = items.length - 1; g <= length; g++)
			{
				packet.writeShort(beginItem);
				beginItem += 8;
				
				if (g == length)
				{
					packet.writeShort(0);
				}
				else
				{
					packet.writeShort(beginItem);
				}
				
				packet.writeInt(items[g].getItemId());
			}
		}
		
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public ShopReplyPacket()
	{
		super();
		prepare = ByteBuffer.allocate(16384).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.NPC_DIALOG_REPLY_SHOP;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		prepare.flip();
		buffer.put(prepare);
	}
}
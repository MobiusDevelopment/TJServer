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
public class MultiShop extends ServerPacket
{
	private static final ServerPacket instance = new MultiShop();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @param items ItemTemplate[]
	 * @param price int[]
	 * @param priceId int
	 * @return MultiShop
	 */
	public static MultiShop getInstance(Player player, ItemTemplate[] items, int[] price, int priceId)
	{
		final MultiShop packet = (MultiShop) instance.newInstance();
		final ByteBuffer buffer = packet.prepare;
		packet.writeShort(buffer, 2);
		int bytes = 32;
		packet.writeShort(buffer, 32);
		packet.writeInt(buffer, player.getObjectId());
		packet.writeInt(buffer, player.getSubId());
		packet.writeInt(buffer, 923335);
		packet.writeInt(buffer, 154);
		packet.writeInt(buffer, 0);
		packet.writeInt(buffer, 25);
		packet.writeShort(buffer, bytes);
		packet.writeInt(buffer, 8390188);
		bytes += 12;
		packet.writeShort(buffer, bytes);
		packet.writeInt(buffer, 1541);
		final int last = items.length - 1;
		
		for (int i = 0, length = items.length; i < length; i++)
		{
			packet.writeShort(buffer, bytes);
			
			if (i == last)
			{
				bytes = 0;
			}
			else
			{
				bytes += 12;
			}
			
			packet.writeShort(buffer, bytes);
			packet.writeInt(buffer, items[i].getItemId());
			packet.writeInt(buffer, price[i]);
		}
		
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public MultiShop()
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
		return ServerPacketType.MULTI_SHOP;
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
		prepare.flip();
		buffer.put(prepare);
	}
}
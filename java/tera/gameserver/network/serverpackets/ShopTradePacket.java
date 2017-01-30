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

import tera.gameserver.model.BuyableItem;
import tera.gameserver.model.SellableItem;
import tera.gameserver.model.npc.interaction.dialogs.ShopDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class ShopTradePacket extends ServerPacket
{
	private static final ServerPacket instance = new ShopTradePacket();
	
	/**
	 * Method getInstance.
	 * @param dialog ShopDialog
	 * @return ShopTradePacket
	 */
	public static ShopTradePacket getInstance(ShopDialog dialog)
	{
		final ShopTradePacket packet = (ShopTradePacket) instance.newInstance();
		packet.buffer = packet.prepare;
		synchronized (dialog)
		{
			final Player player = dialog.getPlayer();
			
			if (player == null)
			{
				return packet;
			}
			
			final Array<BuyableItem> buyItems = dialog.getBuyItems();
			final Array<SellableItem> sellItems = dialog.getSellItems();
			
			if (buyItems.isEmpty())
			{
				packet.writeInt(0);
			}
			else
			{
				packet.writeShort(3);
				packet.writeShort(56);
			}
			
			if (sellItems.isEmpty())
			{
				packet.writeInt(0);
			}
			else
			{
				packet.writeShort(2);
				packet.writeShort(56 + (buyItems.size() * 12));
			}
			
			packet.writeInt(player.getObjectId());
			packet.writeInt(player.getSubId());
			packet.writeInt(0x0009C7ED);
			packet.writeLong(0);
			packet.writeLong(dialog.getBuyPrice());
			packet.writeLong(0x3FA99999999999AL);
			packet.writeLong(dialog.getSellPrice());
			int beginByte = 56;
			int save = beginByte;
			
			for (int i = 0, length = buyItems.size() - 1; i <= length; i++)
			{
				packet.writeShort(beginByte);
				
				if (i == length)
				{
					save = beginByte + 12;
					beginByte = 0;
				}
				else
				{
					beginByte += 12;
				}
				
				final BuyableItem item = buyItems.get(i);
				packet.writeShort(beginByte);
				packet.writeInt(item.getItemId());
				packet.writeInt((int) item.getCount());
			}
			
			beginByte = save;
			
			for (int i = 0, length = sellItems.size() - 1; i <= length; i++)
			{
				packet.writeShort(beginByte);
				
				if (i == length)
				{
					beginByte = 0;
				}
				else
				{
					beginByte += 22;
				}
				
				final SellableItem item = sellItems.get(i);
				packet.writeShort(beginByte);
				packet.writeInt(item.getItemId());
				packet.writeInt((int) item.getCount());
				packet.writeInt(item.getObjectId());
				packet.writeInt(0);
				packet.writeShort(0);
			}
		}
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public ShopTradePacket()
	{
		super();
		prepare = ByteBuffer.allocate(4048).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.ITEM_SHOP_TRADE;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		prepare.flip();
		buffer.put(prepare);
	}
}
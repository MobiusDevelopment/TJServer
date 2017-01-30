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

import tera.gameserver.model.TradeItem;
import tera.gameserver.model.actions.dialogs.TradeDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class ShowTrade extends ServerPacket
{
	private static final ServerPacket instance = new ShowTrade();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @param enemy Player
	 * @param objectId int
	 * @param trade TradeDialog
	 * @return ShowTrade
	 */
	public static ShowTrade getInstance(Player player, Player enemy, int objectId, TradeDialog trade)
	{
		final ShowTrade packet = (ShowTrade) instance.newInstance();
		final Array<TradeItem> playerItems = trade.getItems(player);
		final Array<TradeItem> enemyItems = trade.getItems(enemy);
		final int playerBytes = 25 * playerItems.size();
		final int enemyBytes = 25 * enemyItems.size();
		packet.buffer = packet.prepare;
		packet.writeShort(56);
		packet.writeShort(playerBytes);
		packet.writeShort(56 + playerBytes);
		packet.writeShort(enemyBytes);
		packet.writeInt(player.getObjectId());
		packet.writeInt(player.getSubId());
		packet.writeInt(enemy.getObjectId());
		packet.writeInt(enemy.getSubId());
		packet.writeInt(objectId);
		packet.writeInt(trade.isLock(player) ? 1 : 0);
		packet.writeLong(trade.getMoney(player));
		packet.writeInt(trade.isLock(enemy) ? 1 : 0);
		packet.writeLong(trade.getMoney(enemy));
		TradeItem[] array = playerItems.array();
		
		for (int i = 0, length = playerItems.size(); i < length; i++)
		{
			final TradeItem item = array[i];
			packet.writeInt(i);
			packet.writeInt(item.getItemId());
			packet.writeInt((int) item.getCount());
			packet.writeInt(item.getObjectId());
			packet.writeLong(0);
			packet.writeByte(0);
		}
		
		array = enemyItems.array();
		
		for (int i = 0, length = enemyItems.size(); i < length; i++)
		{
			final TradeItem item = array[i];
			packet.writeInt(i);
			packet.writeInt(item.getItemId());
			packet.writeInt((int) item.getCount());
			packet.writeInt(item.getObjectId());
			packet.writeLong(0);
			packet.writeByte(0);
		}
		
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public ShowTrade()
	{
		super();
		prepare = ByteBuffer.allocate(2048).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.OPEN_TRADE;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		prepare.flip();
		buffer.put(prepare);
	}
}

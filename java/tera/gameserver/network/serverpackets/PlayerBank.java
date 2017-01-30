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

import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class PlayerBank extends ServerPacket
{
	private static final ServerPacket instance = new PlayerBank();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @param startCell int
	 * @return PlayerBank
	 */
	public static PlayerBank getInstance(Player player, int startCell)
	{
		final PlayerBank packet = (PlayerBank) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			final Bank bank = player.getBank();
			
			if (bank == null)
			{
				return packet;
			}
			
			startCell = Math.min(startCell, bank.getMaxSize());
			final int endCell = Math.min(startCell + bank.getTabSize(), bank.getMaxSize());
			int bytes = 44;
			final int last = bank.getMaxSize() - 1;
			packet.writeShort(buffer, bank.getUsedCount());
			packet.writeShort(buffer, bytes);
			packet.writeInt(buffer, player.getObjectId());
			packet.writeInt(buffer, player.getSubId());
			packet.writeLong(buffer, 1);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 48);
			packet.writeLong(buffer, bank.getMoney());
			bank.lock();
			
			try
			{
				final Cell[] cells = bank.getCells();
				
				for (int i = startCell; i < endCell; i++)
				{
					final Cell cell = cells[i];
					
					if (cell.isEmpty())
					{
						continue;
					}
					
					final ItemInstance item = cell.getItem();
					packet.writeShort(buffer, bytes);
					
					if (i == last)
					{
						bytes = 0;
					}
					else
					{
						bytes += 62;
					}
					
					packet.writeShort(buffer, bytes);
					packet.writeInt(buffer, 0);
					packet.writeInt(buffer, item.getItemId());
					packet.writeInt(buffer, item.getObjectId());
					packet.writeInt(buffer, item.getSubId());
					packet.writeInt(buffer, player.getObjectId());
					packet.writeInt(buffer, 0);
					packet.writeInt(buffer, i - startCell);
					packet.writeInt(buffer, 1);
					packet.writeInt(buffer, 1);
					packet.writeInt(buffer, (int) item.getItemCount());
					packet.writeInt(buffer, 0);
					packet.writeInt(buffer, 0);
					packet.writeLong(buffer, 0);
					packet.writeShort(buffer, 0);
				}
			}
			
			finally
			{
				bank.unlock();
			}
			return packet;
		}
		
		finally
		{
			buffer.flip();
		}
	}
	
	private final ByteBuffer prepare;
	
	public PlayerBank()
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
		return ServerPacketType.PLAYER_BANK;
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
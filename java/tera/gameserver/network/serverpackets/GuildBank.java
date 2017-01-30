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

import tera.gameserver.model.Guild;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class GuildBank extends ServerPacket
{
	private static final ServerPacket instance = new GuildBank();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @param startCell int
	 * @return GuildBank
	 */
	public static GuildBank getInstance(Player player, int startCell)
	{
		final GuildBank packet = (GuildBank) instance.newInstance();
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			log.warning(GuildBank.class, new Exception("not found guild"));
			return null;
		}
		
		final Bank bank = guild.getBank();
		startCell = Math.min(startCell, bank.getMaxSize());
		final int endCell = Math.min(startCell + bank.getTabSize(), bank.getMaxSize());
		final ByteBuffer buffer = packet.prepare;
		int bytes = 44;
		final int last = endCell - 1;
		packet.writeShort(buffer, bank.getUsedCount());
		packet.writeShort(buffer, bytes);
		packet.writeInt(buffer, player.getObjectId());
		packet.writeInt(buffer, player.getSubId());
		packet.writeInt(buffer, 1);
		packet.writeInt(buffer, 0);
		packet.writeInt(buffer, 0);
		packet.writeInt(buffer, 0);
		packet.writeInt(buffer, 48);
		packet.writeLong(buffer, bank.getMoney());
		bank.lock();
		
		try
		{
			final Cell[] cells = bank.getCells();
			final int ownerId = player.getObjectId();
			
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
				packet.writeInt(buffer, ownerId);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, i - startCell);
				packet.writeInt(buffer, 1);
				packet.writeInt(buffer, 1);
				packet.writeInt(buffer, (int) item.getItemCount());
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 1);
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
	
	private final ByteBuffer prepare;
	
	public GuildBank()
	{
		super();
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
		return ServerPacketType.GUILD_BANK;
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
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

import tera.gameserver.model.items.CrystalInstance;
import tera.gameserver.model.items.CrystalList;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;

/**
 * @author Ronn
 */
public class InventoryItemInfo extends ServerPacket
{
	private static final ServerPacket instance = new InventoryItemInfo();
	
	/**
	 * Method getInstance.
	 * @param index int
	 * @param item ItemInstance
	 * @return InventoryItemInfo
	 */
	public static InventoryItemInfo getInstance(int index, ItemInstance item)
	{
		final InventoryItemInfo packet = (InventoryItemInfo) instance.newInstance();
		packet.index = index;
		packet.item = item;
		return packet;
	}
	
	private ItemInstance item;
	private int index;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		item = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_INVENTORY_ITEM_INFO;
	}
	
	@Override
	protected void writeImpl()
	{
		final Player player = owner.getOwner();
		final boolean hasCrystals = item.hasCrystals();
		writeOpcode();
		
		if (player == null)
		{
			return;
		}
		
		int bytes = item.isBinded() ? Strings.length(item.getOwnerName()) + 301 : 299;
		
		if (!hasCrystals)
		{
			writeInt(0);
		}
		else
		{
			writeShort(4);
			writeShort(bytes);
		}
		
		if (!item.isBinded())
		{
			writeInt(0);
		}
		else
		{
			writeShort(299);
			writeShort(299 + 2);
		}
		
		writeInt(19);
		writeInt(item.getObjectId());
		writeInt(0);
		writeInt(item.getItemId());
		writeInt(item.getObjectId());
		writeInt(0);
		writeInt(player.getObjectId());
		writeInt(0);
		writeInt(index + 20);
		writeInt(0);
		writeInt(1);
		writeInt(1);
		writeInt(item.getEnchantLevel());
		writeInt(0);
		writeInt(item.isBinded() ? 1 : 0);
		writeInt(10534660);
		writeInt(0);
		writeInt(0);
		writeInt(0);
		writeByte(0);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeLong(0x0000000000000000);
		writeInt(0);
		writeShort(0);
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
		writeLong(0);
		writeLong(0x0000000000000000);
		writeLong(item.getItemLevel());
		writeLong(0xFFFFFFFFFFFFFFFFL);
		writeInt(0xFFFFFFFE);
		
		if (item.isBinded())
		{
			writeShort(0);
			writeString(item.getOwnerName());
		}
		
		if (hasCrystals)
		{
			final CrystalList crystals = item.getCrystals();
			final CrystalInstance[] array = crystals.getArray();
			
			for (int i = 0, length = crystals.size(); i < length; i++)
			{
				final CrystalInstance crystal = array[i];
				writeShort(bytes);
				
				if (i == (length - 1))
				{
					bytes = 0;
				}
				else
				{
					bytes += 8;
				}
				
				writeShort(bytes);
				writeInt(crystal.getItemId());
			}
		}
	}
}
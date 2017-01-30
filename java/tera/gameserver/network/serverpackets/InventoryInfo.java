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

import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.Slot;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.BindType;
import tera.gameserver.model.items.CrystalInstance;
import tera.gameserver.model.items.CrystalList;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class InventoryInfo extends ServerPacket
{
	private static final ServerPacket instance = new InventoryInfo();
	
	public static InventoryInfo getInstance(Player player)
	{
		InventoryInfo packet = (InventoryInfo) instance.newInstance();
		
		ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			Inventory inventory = player.getInventory();
			
			Equipment equipment = player.getEquipment();
			
			if ((inventory == null) || (equipment == null))
			{
				return packet;
			}
			
			inventory.lock();
			try
			{
				equipment.lock();
				try
				{
					int total = inventory.getEngagedCells() + equipment.getEngagedSlots();
					
					packet.writeShort(buffer, total);
					packet.writeShort(buffer, 39);
					packet.writeInt(buffer, player.getObjectId());
					packet.writeInt(buffer, player.getSubId());
					packet.writeLong(buffer, inventory.getMoney());
					packet.writeByte(buffer, 1); // 0101
					packet.writeByte(buffer, 1);
					
					int equipmentSize = equipment.size();
					
					int inventorySize = inventory.getMaxCells();
					
					packet.writeByte(buffer, 0);
					packet.writeInt(buffer, inventorySize); // equipmentSize
					packet.writeInt(buffer, 0x7E);
					packet.writeInt(buffer, 0x7E);
					
					Cell[] cells = inventory.getCells();
					
					Slot[] slots = equipment.getSlots();
					
					int n = 0x27;
					
					for (int i = 0; i < equipmentSize; i++)
					{
						ItemInstance item = slots[i].getItem();
						
						if (item == null)
						{
							continue;
						}
						
						packet.writeShort(buffer, n);
						
						n += 154;
						
						packet.writeShort(buffer, n);
						
						packet.writeInt(buffer, item.getItemId());
						packet.writeInt(buffer, item.getObjectId());
						packet.writeInt(buffer, 0);
						packet.writeInt(buffer, player.getObjectId());
						packet.writeInt(buffer, 0);
						packet.writeInt(buffer, i + 1);
						packet.writeInt(buffer, 0);
						packet.writeLong(buffer, item.getItemCount());
						packet.writeInt(buffer, 0);
						packet.writeByte(buffer, 1); // !!!
						
						CrystalList crystals = item.getCrystals();
						
						if ((crystals == null) || crystals.isEmpty())
						{
							packet.writeInt(buffer, 0); // 1
							packet.writeInt(buffer, 0); // 2
							packet.writeInt(buffer, 0); // 3
							packet.writeInt(buffer, 0); // 4
						}
						else
						{
							int diff = 4 - crystals.size();
							
							CrystalInstance[] array = crystals.getArray();
							
							for (int g = 0, length = crystals.size(); g < length; g++)
							{
								packet.writeInt(buffer, array[g].getItemId());
							}
							
							if (diff > 0)
							{
								for (int g = 0; g < diff; g++)
								{
									packet.writeInt(buffer, 0);
								}
							}
						}
						
						packet.writeInt(buffer, 0);
						packet.writeByte(buffer, 0);
						
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, item.getItemLevel());
					}
					
					int last = inventory.getLastIndex();
					
					for (int i = 0; i < inventorySize; i++)
					{
						ItemInstance item = cells[i].getItem();
						
						if (item == null)
						{
							continue;
						}
						
						packet.writeShort(buffer, n);
						
						n += 154;
						
						if (last == i)
						{
							n = 0;
						}
						
						packet.writeShort(buffer, n);
						
						packet.writeInt(buffer, item.getItemId());
						packet.writeInt(buffer, item.getObjectId());
						packet.writeInt(buffer, 0);// 0;
						packet.writeInt(buffer, player.getObjectId());
						packet.writeInt(buffer, player.getSubId());
						packet.writeInt(buffer, i + 20);
						packet.writeInt(buffer, 0);
						packet.writeLong(buffer, item.getItemCount());
						
						packet.writeInt(buffer, 0);
						packet.writeByte(buffer, (item.getBoundType() == BindType.NONE) || item.isBinded() ? 1 : 0);// можно одевать или нет?
						
						CrystalList crystals = item.getCrystals();
						
						if ((crystals == null) || crystals.isEmpty())
						{
							packet.writeInt(buffer, 0); // 1
							packet.writeInt(buffer, 0); // 2
							packet.writeInt(buffer, 0); // 3
							packet.writeInt(buffer, 0); // 4
						}
						else
						{
							int diff = 4 - crystals.size();
							
							CrystalInstance[] array = crystals.getArray();
							
							for (int g = 0, length = crystals.size(); g < length; g++)
							{
								packet.writeInt(buffer, array[g].getItemId());
							}
							
							if (diff > 0)
							{
								for (int g = 0; g < diff; g++)
								{
									packet.writeInt(buffer, 0);
								}
							}
						}
						
						packet.writeInt(buffer, 0);
						packet.writeByte(buffer, 0);
						
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, 0);
						packet.writeLong(buffer, item.getItemLevel());
					}
				}
				finally
				{
					equipment.unlock();
				}
			}
			finally
			{
				inventory.unlock();
			}
			
			return packet;
		}
		finally
		{
			buffer.flip();
		}
	}
	
	private final ByteBuffer prepare;
	
	public InventoryInfo()
	{
		this.prepare = ByteBuffer.allocate(1024000).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	@Override
	public void finalyze()
	{
		prepare.clear();
	}
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_INVENTORY;
	}
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		
		ByteBuffer prepare = getPrepare();
		
		buffer.put(prepare.array(), 0, prepare.limit());
	}
	
	public ByteBuffer getPrepare()
	{
		return prepare;
	}
}
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

import tera.gameserver.model.TObject;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class ItemInfo extends ServerPacket
{
	private static final ServerPacket instance = new ItemInfo();
	
	/**
	 * Method getInstance.
	 * @param item ItemInstance
	 * @return ItemInfo
	 */
	public static ItemInfo getInstance(ItemInstance item)
	{
		final ItemInfo packet = (ItemInfo) instance.newInstance();
		packet.objectId = item.getObjectId();
		packet.subId = item.getSubId();
		packet.x = item.getX();
		packet.y = item.getY();
		packet.z = item.getZ();
		packet.itemCount = (int) item.getItemCount();
		packet.itemId = item.getItemId();
		final TObject dropper = item.getDropper();
		
		if (dropper != null)
		{
			packet.dropperId = dropper.getObjectId();
			packet.dropperSubId = dropper.getSubId();
		}
		
		final TObject owner = item.getTempOwner();
		
		if (owner != null)
		{
			packet.ownerId = owner.getObjectId();
			packet.ownerSubId = owner.getSubId();
		}
		
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int itemId;
	private int itemCount;
	private int dropperId;
	private int dropperSubId;
	private int ownerId;
	private int ownerSubId;
	private float x;
	private float y;
	private float z;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		dropperId = 0;
		dropperSubId = 0;
		ownerId = 0;
		ownerSubId = 0;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.ITEM_INFO;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeShort(1);
		writeShort(49);
		writeInt(objectId);
		writeInt(subId);
		writeFloat(x);
		writeFloat(y);
		writeFloat(z);
		writeInt(itemId);
		writeInt(itemCount);
		writeInt(119984);
		writeByte(1);
		writeInt(dropperId);
		writeInt(dropperSubId);
		writeInt(49); // 31 00 00 00
		writeInt(ownerId);
		writeInt(ownerSubId);
	}
}
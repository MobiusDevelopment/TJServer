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

import tera.gameserver.model.Character;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.SlotType;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class PlayerEquipment extends ServerPacket
{
	private static final ServerPacket instance = new PlayerEquipment();
	
	/**
	 * Method getInstance.
	 * @param owner Character
	 * @return PlayerEquipment
	 */
	public static PlayerEquipment getInstance(Character owner)
	{
		final PlayerEquipment packet = (PlayerEquipment) instance.newInstance();
		packet.objectId = owner.getObjectId();
		packet.subId = owner.getSubId();
		final Equipment equipment = owner.getEquipment();
		equipment.lock();
		
		try
		{
			ItemInstance item = equipment.getItem(SlotType.SLOT_WEAPON);
			packet.weaponId = item == null ? 0 : item.getItemId();
			packet.enchantLevel = item == null ? 0 : item.getEnchantLevel();
			item = equipment.getItem(SlotType.SLOT_ARMOR);
			packet.armorId = item == null ? 0 : item.getItemId();
			item = equipment.getItem(SlotType.SLOT_BOOTS);
			packet.bootsId = item == null ? 0 : item.getItemId();
			item = equipment.getItem(SlotType.SLOT_GLOVES);
			packet.glovesId = item == null ? 0 : item.getItemId();
			item = equipment.getItem(SlotType.SLOT_HAT);
			packet.hatId = item == null ? 0 : item.getItemId();
			item = equipment.getItem(SlotType.SLOT_MASK);
			packet.maskId = item == null ? 0 : item.getItemId();
		}
		
		finally
		{
			equipment.unlock();
		}
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int weaponId;
	private int armorId;
	private int bootsId;
	private int glovesId;
	private int hatId;
	private int maskId;
	private int enchantLevel;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_EQUIPMENT;
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
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeInt(buffer, weaponId);
		writeInt(buffer, armorId);
		writeInt(buffer, bootsId);
		writeInt(buffer, glovesId);
		writeInt(buffer, hatId);
		writeInt(buffer, maskId);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		writeInt(buffer, enchantLevel);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
		writeInt(buffer, 0);
	}
}
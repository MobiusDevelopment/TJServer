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

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.SlotType;
import tera.gameserver.model.playable.PlayerAppearance;
import tera.gameserver.model.playable.PlayerPreview;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class PlayerList extends ServerPacket
{
	private static final ServerPacket instance = new PlayerList();
	
	/**
	 * Method getInstance.
	 * @param accountName String
	 * @return PlayerList
	 */
	public static PlayerList getInstance(String accountName)
	{
		final PlayerList packet = (PlayerList) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final Array<PlayerPreview> playerList = packet.getPlayerList();
		dbManager.restorePlayerList(playerList, accountName);
		final int size = playerList.size();
		int sizeTo = 0;
		packet.writeShort(buffer, size);
		
		if (size > 0)
		{
			packet.writeShort(buffer, 35);
			packet.writeLong(buffer, 1);
			packet.writeByte(buffer, 0);
			packet.writeInt(buffer, 8);
			packet.writeInt(buffer, 1);
			packet.writeShort(buffer, 0);
			packet.writeInt(buffer, 5);
			packet.writeInt(buffer, 168);
			
			final PlayerPreview[] array = playerList.array();
			final PlayerPreview last = playerList.last();
			
			for (int i = 0; i < size; i++)
			{
				sizeTo = buffer.position() + 4;
				packet.writeShort(buffer, sizeTo);
				final PlayerPreview current = array[i];
				
				if (current == last)
				{
					packet.writeShort(buffer, 0);
				}
				else
				{
					packet.writeShort(buffer, sizeTo + 283 + Strings.length(current.getName())); //
				}
				
				packet.writeShort(buffer, sizeTo += 251); // amount of bytes before char name starts...
				packet.writeShort(buffer, sizeTo + Strings.length(current.getName()));
				packet.writeShort(buffer, 32);
				
				packet.writeInt(buffer, current.getObjectId());
				packet.writeInt(buffer, current.getSex());
				packet.writeInt(buffer, current.getRaceId());
				packet.writeInt(buffer, current.getClassId());
				packet.writeInt(buffer, current.getLevel());
				packet.writeInt(buffer, 0x000186A0);
				packet.writeInt(buffer, 0x000186A0);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0x00007E8F);
				packet.writeInt(buffer, 0);
				packet.writeByte(buffer, 0);
				packet.writeInt(buffer, 0xB060614E);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0xB060614E);
				final Equipment equipment = current.getEquipment();
				packet.writeInt(buffer, equipment.getItemId(SlotType.SLOT_WEAPON));
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, equipment.getItemId(SlotType.SLOT_ARMOR));
				packet.writeInt(buffer, equipment.getItemId(SlotType.SLOT_GLOVES));
				packet.writeInt(buffer, equipment.getItemId(SlotType.SLOT_BOOTS));
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeInt(buffer, equipment.getItemId(SlotType.SLOT_HAT));
				packet.writeInt(buffer, equipment.getItemId(SlotType.SLOT_MASK));
				
				final PlayerAppearance appearance = current.getAppearance();
				
				packet.writeByte(buffer, 65);
				packet.writeByte(buffer, appearance.getFaceColor());
				packet.writeByte(buffer, appearance.getFaceSkin());
				packet.writeByte(buffer, appearance.getAdormentsSkin());
				packet.writeByte(buffer, appearance.getFeaturesSkin());
				packet.writeByte(buffer, appearance.getFeaturesColor());
				packet.writeByte(buffer, appearance.getVoice());
				packet.writeByte(buffer, 0);
				packet.writeLong(buffer, 0);
				packet.writeInt(buffer, 0);
				packet.writeShort(buffer, 0);
				packet.writeInt(buffer, 0xB05718BF);
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
				packet.writeLong(buffer, 0);
				packet.writeLong(buffer, 0);
				packet.writeInt(buffer, 0x000001A3);
				packet.writeInt(buffer, 0x000001A3);
				packet.writeByte(buffer, 1);
				
				if (current.getOnlineTime() > 15000)
				{
					packet.writeByte(buffer, 0);
				}
				else
				{
					packet.writeByte(buffer, 1);
				}
				
				packet.writeByte(buffer, 0);
				packet.writeShort(buffer, 0);
				packet.writeByte(buffer, 0);
				
				packet.writeString(buffer, current.getName());
				packet.writeByte(buffer, appearance.getBoneStructureBrow());
				packet.writeByte(buffer, appearance.getBoneStructureCheekbones());
				packet.writeByte(buffer, appearance.getBoneStructureJaw());
				packet.writeByte(buffer, appearance.getBoneStructureJawJut());
				packet.writeByte(buffer, appearance.getEarsRotation());
				packet.writeByte(buffer, appearance.getEarsExtension());
				packet.writeByte(buffer, appearance.getEarsTrim());
				packet.writeByte(buffer, appearance.getEarsSize());
				packet.writeByte(buffer, appearance.getEyesWidth());
				packet.writeByte(buffer, appearance.getEyesHeight());
				packet.writeByte(buffer, appearance.getEyesSeparation());
				packet.writeByte(buffer, 0);
				packet.writeByte(buffer, appearance.getEyesAngle());
				packet.writeByte(buffer, appearance.getEyesInnerBrow());
				packet.writeByte(buffer, appearance.getEyesOuterBrow());
				packet.writeByte(buffer, 0);
				packet.writeByte(buffer, appearance.getNoseExtension());
				packet.writeByte(buffer, appearance.getNoseSize());
				packet.writeByte(buffer, appearance.getNoseBridge());
				packet.writeByte(buffer, appearance.getNoseNostrilWidth());
				packet.writeByte(buffer, appearance.getNoseTipWidth());
				packet.writeByte(buffer, appearance.getNoseTip());
				packet.writeByte(buffer, appearance.getNoseNostrilFlare());
				packet.writeByte(buffer, appearance.getMouthPucker());
				packet.writeByte(buffer, appearance.getMouthPosition());
				packet.writeByte(buffer, appearance.getMouthWidth());
				packet.writeByte(buffer, appearance.getMouthLipThickness());
				packet.writeByte(buffer, appearance.getMouthCorners());
				packet.writeByte(buffer, appearance.getEyesShape());
				packet.writeByte(buffer, appearance.getNoseBend());
				packet.writeByte(buffer, appearance.getBoneStructureJawWidth());
				packet.writeByte(buffer, appearance.getMothGape());
			}
			
			for (int i = 0; i < size; i++)
			{
				array[i].fold();
			}
		}
		else
		{
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 1);
			packet.writeShort(buffer, 0);
		}
		
		return packet;
	}
	
	private final Array<PlayerPreview> playerList;
	
	private final ByteBuffer prepare;
	
	public PlayerList()
	{
		playerList = Arrays.toArray(PlayerPreview.class);
		prepare = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		playerList.clear();
		prepare.clear();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_LIST;
	}
	
	/**
	 * Method getPlayerList.
	 * @return Array<PlayerPreview>
	 */
	public Array<PlayerPreview> getPlayerList()
	{
		return playerList;
	}
	
	/**
	 * Method getPrepare.
	 * @return ByteBuffer
	 */
	public ByteBuffer getPrepare()
	{
		return prepare;
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
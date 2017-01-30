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

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.playable.NpcAppearance;
import tera.gameserver.network.ServerPacketType;

import rlib.util.Strings;

/**
 * @author Ronn
 */
public class NpcPlayableInfo extends ServerPacket
{
	private static final ServerPacket instance = new NpcPlayableInfo();
	
	/**
	 * Method getInstance.
	 * @param npc Npc
	 * @return NpcPlayableInfo
	 */
	public static NpcPlayableInfo getInstance(Npc npc)
	{
		final NpcPlayableInfo packet = (NpcPlayableInfo) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		int n = 194;
		final String name = npc.getName();
		final String guildName = npc.getFraction();
		final String title = Strings.EMPTY;
		final String guildTitle = npc.getTitle();
		final String iconName = Strings.EMPTY;
		final NpcAppearance appearance = npc.getAppearance();
		
		if (appearance == null)
		{
			log.warning(packet, "not found npc appearance.");
			return packet;
		}
		
		packet.writeShort(buffer, n);
		packet.writeShort(buffer, n += Strings.length(name));
		packet.writeShort(buffer, n += Strings.length(guildName));
		packet.writeShort(buffer, n += Strings.length(title));
		packet.writeShort(buffer, 32);
		packet.writeShort(buffer, n += 32);
		packet.writeShort(buffer, n += Strings.length(guildTitle));
		packet.writeInt(buffer, 0);
		packet.writeInt(buffer, npc.getObjectId());
		packet.writeInt(buffer, npc.getObjectId());
		packet.writeInt(buffer, npc.getSubId());
		packet.writeFloat(buffer, npc.getX());
		packet.writeFloat(buffer, npc.getY());
		packet.writeFloat(buffer, npc.getZ());
		packet.writeShort(buffer, npc.getHeading());
		packet.writeInt(buffer, npc.getNameColor());
		packet.writeInt(buffer, npc.getModelId());
		packet.writeShort(buffer, 0);
		packet.writeShort(buffer, 0x46);
		packet.writeShort(buffer, 0xAA);
		packet.writeShort(buffer, 0);
		packet.writeShort(buffer, 0);
		packet.writeByte(buffer, 1);
		packet.writeByte(buffer, npc.isDead() ? 0 : 1);
		packet.writeByte(buffer, 65);
		packet.writeByte(buffer, appearance.getFaceColor());
		packet.writeByte(buffer, appearance.getFaceSkin());
		packet.writeByte(buffer, appearance.getAdormentsSkin());
		packet.writeByte(buffer, appearance.getFeaturesSkin());
		packet.writeByte(buffer, appearance.getFeaturesColor());
		packet.writeByte(buffer, appearance.getVoice());
		packet.writeByte(buffer, 0);
		packet.writeInt(buffer, appearance.getWeaponId());
		packet.writeInt(buffer, appearance.getArmorId());
		packet.writeInt(buffer, appearance.getBootsId());
		packet.writeInt(buffer, appearance.getGlovesId());
		packet.writeInt(buffer, appearance.getMaskId());
		packet.writeInt(buffer, appearance.getHatId());
		packet.writeInt(buffer, npc.isSpawned() ? 1 : 0);
		packet.writeInt(buffer, 0);
		packet.writeLong(buffer, 0);
		packet.writeLong(buffer, 0);
		packet.writeLong(buffer, 0);
		packet.writeLong(buffer, 0);
		packet.writeLong(buffer, 0);
		packet.writeLong(buffer, 0);
		packet.writeLong(buffer, 0);
		packet.writeByte(buffer, 0);
		packet.writeInt(buffer, npc.getLevel());
		packet.writeLong(buffer, 0);
		packet.writeInt(buffer, 1);
		packet.writeLong(buffer, 0);
		packet.writeInt(buffer, 0);
		packet.writeByte(buffer, 0);
		packet.writeString(buffer, name);
		packet.writeString(buffer, guildName);
		packet.writeString(buffer, title);
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
		packet.writeString(buffer, guildTitle);
		packet.writeString(buffer, iconName);
		buffer.flip();
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public NpcPlayableInfo()
	{
		prepare = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.NPC_PLAYABLE_INFO;
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
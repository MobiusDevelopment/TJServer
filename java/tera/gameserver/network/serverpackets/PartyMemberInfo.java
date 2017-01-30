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

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class PartyMemberInfo extends ServerPacket
{
	private static final ServerPacket instance = new PartyMemberInfo();
	
	/**
	 * Method getInstance.
	 * @param member Player
	 * @return PartyMemberInfo
	 */
	public static PartyMemberInfo getInstance(Player member)
	{
		final PartyMemberInfo packet = (PartyMemberInfo) instance.newInstance();
		packet.objectId = member.getObjectId();
		packet.currentHp = member.getCurrentHp();
		packet.currentMp = member.getCurrentMp();
		packet.maxHp = member.getMaxHp();
		packet.maxMp = member.getMaxMp();
		packet.level = member.getLevel();
		packet.stamina = member.getStamina();
		packet.dead = member.isDead() ? 0 : 1;
		return packet;
	}
	
	private int objectId;
	
	private int currentHp;
	
	private int currentMp;
	
	private int maxHp;
	
	private int maxMp;
	
	private int level;
	
	private int stamina;
	
	private int dead;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PARTY_MEMBER_INFO;
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
		writeInt(buffer, 0);
		writeInt(buffer, objectId);
		writeInt(buffer, currentHp);
		writeInt(buffer, currentMp);
		writeInt(buffer, maxHp);
		writeInt(buffer, maxMp);
		writeInt(buffer, level);
		writeShort(buffer, 2);
		writeByte(buffer, dead);
		writeInt(buffer, stamina);
		writeLong(buffer, 0);
		writeInt(buffer, 0);
	}
}

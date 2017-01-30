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

import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class SkillLeash extends ServerPacket
{
	private static final ServerPacket instance = new SkillLeash();
	
	/**
	 * Method getInstance.
	 * @param casterId int
	 * @param casterSubId int
	 * @param targetId int
	 * @param targetSubId int
	 * @param resut boolean
	 * @return SkillLeash
	 */
	public static SkillLeash getInstance(int casterId, int casterSubId, int targetId, int targetSubId, boolean resut)
	{
		final SkillLeash packet = (SkillLeash) instance.newInstance();
		packet.casterId = casterId;
		packet.casterSubId = casterSubId;
		packet.targetId = targetId;
		packet.targetSubId = targetSubId;
		packet.resut = resut;
		return packet;
	}
	
	private int casterId;
	private int casterSubId;
	private int targetId;
	private int targetSubId;
	private boolean resut;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_LEASH;
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
		writeInt(buffer, casterId);
		writeInt(buffer, casterSubId);
		writeInt(buffer, targetId);
		writeInt(buffer, targetSubId);
		writeShort(buffer, resut ? 1 : 0);
	}
}
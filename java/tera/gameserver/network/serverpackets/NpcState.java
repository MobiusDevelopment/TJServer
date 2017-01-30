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
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class NpcState extends ServerPacket
{
	private static final ServerPacket instance = new NpcState();
	
	/**
	 * Method getInstance.
	 * @param character Character
	 * @param target Character
	 * @param state int
	 * @return NpcState
	 */
	public static NpcState getInstance(Character character, Character target, int state)
	{
		final NpcState packet = (NpcState) instance.newInstance();
		packet.objectId = character.getObjectId();
		packet.subId = character.getSubId();
		packet.targetId = target.getObjectId();
		packet.targetSubId = target.getSubId();
		packet.state = state;
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int targetId;
	private int targetSubId;
	private int state;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.NPC_STATE;
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
		writeShort(buffer, 0xC2F6);
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeInt(buffer, targetId);
		writeInt(buffer, targetSubId);
		writeShort(buffer, state);
	}
}
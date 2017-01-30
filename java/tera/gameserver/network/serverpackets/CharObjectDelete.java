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

import tera.gameserver.model.traps.Trap;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class CharObjectDelete extends ServerPacket
{
	private static final ServerPacket instance = new CharObjectDelete();
	
	/**
	 * Method getInstance.
	 * @param objectId int
	 * @param subId int
	 * @return CharObjectDelete
	 */
	public static CharObjectDelete getInstance(int objectId, int subId)
	{
		final CharObjectDelete packet = (CharObjectDelete) instance.newInstance();
		packet.objectId = objectId;
		packet.subId = subId;
		return packet;
	}
	
	/**
	 * Method getInstance.
	 * @param trap Trap
	 * @return CharObjectDelete
	 */
	public static CharObjectDelete getInstance(Trap trap)
	{
		final CharObjectDelete packet = (CharObjectDelete) instance.newInstance();
		packet.objectId = trap.getObjectId();
		packet.subId = trap.getSubId();
		return packet;
	}
	
	private int objectId;
	
	private int subId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.TRAP_DELETE;
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
		writeByte(buffer, 1);
	}
}

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

import tera.gameserver.model.Character;
import tera.gameserver.model.traps.Trap;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class TrapInfo extends ServerPacket
{
	private static final ServerPacket instance = new TrapInfo();
	
	/**
	 * Method getInstance.
	 * @param trap Trap
	 * @return TrapInfo
	 */
	public static TrapInfo getInstance(Trap trap)
	{
		final TrapInfo packet = (TrapInfo) instance.newInstance();
		final ByteBuffer buffer = packet.prepare;
		final Character owner = trap.getOwner();
		packet.writeInt(buffer, trap.getObjectId());
		packet.writeInt(buffer, trap.getSubId());
		packet.writeInt(buffer, owner.getTemplateType());
		packet.writeInt(buffer, trap.getTemplateId());
		packet.writeFloat(buffer, trap.getX());
		packet.writeFloat(buffer, trap.getY());
		packet.writeFloat(buffer, trap.getZ());
		packet.writeFloat(buffer, trap.getX());
		packet.writeFloat(buffer, trap.getY());
		packet.writeFloat(buffer, trap.getZ());
		packet.writeInt(buffer, 0);
		packet.writeByte(buffer, 0);
		packet.writeInt(buffer, owner.getObjectId());
		packet.writeInt(buffer, owner.getSubId());
		packet.writeInt(buffer, owner.getModelId());
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public TrapInfo()
	{
		super();
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
		return ServerPacketType.TRAP_INFO;
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
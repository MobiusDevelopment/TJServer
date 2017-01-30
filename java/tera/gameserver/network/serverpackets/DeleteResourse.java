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

import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class DeleteResourse extends ServerPacket
{
	public static final int DEAD = 5;
	public static final int DISAPPEARS_DUST = 4;
	public static final int DISAPPEARS = 1;
	private static final ServerPacket instance = new DeleteResourse();
	
	/**
	 * Method getInstance.
	 * @param resourse ResourseInstance
	 * @param type int
	 * @return DeleteResourse
	 */
	public static DeleteResourse getInstance(ResourseInstance resourse, int type)
	{
		final DeleteResourse packet = (DeleteResourse) instance.newInstance();
		packet.type = type;
		packet.objectId = resourse.getObjectId();
		packet.subId = resourse.getSubId();
		return packet;
	}
	
	private int type;
	private int objectId;
	private int subId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.RESOURSE_REMOVE;
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
	protected final void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeByte(buffer, type);
	}
}
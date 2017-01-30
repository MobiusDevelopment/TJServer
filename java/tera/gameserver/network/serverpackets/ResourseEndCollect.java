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
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class ResourseEndCollect extends ServerPacket
{
	private static final ServerPacket instance = new ResourseEndCollect();
	public static final int SUCCESSFUL = 3;
	public static final int FAILED = 2;
	public static final int INTERRUPTED = 0;
	
	/**
	 * Method getInstance.
	 * @param collector Character
	 * @param resourse ResourseInstance
	 * @param result int
	 * @return ResourseEndCollect
	 */
	public static ResourseEndCollect getInstance(Character collector, ResourseInstance resourse, int result)
	{
		final ResourseEndCollect packet = (ResourseEndCollect) instance.newInstance();
		packet.collectorId = collector.getObjectId();
		packet.collectorSubId = collector.getSubId();
		packet.resourseId = resourse.getObjectId();
		packet.resourseSubId = resourse.getSubId();
		packet.result = result;
		return packet;
	}
	
	private int collectorId;
	private int collectorSubId;
	private int resourseId;
	private int resourseSubId;
	private int result;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.RESOURSE_END_COLLECT;
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
		writeInt(buffer, collectorId);
		writeInt(buffer, collectorSubId);
		writeInt(buffer, resourseId);
		writeInt(buffer, resourseSubId);
		writeInt(buffer, result);
	}
}
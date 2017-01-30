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
import tera.util.Location;

/**
 * @author Ronn
 */
public class ResourseInfo extends ServerPacket
{
	private static final ServerPacket instance = new ResourseInfo();
	
	/**
	 * Method getInstance.
	 * @param resourse ResourseInstance
	 * @return ResourseInfo
	 */
	public static ResourseInfo getInstance(ResourseInstance resourse)
	{
		final ResourseInfo packet = (ResourseInfo) instance.newInstance();
		packet.objectId = resourse.getObjectId();
		packet.subId = resourse.getSubId();
		packet.templateId = resourse.getTemplateId();
		resourse.getLoc(packet.loc);
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int templateId;
	private final Location loc;
	
	public ResourseInfo()
	{
		super();
		loc = new Location();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.RESOURSE_INFO;
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
		writeInt(buffer, templateId);
		writeInt(buffer, 1);
		writeFloat(buffer, loc.getX());
		writeFloat(buffer, loc.getY());
		writeFloat(buffer, loc.getZ());
	}
}
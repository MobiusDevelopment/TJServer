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

import tera.gameserver.model.worldobject.BonfireObject;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class CharmSmoke extends ServerPacket
{
	private static final ServerPacket instance = new CharmSmoke();
	
	/**
	 * Method getInstance.
	 * @param bonfire BonfireObject
	 * @return CharmSmoke
	 */
	public static CharmSmoke getInstance(BonfireObject bonfire)
	{
		final CharmSmoke packet = (CharmSmoke) instance.newInstance();
		packet.objectId = bonfire.getObjectId();
		packet.subId = bonfire.getSubId();
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
		return ServerPacketType.CHARM_SMOKE;
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
		writeInt(buffer, 0x00000006);
		writeInt(buffer, 0x0000ccb0);
	}
}

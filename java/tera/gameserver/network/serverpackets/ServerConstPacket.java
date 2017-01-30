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

import rlib.util.Util;

/**
 */
public abstract class ServerConstPacket extends ServerPacket
{
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.SendablePacket#isSynchronized()
	 */
	@Override
	public final boolean isSynchronized()
	{
		return false;
	}
	
	/**
	 * Method write.
	 * @param buffer ByteBuffer
	 * @see rlib.network.packets.SendablePacket#write(ByteBuffer)
	 */
	@Override
	public final void write(ByteBuffer buffer)
	{
		try
		{
			writeImpl(buffer);
		}
		catch (Exception e)
		{
			log.warning(this, e);
			log.warning(this, "Buffer " + buffer + "\n" + Util.hexdump(buffer.array(), buffer.position()));
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		super.writeImpl();
	}
}
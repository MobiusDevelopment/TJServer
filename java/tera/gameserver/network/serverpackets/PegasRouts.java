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
import java.util.Iterator;

import tera.gameserver.model.Route;
import tera.gameserver.model.TownInfo;
import tera.gameserver.network.ServerPacketType;

import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public class PegasRouts extends ServerPacket
{
	private static final ServerPacket instance = new PegasRouts();
	
	/**
	 * Method getInstance.
	 * @param routs Table<IntKey,Route>
	 * @param townId int
	 * @return PegasRouts
	 */
	public static PegasRouts getInstance(Table<IntKey, Route> routs, int townId)
	{
		final PegasRouts packet = (PegasRouts) instance.newInstance();
		packet.routs = routs;
		packet.townId = townId;
		return packet;
	}
	
	private Table<IntKey, Route> routs;
	private int townId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.ROUTE_PEGAS;
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
		writeInt(buffer, 0x0008000B);
		int n = 8;
		
		for (Iterator<Route> iterator = routs.iterator(); iterator.hasNext();)
		{
			final Route route = iterator.next();
			writeShort(buffer, n);
			
			if (!iterator.hasNext())
			{
				n = 0;
			}
			else
			{
				n += 24;
			}
			
			writeShort(buffer, n);
			writeInt(buffer, route.getIndex());
			writeInt(buffer, route.getPrice());
			writeInt(buffer, townId);
			final TownInfo target = route.getTarget();
			writeInt(buffer, target.getId());
			writeInt(buffer, 0);
		}
	}
}
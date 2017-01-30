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

import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class RequestNpcInteractionSuccess extends ServerPacket
{
	public static final byte SUCCEESS = 1;
	public static final byte NOT_SUCCESS = 0;
	private static final ServerPacket instance = new RequestNpcInteractionSuccess();
	
	/**
	 * Method getInstance.
	 * @param result int
	 * @return RequestNpcInteractionSuccess
	 */
	public static RequestNpcInteractionSuccess getInstance(int result)
	{
		final RequestNpcInteractionSuccess packet = (RequestNpcInteractionSuccess) instance.newInstance();
		packet.result = result;
		return packet;
	}
	
	private int result;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.NPC_REQUEST_INTERACTION_SUCCESS;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeByte(result);
	}
}
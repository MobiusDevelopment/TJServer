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
public class CharState extends ServerPacket
{
	private static final ServerPacket instance = new CharState();
	
	/**
	 * Method getInstance.
	 * @param objectId int
	 * @param subId int
	 * @param state int
	 * @return CharState
	 */
	public static CharState getInstance(int objectId, int subId, int state)
	{
		final CharState packet = (CharState) instance.newInstance();
		packet.objectId = objectId;
		packet.subId = subId;
		packet.state = state;
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int state;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.CHAR_STATE;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(objectId);
		writeInt(subId);
		writeByte(state);
	}
}
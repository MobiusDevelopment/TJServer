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
public class AddExp extends ServerPacket
{
	private static final ServerPacket instance = new AddExp();
	
	/**
	 * Method getInstance.
	 * @param exp int
	 * @param added int
	 * @param next int
	 * @param npcId int
	 * @param npcSubId int
	 * @return AddExp
	 */
	public static AddExp getInstance(int exp, int added, int next, int npcId, int npcSubId)
	{
		final AddExp packet = (AddExp) instance.newInstance();
		packet.exp = exp;
		packet.added = added;
		packet.next = next;
		packet.npcId = npcId;
		packet.npcSubId = npcSubId;
		return packet;
	}
	
	private int exp;
	private int added;
	private int next;
	private int npcId;
	private int npcSubId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_EXP;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(added);
		writeInt(0);
		writeLong(exp);
		writeLong(exp);
		writeInt(next);
		writeInt(0);
		writeInt(npcId);
		writeInt(npcSubId);
		writeInt(0);
		writeInt(0);
		writeInt(0);
		writeInt(0x0000210B);
		writeInt(0x3F800000);
		writeInt(0);
	}
}
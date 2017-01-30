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

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class PegasReplyPacket extends ServerPacket
{
	private static final ServerPacket instance = new PegasReplyPacket();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @return PegasReplyPacket
	 */
	public static PegasReplyPacket getInstance(Player player)
	{
		final PegasReplyPacket packet = (PegasReplyPacket) instance.newInstance();
		packet.player = player;
		return packet;
	}
	
	private Player player;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.FLY_PEGAS_REPLY_PACKET;
	}
	
	@Override
	protected void writeImpl()
	{
		writeShort(ServerPacketType.PLAYER_WAITING_ACTION.getOpcode());
		writeShort(0x2c);
		writeShort(0x3e);
		writeInt(0x40);
		writeInt(player.getObjectId());
		writeInt(player.getSubId());
		writeLong(0);
		writeInt(0x0F);
		writeInt(0x989b0300);
		writeLong(0);
		writeS(player.getName());
		writeShort(0);
	}
}
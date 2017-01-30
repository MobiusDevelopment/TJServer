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

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class UpdateStamina extends ServerPacket
{
	private static final ServerPacket instance = new UpdateStamina();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @return UpdateStamina
	 */
	public static UpdateStamina getInstance(Player player)
	{
		final UpdateStamina packet = (UpdateStamina) instance.newInstance();
		packet.currentHeart = player.getStamina();
		packet.maxHeart = player.getMaxStamina();
		return packet;
	}
	
	private int currentHeart;
	private int maxHeart;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.UPDATE_STAMINA;
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
		writeInt(buffer, currentHeart);
		writeInt(buffer, maxHeart);
		writeShort(buffer, 1);
	}
}
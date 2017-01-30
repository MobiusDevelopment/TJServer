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

import tera.gameserver.model.Character;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class PlayerCurrentMp extends ServerPacket
{
	public static final int INCREASE = 0;
	public static final int INCREASE_PLUS = 1;
	private static final ServerPacket instance = new PlayerCurrentMp();
	
	/**
	 * Method getInstance.
	 * @param player Character
	 * @param attacked Character
	 * @param countChange int
	 * @param type int
	 * @return PlayerCurrentMp
	 */
	public static PlayerCurrentMp getInstance(Character player, Character attacked, int countChange, int type)
	{
		final PlayerCurrentMp packet = (PlayerCurrentMp) instance.newInstance();
		packet.player = player;
		packet.countChange = countChange;
		packet.type = type;
		packet.attacked = attacked;
		return packet;
	}
	
	private Character player;
	private Character attacked;
	private int countChange;
	private int type;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_CURRENT_MP;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(player.getCurrentMp());
		writeInt(player.getMaxMp());
		writeInt(countChange);
		writeInt(type);
		writeInt(player.getObjectId());
		writeInt(player.getSubId());
		
		if (attacked != null)
		{
			writeInt(attacked.getObjectId());
			writeInt(attacked.getSubId());
		}
		else
		{
			writeInt(0);
			writeInt(0);
		}
	}
}
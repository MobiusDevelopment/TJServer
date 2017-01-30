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
public class PlayerBattleStance extends ServerPacket
{
	
	public static final int STANCE_ON = 1;
	
	public static final int STANCE_OFF = 0;
	
	private static final ServerPacket instance = new PlayerBattleStance();
	
	/**
	 * Method getInstance.
	 * @param character Character
	 * @param stance int
	 * @return PlayerBattleStance
	 */
	public static PlayerBattleStance getInstance(Character character, int stance)
	{
		final PlayerBattleStance packet = (PlayerBattleStance) instance.newInstance();
		packet.character = character;
		packet.stance = stance;
		return packet;
	}
	
	private Character character;
	
	private int stance;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		character = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.CHAR_BATTLE_STATE;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeInt(character.getObjectId());
		writeInt(character.getSubId());
		writeInt(stance);
		writeByte(0);
	}
}

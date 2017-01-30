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

import rlib.util.Strings;

/**
 * @author Ronn
 */
public class PlayerBankPanel extends ServerPacket
{
	private static final ServerPacket instance = new PlayerBankPanel();
	
	/**
	 * Method getInstance.
	 * @param owner Character
	 * @return PlayerBankPanel
	 */
	public static PlayerBankPanel getInstance(Character owner)
	{
		final PlayerBankPanel packet = (PlayerBankPanel) instance.newInstance();
		packet.name = owner.getName();
		packet.objectId = owner.getObjectId();
		packet.subId = owner.getSubId();
		return packet;
	}
	
	private String name;
	private int objectId;
	private int subId;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.NPC_BANK_PANEL;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeShort(44);
		writeShort(44 + Strings.length(name));
		writeShort(44 + Strings.length(name) + 2);
		writeShort(0);
		writeInt(objectId);
		writeInt(subId);
		writeLong(0);
		writeInt(26);
		writeInt(0);
		writeLong(0);
		writeString(name);
		writeShort(0);
	}
}
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

import rlib.util.Strings;

/**
 * @author Ronn
 * @created 26.04.2012
 */
public class ActionInvite extends ServerPacket
{
	private static final ServerPacket instance = new ActionInvite();
	
	/**
	 * Method getInstance.
	 * @param actorName String
	 * @param enemyName String
	 * @param id int
	 * @param objectId int
	 * @return ActionInvite
	 */
	public static ActionInvite getInstance(String actorName, String enemyName, int id, int objectId)
	{
		final ActionInvite packet = (ActionInvite) instance.newInstance();
		packet.actorName = actorName;
		packet.enemyName = enemyName;
		packet.id = id;
		packet.objectId = objectId;
		return packet;
	}
	
	private String actorName;
	
	private String enemyName;
	
	private int id;
	
	private int objectId;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		actorName = null;
		enemyName = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_ACTION_INVITE;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeShort(22);
		writeShort(22 + Strings.length(actorName));
		writeShort(Strings.length(actorName));
		writeInt(id);
		writeInt(objectId);
		writeByte(0x26);
		writeShort(0x46);
		writeByte(0);
		writeString(actorName);
		writeString(enemyName);
		writeByte(0);
	}
}

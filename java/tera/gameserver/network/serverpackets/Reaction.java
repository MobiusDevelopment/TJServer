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
import tera.gameserver.model.ReactionType;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class Reaction extends ServerPacket
{
	private static final ServerPacket instance = new Reaction();
	
	/**
	 * Method getInstance.
	 * @param actor Character
	 * @param type ReactionType
	 * @return Reaction
	 */
	public static Reaction getInstance(Character actor, ReactionType type)
	{
		final Reaction packet = (Reaction) instance.newInstance();
		packet.objectId = actor.getObjectId();
		packet.subId = actor.getSubId();
		packet.type = type;
		return packet;
	}
	
	private ReactionType type;
	private int objectId;
	private int subId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.REACTION;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(objectId);
		writeInt(subId);
		writeInt(type.ordinal());
	}
}
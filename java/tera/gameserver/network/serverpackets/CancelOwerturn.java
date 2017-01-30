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
public class CancelOwerturn extends ServerPacket
{
	private static final ServerPacket instance = new CancelOwerturn();
	
	/**
	 * Method getInstance.
	 * @param actor Character
	 * @return CancelOwerturn
	 */
	public static CancelOwerturn getInstance(Character actor)
	{
		final CancelOwerturn packet = (CancelOwerturn) instance.newInstance();
		packet.actor = actor;
		return packet;
	}
	
	private Character actor;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		actor = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.CANCEL_OWERTURN;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeInt(actor.getObjectId());
		writeInt(actor.getSubId());
		writeFloat(actor.getX());
		writeFloat(actor.getY());
		writeFloat(actor.getZ());
		writeShort(actor.getHeading());
		writeInt(actor.getModelId());
		writeInt(actor.getOwerturnId());
		writeInt(0);
		writeInt(0);
	}
}
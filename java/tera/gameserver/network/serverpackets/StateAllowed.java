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
public class StateAllowed extends ServerPacket
{
	private static final ServerPacket instance = new StateAllowed();
	
	/**
	 * Method getInstance.
	 * @param actor Character
	 * @param stateId int
	 * @return StateAllowed
	 */
	public static StateAllowed getInstance(Character actor, int stateId)
	{
		final StateAllowed packet = (StateAllowed) instance.newInstance();
		packet.actor = actor;
		packet.stateId = stateId;
		return packet;
	}
	
	private Character actor;
	private int stateId;
	
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
		return ServerPacketType.STATE_ALLOWED;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeInt(actor.getObjectId());
		writeInt(actor.getSubId());
		writeInt(stateId);
	}
}
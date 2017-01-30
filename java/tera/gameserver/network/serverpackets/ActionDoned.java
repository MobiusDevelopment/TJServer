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
 * @created 07.03.2012
 */
public class ActionDoned extends ServerPacket
{
	private static final ServerPacket instance = new ActionDoned();
	
	/**
	 * Method getInstance.
	 * @param actorId int
	 * @param actorSubId int
	 * @param enemyId int
	 * @param enemySubId int
	 * @param id int
	 * @param objectId int
	 * @return ActionDoned
	 */
	public static ActionDoned getInstance(int actorId, int actorSubId, int enemyId, int enemySubId, int id, int objectId)
	{
		final ActionDoned packet = (ActionDoned) instance.newInstance();
		packet.actorId = actorId;
		packet.actorSubId = actorSubId;
		packet.enemyId = enemyId;
		packet.enemySubId = enemySubId;
		packet.id = id;
		packet.objectId = objectId;
		return packet;
	}
	
	private int actorId;
	private int actorSubId;
	private int enemyId;
	private int enemySubId;
	private int id;
	private int objectId;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_ACTION_DONED;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeInt(actorId);
		writeInt(actorSubId);
		writeInt(enemyId);
		writeInt(enemySubId);
		writeInt(id);
		writeInt(objectId);
	}
}
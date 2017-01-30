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
public class ActionDialogCancel extends ServerPacket
{
	private static final ServerPacket instance = new ActionDialogCancel();
	
	/**
	 * Method getInstance.
	 * @param actor Player
	 * @param enemy Player
	 * @param id int
	 * @param objectId int
	 * @return ActionDialogCancel
	 */
	public static ActionDialogCancel getInstance(Player actor, Player enemy, int id, int objectId)
	{
		final ActionDialogCancel packet = (ActionDialogCancel) instance.newInstance();
		packet.actorId = actor.getObjectId();
		packet.actorSubId = actor.getSubId();
		packet.enemyId = enemy.getObjectId();
		packet.enemySubId = enemy.getSubId();
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
		return ServerPacketType.CLOSE_TRADE;
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

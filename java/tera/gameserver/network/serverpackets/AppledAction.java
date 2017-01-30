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

import rlib.util.Strings;

/**
 * @author Ronn
 */
public class AppledAction extends ServerPacket
{
	private static final ServerPacket instance = new AppledAction();
	
	/**
	 * Method newInstance.
	 * @param player Player
	 * @param enemy Player
	 * @param type int
	 * @param objectId int
	 * @return AppledAction
	 */
	public static AppledAction newInstance(Player player, Player enemy, int type, int objectId)
	{
		final AppledAction packet = (AppledAction) instance.newInstance();
		packet.player = player.getName();
		packet.enemy = enemy == null ? Strings.EMPTY : enemy.getName();
		packet.actorId = player.getObjectId();
		packet.actorSubId = player.getSubId();
		packet.enemyId = enemy == null ? 0 : enemy.getObjectId();
		packet.enemySubId = enemy == null ? 0 : enemy.getSubId();
		packet.type = type;
		packet.objectId = objectId;
		return packet;
	}
	
	private String player;
	private String enemy;
	private int actorId;
	private int actorSubId;
	private int enemyId;
	private int enemySubId;
	private int type;
	private int objectId;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
		enemy = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_WAITING_ACTION;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		int n = 44;
		writeShort(n);
		writeShort(n += Strings.length(player));
		writeShort(n);
		writeShort(Strings.length(enemy));
		writeInt(actorId);
		writeInt(actorSubId);
		writeInt(enemyId);
		writeInt(enemySubId);
		writeInt(type);
		writeInt(objectId);
		writeInt(0);
		writeByte(48);
		writeShort(165);
		writeByte(0);
		writeString(player);
		writeString(enemy);
		writeByte(0);
	}
}
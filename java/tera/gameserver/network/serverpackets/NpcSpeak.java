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
import tera.gameserver.model.npc.Npc;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class NpcSpeak extends ServerPacket
{
	private static final ServerPacket instance = new NpcSpeak();
	
	/**
	 * Method getInstance.
	 * @param target Character
	 * @param npc Npc
	 * @return NpcSpeak
	 */
	public static NpcSpeak getInstance(Character target, Npc npc)
	{
		final NpcSpeak packet = (NpcSpeak) instance.newInstance();
		packet.target = target;
		packet.npc = npc;
		return packet;
	}
	
	private Character target;
	private Character npc;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		target = null;
		npc = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.NPC_SPEAK;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(npc.getObjectId());
		writeInt(npc.getSubId());
		writeInt(target != null ? target.getObjectId() : 0);
		writeInt(target != null ? target.getSubId() : 0);
		writeInt(0);
		writeInt(0x00000001);
		writeInt(0);
	}
}
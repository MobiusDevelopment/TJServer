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
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class SitOnTransport extends ServerPacket
{
	private static final ServerPacket instance = new SitOnTransport();
	
	/**
	 * Method getInstance.
	 * @param player Character
	 * @param skill Skill
	 * @return SitOnTransport
	 */
	public static SitOnTransport getInstance(Character player, Skill skill)
	{
		final SitOnTransport packet = (SitOnTransport) instance.newInstance();
		packet.player = player;
		packet.skill = skill;
		return packet;
	}
	
	private Character player;
	
	private Skill skill;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
		skill = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return null;
	}
	
	@Override
	protected void writeImpl()
	{
		writeShort(0x21E8);
		writeInt(player.getObjectId());
		writeInt(player.getSubId());
		writeInt(1);
		writeInt(skill.getId() + 67108864);
	}
}

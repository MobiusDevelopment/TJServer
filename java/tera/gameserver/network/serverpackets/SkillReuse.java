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
 */
public class SkillReuse extends ServerPacket
{
	private static final ServerPacket instance = new SkillReuse();
	
	/**
	 * Method getInstance.
	 * @param skillId int
	 * @param reuseDelay int
	 * @return SkillReuse
	 */
	public static SkillReuse getInstance(int skillId, int reuseDelay)
	{
		final SkillReuse packet = (SkillReuse) instance.newInstance();
		packet.skillId = skillId;
		packet.reuseDelay = reuseDelay;
		return packet;
	}
	
	private int skillId;
	private int reuseDelay;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_REUSE;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(skillId);
		writeInt(reuseDelay);
	}
}
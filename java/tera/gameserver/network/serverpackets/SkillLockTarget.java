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

import java.nio.ByteBuffer;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class SkillLockTarget extends ServerPacket
{
	private static final ServerPacket instance = new SkillLockTarget();
	
	/**
	 * Method getInstance.
	 * @param target Character
	 * @param skill Skill
	 * @param locked boolean
	 * @return SkillLockTarget
	 */
	public static SkillLockTarget getInstance(Character target, Skill skill, boolean locked)
	{
		final SkillLockTarget packet = (SkillLockTarget) instance.newInstance();
		packet.id = target.getObjectId();
		packet.subId = target.getSubId();
		packet.skillId = skill.getIconId();
		packet.locked = locked ? 1 : 0;
		return packet;
	}
	
	private int id;
	private int subId;
	private int skillId;
	private int locked;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_LOCK_TARGET;
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.SendablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	/**
	 * Method writeImpl.
	 * @param buffer ByteBuffer
	 */
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeInt(buffer, id);
		writeInt(buffer, subId);
		writeInt(buffer, skillId);
		writeByte(buffer, locked);
	}
}
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
import tera.gameserver.network.ServerPacketType;
import tera.util.Location;

/**
 * @author Ronn
 */
public class SkillEnd extends ServerPacket
{
	private static final ServerPacket instance = new SkillEnd();
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param castId int
	 * @param skillId int
	 * @return SkillEnd
	 */
	public static SkillEnd getInstance(Character caster, int castId, int skillId)
	{
		final SkillEnd packet = (SkillEnd) instance.newInstance();
		
		if (caster == null)
		{
			log.warning(packet, new Exception("not found caster"));
			return null;
		}
		
		packet.casterId = caster.getObjectId();
		packet.casterSubId = caster.getSubId();
		packet.modelId = caster.getModelId();
		packet.skillId = skillId;
		packet.castId = castId;
		caster.getLoc(packet.loc);
		return packet;
	}
	
	private final Location loc;
	private int casterId;
	private int casterSubId;
	private int modelId;
	private int skillId;
	private int castId;
	
	public SkillEnd()
	{
		loc = new Location();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_END;
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
		writeInt(buffer, casterId);
		writeInt(buffer, casterSubId);
		writeFloat(buffer, loc.getX());
		writeFloat(buffer, loc.getY());
		writeFloat(buffer, loc.getZ());
		writeShort(buffer, loc.getHeading());
		writeInt(buffer, modelId);
		writeInt(buffer, skillId);
		writeInt(buffer, 0);
		writeInt(buffer, castId);
	}
}
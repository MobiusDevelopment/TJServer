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
public class SkillStart extends ServerPacket
{
	private static final ServerPacket instance = new SkillStart();
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param skillId int
	 * @param castId int
	 * @param state int
	 * @return SkillStart
	 */
	public static SkillStart getInstance(Character caster, int skillId, int castId, int state)
	{
		final SkillStart packet = (SkillStart) instance.newInstance();
		
		if (caster == null)
		{
			log.warning(packet, new Exception("Caster not found."));
			return null;
		}
		
		packet.casterId = caster.getObjectId();
		packet.casterSubId = caster.getSubId();
		packet.modelId = caster.getModelId();
		packet.atkSpd = caster.getAtkSpd() / 100F;
		packet.skillId = skillId;
		packet.state = state;
		packet.castId = castId;
		caster.getLoc(packet.loc);
		return packet;
	}
	
	private final Location loc;
	private int casterId;
	private int casterSubId;
	private int modelId;
	private int skillId;
	private int state;
	private int castId;
	private float atkSpd;
	
	public SkillStart()
	{
		super();
		loc = new Location();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_START;
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
		writeInt(buffer, 0);
		writeInt(buffer, casterId);
		writeInt(buffer, casterSubId);
		writeFloat(buffer, loc.getX());
		writeFloat(buffer, loc.getY());
		writeFloat(buffer, loc.getZ());
		writeShort(buffer, loc.getHeading());
		writeInt(buffer, modelId);
		writeInt(buffer, skillId);
		writeInt(buffer, state);
		writeFloat(buffer, atkSpd);
		
		/*
		 * writeShort(buffer, 0); writeByte(buffer, atkSpd); writeByte(buffer, 0x3F);
		 */
		
		writeInt(buffer, castId);
		
		/*
		 * writeUid(skillProcessor.creature) writeF(skillProcessor.args.startPosition.x) writeF(skillProcessor.args.startPosition.y) writeF(skillProcessor.args.startPosition.z) writeH(skillProcessor.args.startPosition.heading) writeD(skillProcessor.creature.templateId)
		 * writeD(skillProcessor.args.skillId + 0x4000000) writeD(skillProcessor.stage) writeF(skillProcessor.speed) writeD(skillProcessor.uid)
		 */
		
		/*
		 * if(true) { writeInt(buffer, 0); // 11 57 30 04 writeInt(buffer, 50); writeInt(buffer, 200); // 200-2100 writeInt(buffer, 0x6666a63F); writeShort(buffer, 0); writeByte(buffer, atkSpd); writeByte(buffer, 0x3F); }
		 */
	}
}
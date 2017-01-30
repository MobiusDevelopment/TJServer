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
import tera.gameserver.model.skillengine.shots.Shot;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class StartObjectShot extends ServerPacket
{
	private static final ServerPacket instance = new StartObjectShot();
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param skill Skill
	 * @param shot Shot
	 * @return StartObjectShot
	 */
	public static StartObjectShot getInstance(Character caster, Skill skill, Shot shot)
	{
		final StartObjectShot packet = (StartObjectShot) instance.newInstance();
		packet.casterId = caster.getObjectId();
		packet.casterSubId = caster.getSubId();
		packet.casterTemplateId = caster.getModelId();
		packet.startX = caster.getX();
		packet.startY = caster.getY();
		packet.startZ = (caster.getZ() + caster.getGeomHeight()) - 20;
		packet.damageId = skill.getDamageId();
		packet.speed = skill.getSpeed() + skill.getSpeedOffset();
		packet.objectId = shot.getObjectId();
		packet.subId = shot.getSubId();
		packet.targetX = shot.getTargetX();
		packet.targetY = shot.getTargetY();
		packet.targetZ = shot.getTargetZ();
		packet.casterTemplateType = caster.getTemplateType();
		return packet;
	}
	
	private int casterId;
	private int casterSubId;
	private int casterTemplateId;
	private int casterTemplateType;
	private int objectId;
	private int subId;
	private int speed;
	private int damageId;
	private float startX;
	private float startY;
	private float startZ;
	private float targetX;
	private float targetY;
	private float targetZ;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_NPC_SLOW_SHOT;
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
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeInt(buffer, casterTemplateType);
		writeInt(buffer, damageId);
		writeFloat(buffer, startX);
		writeFloat(buffer, startY);
		writeFloat(buffer, startZ);
		writeFloat(buffer, targetX);
		writeFloat(buffer, targetY);
		writeFloat(buffer, targetZ);
		writeByte(buffer, 1);
		writeByte(buffer, 0);
		writeByte(buffer, 00);
		writeShort(buffer, 17048 + speed);
		writeInt(buffer, casterId);
		writeInt(buffer, casterSubId);
		writeInt(buffer, casterTemplateId);
	}
}
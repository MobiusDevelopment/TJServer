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
public class StartSlowShot extends ServerPacket
{
	private static final ServerPacket instance = new StartSlowShot();
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param skill Skill
	 * @param objectId int
	 * @param subId int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return StartSlowShot
	 */
	public static StartSlowShot getInstance(Character caster, Skill skill, int objectId, int subId, float targetX, float targetY, float targetZ)
	{
		final StartSlowShot packet = (StartSlowShot) instance.newInstance();
		packet.caster = caster;
		packet.skill = skill;
		packet.objectId = objectId;
		packet.subId = subId;
		packet.targetX = targetX;
		packet.targetY = targetY;
		packet.targetZ = targetZ;
		return packet;
	}
	
	private Character caster;
	private Skill skill;
	private int objectId;
	private int subId;
	private float targetX;
	private float targetY;
	private float targetZ;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		caster = null;
		skill = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_SLOW_SHOT;
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
		writeInt(buffer, caster.getObjectId());
		writeInt(buffer, caster.getSubId());
		writeInt(buffer, caster.getModelId());
		writeInt(buffer, 0);
		writeInt(buffer, objectId);
		writeInt(buffer, subId);
		writeInt(buffer, skill.getDamageId());
		writeFloat(buffer, caster.getX());
		writeFloat(buffer, caster.getY());
		writeFloat(buffer, (caster.getZ() + caster.getGeom().getHeight()) - 5F);
		writeFloat(buffer, targetX);
		writeFloat(buffer, targetY);
		writeFloat(buffer, targetZ);
		writeShort(buffer, 0x8000);
		writeShort(buffer, 17048 + skill.getSpeed() + skill.getSpeedOffset());
	}
}
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
public class StartFastShot extends ServerPacket
{
	private static final ServerPacket instance = new StartFastShot();
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param target Character
	 * @param skill Skill
	 * @param castId int
	 * @return StartFastShot
	 */
	public static StartFastShot getInstance(Character caster, Character target, Skill skill, int castId)
	{
		final StartFastShot packet = (StartFastShot) instance.newInstance();
		packet.caster = caster;
		packet.target = target;
		packet.skill = skill;
		packet.castId = castId;
		return packet;
	}
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param skill Skill
	 * @param castId int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return StartFastShot
	 */
	public static StartFastShot getInstance(Character caster, Skill skill, int castId, float targetX, float targetY, float targetZ)
	{
		final StartFastShot packet = (StartFastShot) instance.newInstance();
		packet.caster = caster;
		packet.skill = skill;
		packet.targetX = targetX;
		packet.targetY = targetY;
		packet.targetZ = targetZ;
		return packet;
	}
	
	private Character caster;
	private Character target;
	private Skill skill;
	private int castId;
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
		target = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_FAST_SHOT;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		
		if (target != null)
		{
			writeInt(0x00200001);
			writeInt(0x00300001);
			writeInt(caster.getObjectId());
			writeInt(caster.getSubId());
			writeInt(caster.getModelId());
			writeInt(skill.getIconId());
			writeInt(castId);
			writeInt(32);
			writeInt(0);
			writeInt(target.getObjectId());
			writeInt(target.getSubId());
			writeInt(48);
			writeFloat(target.getX());
			writeFloat(target.getY());
			writeFloat(target.getZ());
		}
		else
		{
			writeInt(0);
			writeInt(0x00200001);
			writeInt(caster.getObjectId());
			writeInt(caster.getSubId());
			writeInt(caster.getModelId());
			writeInt(skill.getIconId());
			writeInt(castId);
			writeInt(32);
			writeFloat(targetX);
			writeFloat(targetY);
			writeFloat(targetZ);
		}
	}
}
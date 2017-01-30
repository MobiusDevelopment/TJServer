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
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class MoveSkill extends ServerPacket
{
	private static final ServerPacket instance = new MoveSkill();
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param target Character
	 * @return MoveSkill
	 */
	public static MoveSkill getInstance(Character caster, Character target)
	{
		final MoveSkill packet = (MoveSkill) instance.newInstance();
		packet.caster = caster;
		packet.target = target;
		return packet;
	}
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return MoveSkill
	 */
	public static MoveSkill getInstance(Character caster, float targetX, float targetY, float targetZ)
	{
		final MoveSkill packet = (MoveSkill) instance.newInstance();
		packet.caster = caster;
		packet.targetX = targetX;
		packet.targetY = targetY;
		packet.targetZ = targetZ;
		return packet;
	}
	
	private Character caster;
	private Character target;
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
		return ServerPacketType.SKILL_MOVE;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		
		if (target != null)
		{
			writeInt(caster.getObjectId());
			writeInt(caster.getSubId());
			writeInt(target.getObjectId());
			writeInt(target.getSubId());
			writeFloat(target.getX());
			writeFloat(target.getY());
			writeFloat(target.getZ());
			writeShort(caster.calcHeading(caster == target ? caster.getHeading() : target.getX(), target.getY()));
		}
		else
		{
			writeInt(caster.getObjectId());
			writeInt(caster.getSubId());
			writeInt(0);
			writeInt(0);
			writeFloat(targetX);
			writeFloat(targetY);
			writeFloat(targetZ);
			writeShort(caster.calcHeading(targetX, targetY));
		}
	}
}
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
import java.nio.ByteOrder;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.ServerPacketType;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class SkillLockAttack extends ServerPacket
{
	private static final ServerPacket instance = new SkillLockAttack();
	
	/**
	 * Method getInstance.
	 * @param caster Character
	 * @param targets Array<Character>
	 * @param skill Skill
	 * @param castId int
	 * @return SkillLockAttack
	 */
	public static SkillLockAttack getInstance(Character caster, Array<Character> targets, Skill skill, int castId)
	{
		final SkillLockAttack packet = (SkillLockAttack) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			int n = 28;
			packet.writeShort(buffer, targets.size());
			packet.writeShort(buffer, n);
			packet.writeInt(buffer, caster.getObjectId());
			packet.writeInt(buffer, caster.getSubId());
			packet.writeInt(buffer, caster.getModelId());
			packet.writeInt(buffer, skill.getIconId());
			packet.writeInt(buffer, castId);
			targets.readLock();
			
			try
			{
				final Character[] array = targets.array();
				
				for (int i = 0, length = targets.size(); i < length; i++)
				{
					final Character target = array[i];
					packet.writeShort(buffer, n);
					
					if (i == (length - 1))
					{
						n = 0;
					}
					else
					{
						n += 12;
					}
					
					packet.writeShort(buffer, n);
					packet.writeInt(buffer, target.getObjectId());
					packet.writeInt(buffer, target.getSubId());
				}
			}
			
			finally
			{
				targets.readUnlock();
			}
			return packet;
		}
		
		finally
		{
			buffer.flip();
		}
	}
	
	private final ByteBuffer prepare;
	
	public SkillLockAttack()
	{
		prepare = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		prepare.clear();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SKILL_LOCK_ATTACK;
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
		final ByteBuffer prepare = getPrepare();
		buffer.put(prepare.array(), 0, prepare.limit());
	}
	
	/**
	 * Method getPrepare.
	 * @return ByteBuffer
	 */
	public ByteBuffer getPrepare()
	{
		return prepare;
	}
}
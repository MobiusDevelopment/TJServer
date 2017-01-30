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

import tera.gameserver.model.SkillLearn;
import tera.gameserver.model.npc.interaction.dialogs.SkillShopDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.ServerPacketType;

import rlib.util.array.Array;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public class SkillShopList extends ServerPacket
{
	private static final ServerPacket instance = new SkillShopList();
	
	/**
	 * Method getInstance.
	 * @param skills Array<SkillLearn>
	 * @param player Player
	 * @return SkillShopList
	 */
	public static SkillShopList getInstance(Array<SkillLearn> skills, Player player)
	{
		final SkillShopList packet = (SkillShopList) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			final SkillLearn last = skills.last();
			packet.writeInt(buffer, 524422);
			
			if ((player == null) || skills.isEmpty() || (last == null))
			{
				return packet;
			}
			
			int beginByte = 8;
			final SkillLearn[] array = skills.array();
			final Table<IntKey, Skill> currentSkills = player.getSkills();
			
			for (int i = 0, length = skills.size() - 1; i <= length; i++)
			{
				final SkillLearn skill = array[i];
				
				if (skill == null)
				{
					continue;
				}
				
				packet.writeShort(buffer, beginByte);
				
				if (skill == last)
				{
					beginByte = 0;
				}
				else
				{
					beginByte += skill.getReplaceId() == 0 ? 26 : 35;
				}
				
				packet.writeShort(buffer, beginByte);
				
				if (skill.getReplaceId() == 0)
				{
					packet.writeInt(buffer, 0);
				}
				else
				{
					packet.writeShort(buffer, 1);
					packet.writeShort(buffer, beginByte - 9);
				}
				
				packet.writeInt(buffer, 0);
				
				if (skill.getClassId() > 0)
				{
					packet.writeInt(buffer, skill.getId());
				}
				else
				{
					packet.writeInt(buffer, skill.getId());
				}
				
				packet.writeByte(buffer, skill.isPassive() ? 0 : 1);
				packet.writeInt(buffer, skill.getPrice());
				packet.writeInt(buffer, skill.getMinLevel());
				packet.writeByte(buffer, SkillShopDialog.isLearneableSkill(player, currentSkills, skill, false) ? 1 : 0);
				
				if (skill.getReplaceId() != 0)
				{
					packet.writeShort(buffer, beginByte - 9);
					packet.writeShort(buffer, 0);
					packet.writeInt(buffer, skill.getReplaceId());
					packet.writeByte(buffer, 1);
				}
			}
			
			return packet;
		}
		
		finally
		{
			buffer.flip();
		}
	}
	
	private final ByteBuffer prepare;
	
	public SkillShopList()
	{
		prepare = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.NPC_DIALOG_SKILL_LEARN;
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
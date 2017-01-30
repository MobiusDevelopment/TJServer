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

import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.ServerPacketType;

import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public final class SkillListInfo extends ServerPacket
{
	private static final ServerPacket instance = new SkillListInfo();
	
	public static SkillListInfo getInstance(Player player)
	{
		SkillListInfo packet = (SkillListInfo) instance.newInstance();
		
		ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			Table<IntKey, Skill> table = player.getSkills();
			
			Array<Skill> skills = table.values(packet.getSkills());
			
			Skill[] array = skills.array();
			
			int counter = 0;
			
			int index = 8;
			
			for (int i = 0, length = skills.size(); i < length; i++)
			{
				Skill skill = array[i];
				
				if ((skill.getLevel() < 2) && skill.isVisibleOnSkillList())
				{
					counter++;
				}
			}
			
			if (counter > 0)
			{
				for (int i = 0, length = skills.size(); i < length; i++)
				{
					Skill skill = array[i];
					
					if ((skill.getLevel() < 2) && skill.isVisibleOnSkillList())
					{
						counter--;
						
						packet.writeShort(buffer, index);
						
						if (counter == 0)
						{
							index = 0;
						}
						else
						{
							index += 9;
						}
						
						packet.writeShort(buffer, index);
						packet.writeInt(buffer, skill.getId());
						packet.writeByte(buffer, skill.isActive() ? 1 : 0);
					}
				}
			}
			
			return packet;
		}
		finally
		{
			buffer.flip();
		}
	}
	
	private final Array<Skill> skills;
	
	private final ByteBuffer prepare;
	
	public SkillListInfo()
	{
		this.skills = Arrays.toArray(Skill.class);
		this.prepare = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	@Override
	public void finalyze()
	{
		prepare.clear();
		skills.clear();
	}
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_SKILL_LIST;
	}
	
	public ByteBuffer getPrepare()
	{
		return prepare;
	}
	
	public Array<Skill> getSkills()
	{
		return skills;
	}
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		
		writeInt(buffer, 0x00080007);
		
		ByteBuffer prepare = getPrepare();
		
		buffer.put(prepare.array(), 0, prepare.limit());
	}
}
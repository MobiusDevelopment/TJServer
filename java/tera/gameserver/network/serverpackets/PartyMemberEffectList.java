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
import tera.gameserver.model.EffectList;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.network.ServerPacketType;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class PartyMemberEffectList extends ServerPacket
{
	private static final ServerPacket instance = new PartyMemberEffectList();
	
	/**
	 * Method getInstance.
	 * @param character Character
	 * @return PartyMemberEffectList
	 */
	public static PartyMemberEffectList getInstance(Character character)
	{
		final PartyMemberEffectList packet = (PartyMemberEffectList) instance.newInstance();
		final EffectList effectList = character.getEffectList();
		
		if (effectList == null)
		{
			return packet;
		}
		
		final ByteBuffer buffer = packet.getPrepare();
		effectList.lock();
		
		try
		{
			int bytes = 20;
			final int lenght = (((effectList.size() - 1) * 16) + bytes);
			packet.writeShort(buffer, 5);
			packet.writeShort(buffer, bytes);
			packet.writeShort(buffer, 1);
			packet.writeShort(buffer, lenght);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, character.getObjectId());
			final Array<Effect> effects = effectList.getEffects();
			final Effect[] array = effects.array();
			
			for (int i = 0, length = effects.size(); i < length; i++)
			{
				final Effect effect = array[i];
				
				if ((effect == null) || effect.isEnded())
				{
					continue;
				}
				
				packet.writeShort(buffer, bytes);
				bytes += 16;
				
				if (lenght < bytes)
				{
					bytes = 0;
				}
				
				packet.writeShort(buffer, bytes);
				packet.writeInt(buffer, effect.getEffectId());
				packet.writeInt(buffer, effect.getTimeEnd() * 1000);
				packet.writeInt(buffer, 1);
			}
		}
		
		finally
		{
			effectList.unlock();
		}
		buffer.flip();
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public PartyMemberEffectList()
	{
		prepare = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.PARTY_MEMBER_EFFECT;
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

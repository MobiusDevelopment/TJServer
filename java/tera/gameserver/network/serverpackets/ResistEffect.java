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
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class ResistEffect extends ServerPacket
{
	private static final ServerPacket instance = new ResistEffect();
	public static final int RESISTED = 2;
	public static final int IMMUNE = 3;
	
	/**
	 * Method getInstance.
	 * @param character Character
	 * @param effect Effect
	 * @param result int
	 * @return ResistEffect
	 */
	public static ResistEffect getInstance(Character character, Effect effect, int result)
	{
		final ResistEffect packet = (ResistEffect) instance.newInstance();
		packet.objectId = character.getObjectId();
		packet.subId = character.getSubId();
		packet.effectId = effect.getEffectId();
		packet.result = result;
		return packet;
	}
	
	private int objectId;
	private int subId;
	private int effectId;
	private int result;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.RESIST_EFFECT;
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
		writeInt(buffer, effectId);
		writeShort(buffer, result);
		writeByte(buffer, 0);
	}
}
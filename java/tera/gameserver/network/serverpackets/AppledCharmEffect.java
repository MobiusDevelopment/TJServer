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
public class AppledCharmEffect extends ServerPacket
{
	private static final ServerPacket instance = new AppledCharmEffect();
	
	/**
	 * Method getInstance.
	 * @param effected Character
	 * @param effect Effect
	 * @return ServerPacket
	 */
	public static ServerPacket getInstance(Character effected, Effect effect)
	{
		final AppledCharmEffect packet = (AppledCharmEffect) instance.newInstance();
		packet.effected = effected;
		packet.effectId = effect.getEffectId();
		packet.time = effect.getTimeForPacket();
		return packet;
	}
	
	/**
	 * Method getInstance.
	 * @param effected Character
	 * @param id int
	 * @param time int
	 * @return ServerPacket
	 */
	public static ServerPacket getInstance(Character effected, int id, int time)
	{
		final AppledCharmEffect packet = (AppledCharmEffect) instance.newInstance();
		packet.effected = effected;
		packet.effectId = id;
		packet.time = time;
		return packet;
	}
	
	private Character effected;
	
	private int effectId;
	
	private int time;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		effected = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.APPLED_CHARM_PACKET;
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
		if (effected == null)
		{
			return;
		}
		
		writeOpcode(buffer);
		writeInt(buffer, effected.getObjectId());
		writeInt(buffer, effected.getSubId());
		writeInt(buffer, effectId);
		writeByte(buffer, 1);
		writeInt(buffer, time);
	}
}

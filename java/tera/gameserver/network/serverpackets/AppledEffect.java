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
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class AppledEffect extends ServerPacket
{
	private static final ServerPacket instance = new AppledEffect();
	
	/**
	 * Method getInstance.
	 * @param effector Character
	 * @param effected Character
	 * @param effect Effect
	 * @return ServerPacket
	 */
	public static ServerPacket getInstance(Character effector, Character effected, Effect effect)
	{
		final AppledEffect packet = (AppledEffect) instance.newInstance();
		packet.effector = effector;
		packet.effected = effected;
		packet.effectId = effect.getEffectId();
		packet.time = effect.getTimeForPacket();
		return packet;
	}
	
	/**
	 * Method getInstance.
	 * @param effector Character
	 * @param effected Character
	 * @param effectId int
	 * @param time int
	 * @return ServerPacket
	 */
	public static ServerPacket getInstance(Character effector, Character effected, int effectId, int time)
	{
		final AppledEffect packet = (AppledEffect) instance.newInstance();
		packet.effector = effector;
		packet.effected = effected;
		packet.effectId = effectId;
		packet.time = time;
		return packet;
	}
	
	private Character effector;
	
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
		effector = null;
		effected = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.APPLED_BUFF_PACKET;
	}
	
	@Override
	protected void writeImpl()
	{
		if ((effected == null) || (effector == null))
		{
			return;
		}
		
		writeOpcode();
		writeInt(effected.getObjectId());
		writeInt(effected.getSubId());
		writeInt(effector.getObjectId());
		writeInt(effector.getSubId());
		writeInt(effectId);
		writeInt(time);
		writeInt(1);
	}
}

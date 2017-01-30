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
public class ObjectPosition extends ServerPacket
{
	private static final ServerPacket instance = new ObjectPosition();
	
	/**
	 * Method getInstance.
	 * @param attacker Character
	 * @param target Character
	 * @return ObjectPosition
	 */
	public static ObjectPosition getInstance(Character attacker, Character target)
	{
		final ObjectPosition packet = (ObjectPosition) instance.newInstance();
		packet.attackedId = attacker.getObjectId();
		packet.attackerSubId = attacker.getSubId();
		packet.targetId = target.getObjectId();
		packet.targetSubId = target.getSubId();
		packet.heading = target.getHeading();
		packet.x = target.getX();
		packet.y = target.getY();
		packet.z = target.getZ();
		return packet;
	}
	
	private int attackedId;
	private int attackerSubId;
	private int targetId;
	private int targetSubId;
	private int heading;
	private float x;
	private float y;
	private float z;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.OBJECT_POSITION;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeInt(attackedId);
		writeInt(attackerSubId);
		writeInt(targetId);
		writeInt(targetSubId);
		writeFloat(x);
		writeFloat(y);
		writeFloat(z);
		writeShort(heading);
		writeShort(1);
	}
}
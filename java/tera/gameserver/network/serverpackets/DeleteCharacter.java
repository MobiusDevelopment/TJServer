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
public class DeleteCharacter extends ServerPacket
{
	public static final int DEAD = 5;
	public static final int DISAPPEARS_DUST = 4;
	public static final int DISAPPEARS = 1;
	private static final ServerPacket instance = new DeleteCharacter();
	
	/**
	 * Method getInstance.
	 * @param character Character
	 * @param type int
	 * @return DeleteCharacter
	 */
	public static DeleteCharacter getInstance(Character character, int type)
	{
		final DeleteCharacter packet = (DeleteCharacter) instance.newInstance();
		packet.type = type;
		packet.objectId = character.getObjectId();
		packet.subId = character.getSubId();
		packet.x = character.getX();
		packet.y = character.getY();
		packet.z = character.getZ();
		return packet;
	}
	
	private int type;
	private int objectId;
	private int subId;
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
		return ServerPacketType.DELETE_OBJECT;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(objectId);
		writeInt(subId);
		writeFloat(x);
		writeFloat(y);
		writeFloat(z);
		writeByte(type);
		writeInt(0);
		writeShort(0);
		writeByte(0);
	}
}
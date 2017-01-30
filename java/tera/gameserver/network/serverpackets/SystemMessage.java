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

import tera.gameserver.model.MessageType;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class SystemMessage extends ServerPacket
{
	private static final SystemMessage instance = new SystemMessage();
	private static final char split = 0x0B;
	
	/**
	 * Method getInstance.
	 * @param type MessageType
	 * @return SystemMessage
	 */
	public static SystemMessage getInstance(MessageType type)
	{
		final SystemMessage packet = (SystemMessage) instance.newInstance();
		packet.builder = new StringBuilder(type.getName());
		return packet;
	}
	
	/**
	 * Method getInstance.
	 * @param message String
	 * @return SystemMessage
	 */
	public static SystemMessage getInstance(String message)
	{
		final SystemMessage packet = (SystemMessage) instance.newInstance();
		packet.builder = new StringBuilder(message);
		return packet;
	}
	
	private StringBuilder builder;
	
	/**
	 * Method add.
	 * @param var String
	 * @param val String
	 * @return SystemMessage
	 */
	public SystemMessage add(String var, String val)
	{
		builder.append(split);
		builder.append(var);
		builder.append(split);
		builder.append(val);
		return this;
	}
	
	/**
	 * Method addAttacker.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addAttacker(String name)
	{
		builder.append(split);
		builder.append("attacker");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addItem.
	 * @param id int
	 * @param count int
	 * @return SystemMessage
	 */
	public SystemMessage addItem(int id, int count)
	{
		builder.append(split);
		builder.append("ItemName");
		builder.append(split);
		builder.append("@Item:").append(id);
		builder.append(split);
		builder.append("ItemAmount");
		builder.append(split);
		builder.append(count);
		return this;
	}
	
	/**
	 * Method addItemName.
	 * @param id int
	 * @return SystemMessage
	 */
	public SystemMessage addItemName(int id)
	{
		builder.append(split);
		builder.append("ItemName");
		builder.append(split);
		builder.append("@Item:").append(id);
		return this;
	}
	
	/**
	 * Method addLoser.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addLoser(String name)
	{
		builder.append(split);
		builder.append("loser");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addMoney.
	 * @param name String
	 * @param count int
	 * @return SystemMessage
	 */
	public SystemMessage addMoney(String name, int count)
	{
		builder.append(split);
		builder.append("UserName");
		builder.append(split);
		builder.append(name);
		builder.append(split);
		builder.append("Money");
		builder.append(split);
		builder.append(count);
		return this;
	}
	
	/**
	 * Method addOpponent.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addOpponent(String name)
	{
		builder.append(split);
		builder.append("Opponent");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addPaidMoney.
	 * @param count int
	 * @return SystemMessage
	 */
	public SystemMessage addPaidMoney(int count)
	{
		builder.append(split);
		builder.append("amount");
		builder.append(split);
		builder.append(count);
		return this;
	}
	
	/**
	 * Method addPlayer.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addPlayer(String name)
	{
		builder.append(split);
		builder.append("player");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addProf.
	 * @param count int
	 * @return SystemMessage
	 */
	public SystemMessage addProf(int count)
	{
		builder.append(split);
		builder.append("prof");
		builder.append(split);
		builder.append(count);
		return this;
	}
	
	/**
	 * Method addQuestName.
	 * @param id int
	 * @return SystemMessage
	 */
	public SystemMessage addQuestName(int id)
	{
		builder.append(split);
		builder.append("QuestName");
		builder.append(split);
		builder.append("@quest:");
		builder.append(id).append("001");
		return this;
	}
	
	/**
	 * Method addQuestName.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addQuestName(String name)
	{
		builder.append(split);
		builder.append("QuestName");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addRequestor.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addRequestor(String name)
	{
		builder.append(split);
		builder.append("requestor");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addSkillName.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addSkillName(String name)
	{
		builder.append(split);
		builder.append("SkillName");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addTarget.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addTarget(String name)
	{
		builder.append(split);
		builder.append("target");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addUserName.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addUserName(String name)
	{
		builder.append(split);
		builder.append("UserName");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method addWinner.
	 * @param name String
	 * @return SystemMessage
	 */
	public SystemMessage addWinner(String name)
	{
		builder.append(split);
		builder.append("winner");
		builder.append(split);
		builder.append(name);
		return this;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.SYSTEM_MESSAGE;
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
		writeShort(buffer, 6);
		writeStringBuilder(buffer, builder);
	}
}
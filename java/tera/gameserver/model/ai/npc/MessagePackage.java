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
package tera.gameserver.model.ai.npc;

import rlib.util.Rnd;

/**
 * @author Ronn
 */
public final class MessagePackage
{
	private final String name;
	private final String[] messages;
	private final int limit;
	
	/**
	 * Constructor for MessagePackage.
	 * @param name String
	 * @param messages String[]
	 */
	public MessagePackage(String name, String[] messages)
	{
		this.name = name;
		this.messages = messages;
		limit = messages.length - 1;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getRandomMessage.
	 * @return String
	 */
	public final String getRandomMessage()
	{
		return messages[Rnd.nextInt(0, limit)];
	}
}
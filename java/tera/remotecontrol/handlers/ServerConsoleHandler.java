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
package tera.remotecontrol.handlers;

import java.util.ArrayList;

import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;
import tera.remotecontrol.PacketType;

import rlib.logging.LoggerListener;

/**
 * @author Ronn
 */
public class ServerConsoleHandler implements PacketHandler, LoggerListener
{
	public static final ServerConsoleHandler instance = new ServerConsoleHandler();
	
	private final ArrayList<String> messages;
	
	private ServerConsoleHandler()
	{
		messages = new ArrayList<>();
	}
	
	/**
	 * Method println.
	 * @param text String
	 * @see rlib.logging.LoggerListener#println(String)
	 */
	@Override
	public void println(String text)
	{
		if (messages.size() > 1000)
		{
			messages.clear();
		}
		
		messages.add(text);
	}
	
	/**
	 * Method processing.
	 * @param packet Packet
	 * @return Packet
	 * @see tera.remotecontrol.PacketHandler#processing(Packet)
	 */
	@Override
	public Packet processing(Packet packet)
	{
		final ArrayList<String> buffer = new ArrayList<>();
		buffer.addAll(messages);
		messages.clear();
		return new Packet(PacketType.RESPONSE, buffer);
	}
}

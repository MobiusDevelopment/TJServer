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
package tera.gameserver.network;

import java.io.File;

import tera.Config;
import tera.gameserver.document.DocumentSnifferOpcode;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public abstract class Opcodes
{
	public static void prepare()
	{
		if (Config.SERVER_USE_SNIFFER_OPCODE)
		{
			final VarTable opcodes = new DocumentSnifferOpcode(new File("./config/Opcode.xml")).parse();
			
			for (ClientPacketType packet : ClientPacketType.values())
			{
				packet.setOpcode(opcodes.getInteger("CLIENT_PACKET_" + packet.name(), packet.getOpcode()));
			}
			
			for (ServerPacketType packet : ServerPacketType.values())
			{
				packet.setOpcode(opcodes.getInteger("SERVER_PACKET_" + packet.name(), packet.getOpcode()));
			}
		}
		
		ClientPacketType.init();
		ServerPacketType.init();
	}
}
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

import tera.Config;
import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;
import tera.remotecontrol.PacketType;
import tera.remotecontrol.ServerControl;

/**
 * @author Ronn
 * @created 26.03.2012
 */
public class AuthHandler implements PacketHandler
{
	/**
	 * Method processing.
	 * @param packet Packet
	 * @return Packet
	 * @see tera.remotecontrol.PacketHandler#processing(Packet)
	 */
	@Override
	public Packet processing(Packet packet)
	{
		final String login = packet.nextString();
		final String password = packet.nextString();
		
		if (Config.DIST_CONTROL_LOGIN.equals(login) && Config.DIST_CONTROL_PASSWORD.equals(password))
		{
			ServerControl.authed = true;
			return new Packet(PacketType.REQUEST_AUTH, true);
		}
		
		return new Packet(PacketType.REQUEST_AUTH, false);
	}
}
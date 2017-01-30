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

import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class AuthAttempt extends ServerPacket
{
	public static final int SUCCESSFUL = 1;
	public static final int INCORRECT = 2;
	private static final ServerPacket instance = new AuthAttempt();
	
	/**
	 * Method getInstance.
	 * @param result int
	 * @return AuthAttempt
	 */
	public static AuthAttempt getInstance(int result)
	{
		final AuthAttempt packet = (AuthAttempt) instance.newInstance();
		packet.result = result;
		return packet;
	}
	
	private int result;
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.AUTH_ATTEMPT;
	}
	
	@Override
	protected void writeImpl()
	{
		switch (result)
		{
			case SUCCESSFUL:
			{
				writeOpcode();
				writeByte(1);
				
				owner.sendPacket(AuthSuccessful.getInstance(), true);
				owner.sendPacket(AuthSuccessful2.getInstance(), true);
				
				break;
			}
			
			case INCORRECT:
				owner.sendPacket(AuthFailed.getInstance(), true);
		}
	}
}
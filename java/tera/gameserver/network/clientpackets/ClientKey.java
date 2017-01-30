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
package tera.gameserver.network.clientpackets;

import tera.gameserver.network.serverpackets.ServerKey;

/**
 * @author Ronn
 */
public class ClientKey extends ClientPacket
{
	private final byte[] data;
	
	public ClientKey()
	{
		data = new byte[128];
	}
	
	@Override
	public void readImpl()
	{
		readBytes(data);
	}
	
	@Override
	@SuppressWarnings("incomplete-switch")
	public void runImpl()
	{
		switch (owner.getCryptorState())
		{
			case WAIT_FIRST_SERVER_KEY:
			case WAIT_SECOND_SERCER_KEY:
			{
				owner.sendPacket(ServerKey.getInstance(), true);
			}
		}
	}
}
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

import tera.gameserver.network.model.UserClient;
import tera.gameserver.network.serverpackets.CharacterQuitCancelOk;

/**
 * @author Ronn
 */
public class CharacterQuitCancel extends ClientPacket
{
	private UserClient client;
	
	@Override
	public void readImpl()
	{
		client = getOwner();
	}
	
	@Override
	public void runImpl()
	{
		if (client == null)
		{
			log.warning(this, new Exception("not found client"));
			return;
		}
		
		client.sendPacket(CharacterQuitCancelOk.getInstance(), true);
	}
}
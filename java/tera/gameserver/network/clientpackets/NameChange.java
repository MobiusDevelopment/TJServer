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

import tera.Config;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.network.model.UserClient;
import tera.gameserver.network.serverpackets.PlayerNameResult;

/**
 * @author Ronn
 */
public class NameChange extends ClientPacket
{
	
	private String name;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
	}
	
	@Override
	public void readImpl()
	{
		readShort();
		name = readString();
	}
	
	@Override
	public void runImpl()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final UserClient owner = getOwner();
		
		if (dbManager.getAccountSize(owner.getAccount().getName()) >= 8)
		{
			owner.sendPacket(PlayerNameResult.getInstance(PlayerNameResult.FAILED), true);
		}
		else if (!Config.checkName(name))
		{
			owner.sendPacket(PlayerNameResult.getInstance(PlayerNameResult.FAILED), true);
		}
		else if (!dbManager.isFreeName(name))
		{
			owner.sendPacket(PlayerNameResult.getInstance(PlayerNameResult.FAILED), true);
		}
		else
		{
			owner.sendPacket(PlayerNameResult.getInstance(PlayerNameResult.SUCCESSFUL), true);
		}
	}
}
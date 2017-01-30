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

import tera.gameserver.manager.AccountManager;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.model.Account;
import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;
import tera.remotecontrol.PacketType;

/**
 * @author Ronn
 * @created 25.04.2012
 */
public class UpdateAccountHandler implements PacketHandler
{
	public static final UpdateAccountHandler instance = new UpdateAccountHandler();
	
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
		final AccountManager accountManager = AccountManager.getInstance();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		Account account = accountManager.getAccount(login.toLowerCase());
		boolean inDB = false;
		
		if (account == null)
		{
			account = dbManager.restoreAccount(login);
			inDB = true;
		}
		
		if (account == null)
		{
			return new Packet(PacketType.RESPONSE, false);
		}
		
		final Packet response = new Packet(PacketType.RESPONSE, true, account.getEmail(), account.getLastIP(), account.getAllowIPs(), account.getComments(), account.getEndBlock(), Math.max(System.currentTimeMillis(), account.getEndPay()), account.getAccessLevel());
		
		if (inDB)
		{
			accountManager.removeAccount(account);
		}
		
		return response;
	}
}

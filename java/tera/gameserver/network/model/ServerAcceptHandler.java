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
package tera.gameserver.network.model;

import java.nio.channels.AsynchronousSocketChannel;

import tera.gameserver.network.Network;

import rlib.logging.Loggers;
import rlib.network.server.AcceptHandler;

/**
 * @author Ronn
 */
public final class ServerAcceptHandler extends AcceptHandler
{
	private static ServerAcceptHandler instance;
	
	/**
	 * Method getInstance.
	 * @return ServerAcceptHandler
	 */
	public static ServerAcceptHandler getInstance()
	{
		if (instance == null)
		{
			instance = new ServerAcceptHandler();
		}
		
		return instance;
	}
	
	/**
	 * Method onAccept.
	 * @param channel AsynchronousSocketChannel
	 */
	@Override
	protected void onAccept(AsynchronousSocketChannel channel)
	{
		final Network network = Network.getInstance();
		final UserAsynConnection connect = new UserAsynConnection(network.getNetwork(), channel);
		final UserClient client = new UserClient(connect);
		connect.setClient(client);
		client.successfulConnection();
		connect.startRead();
	}
	
	/**
	 * Method onFailed.
	 * @param exc Throwable
	 */
	@Override
	protected void onFailed(Throwable exc)
	{
		Loggers.warning(this, new Exception(exc));
	}
}
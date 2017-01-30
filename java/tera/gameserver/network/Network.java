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

import java.net.InetSocketAddress;

import tera.Config;
import tera.gameserver.network.model.ServerAcceptHandler;
import tera.gameserver.network.model.ServerNetworkConfig;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.network.NetworkFactory;
import rlib.network.server.ServerNetwork;

/**
 * @author Ronn
 * @created 24.03.2012
 */
public final class Network
{
	private static final Logger log = Loggers.getLogger(Network.class);
	private static Network instance;
	
	/**
	 * Method getInstance.
	 * @return Network
	 */
	public static Network getInstance()
	{
		if (instance == null)
		{
			instance = new Network();
		}
		
		return instance;
	}
	
	private ServerNetwork network;
	
	private Network()
	{
		try
		{
			network = NetworkFactory.newDefaultAsynchronousServerNetwork(ServerNetworkConfig.getInstance(), ServerAcceptHandler.getInstance());
			network.bind(new InetSocketAddress(Config.SERVER_PORT));
			log.info("started.");
		}
		catch (Exception e)
		{
			log.warning(e);
		}
	}
	
	/**
	 * Method getNetwork.
	 * @return ServerNetwork
	 */
	public ServerNetwork getNetwork()
	{
		return network;
	}
}
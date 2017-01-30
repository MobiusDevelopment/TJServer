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
package rlib.network;

import java.io.IOException;

import rlib.logging.Loggers;
import rlib.network.client.ClientNetwork;
import rlib.network.client.ConnectHandler;
import rlib.network.client.DefaultClientNetwork;
import rlib.network.server.AcceptHandler;
import rlib.network.server.DefaultServerNetwork;
import rlib.network.server.ServerNetwork;

public final class NetworkFactory
{
	public static ClientNetwork newDefaultAsynchronousClientNetwork(NetworkConfig config, ConnectHandler connectHandler)
	{
		try
		{
			return new DefaultClientNetwork(config, connectHandler);
		}
		catch (IOException e)
		{
			Loggers.warning(NetworkFactory.class, e);
			return null;
		}
	}
	
	public static ServerNetwork newDefaultAsynchronousServerNetwork(NetworkConfig config, AcceptHandler acceptHandler)
	{
		try
		{
			return new DefaultServerNetwork(config, acceptHandler);
		}
		catch (IOException e)
		{
			Loggers.warning(NetworkFactory.class, e);
			return null;
		}
	}
	
	private NetworkFactory() throws Exception
	{
		throw new Exception("\u041a\u0423\u0414\u0410 \u0422\u042b \u041b\u0415\u0417\u0415\u0428\u042c");
	}
}

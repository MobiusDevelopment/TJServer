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
package rlib.network.client.server;

import java.nio.channels.AsynchronousSocketChannel;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.network.AbstractAsynConnection;
import rlib.network.client.ClientNetwork;
import rlib.network.packets.ReadeablePacket;
import rlib.network.packets.SendablePacket;

public abstract class AbstractServerConnection<T extends Server, R extends ReadeablePacket<T>, S extends SendablePacket<T>> extends AbstractAsynConnection<ClientNetwork, R, S> implements ServerConnection<T, R, S>
{
	protected static final Logger log = Loggers.getLogger(ServerConnection.class);
	protected T server;
	
	public AbstractServerConnection(ClientNetwork network, AsynchronousSocketChannel channel, Class<S> sendableType)
	{
		super(network, channel, sendableType);
	}
	
	@Override
	protected void finish()
	{
		T server = this.getServer();
		if (server != null)
		{
			server.close();
		}
		else if (!isClosed())
		{
			close();
		}
	}
	
	@Override
	public T getServer()
	{
		return this.server;
	}
	
	@Override
	protected void onWrited(S packet)
	{
	}
	
	@Override
	public void setServer(T server)
	{
		this.server = server;
	}
}

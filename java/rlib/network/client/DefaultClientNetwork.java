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
package rlib.network.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;

import rlib.concurrent.GroupThreadFactory;
import rlib.network.AbstractAsynchronousNetwork;
import rlib.network.NetworkConfig;

public final class DefaultClientNetwork extends AbstractAsynchronousNetwork implements ClientNetwork
{
	private final AsynchronousChannelGroup channelGroup;
	private final ConnectHandler connectHandler;
	private AsynchronousSocketChannel clientChannel;
	
	public DefaultClientNetwork(NetworkConfig config, ConnectHandler connectHandler) throws IOException
	{
		super(config);
		channelGroup = AsynchronousChannelGroup.withFixedThreadPool(config.getGroupSize(), new GroupThreadFactory(config.getGroupName(), config.getThreadClass(), config.getThreadPriority()));
		this.connectHandler = connectHandler;
	}
	
	@Override
	public void connect(InetSocketAddress serverAddress)
	{
		try
		{
			if (clientChannel != null)
			{
				clientChannel.close();
			}
			clientChannel = AsynchronousSocketChannel.open(channelGroup);
		}
		catch (IOException e)
		{
			log.warning(this, e);
		}
		clientChannel.connect(serverAddress, clientChannel, connectHandler);
	}
}

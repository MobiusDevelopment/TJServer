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
package rlib.network.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import rlib.concurrent.GroupThreadFactory;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.network.NetworkConfig;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

public final class DefaultServerNetwork implements ServerNetwork
{
	protected static final Logger log = Loggers.getLogger(ServerNetwork.class);
	private final Array<ByteBuffer> readBufferPool;
	private final Array<ByteBuffer> writeBufferPool;
	private final AsynchronousChannelGroup channelGroup;
	private final AsynchronousServerSocketChannel serverChannel;
	private final NetworkConfig config;
	private final AcceptHandler acceptHandler;
	
	public DefaultServerNetwork(NetworkConfig config, AcceptHandler acceptHandler) throws IOException
	{
		this.config = config;
		this.readBufferPool = Arrays.toConcurrentArray(ByteBuffer.class);
		this.writeBufferPool = Arrays.toConcurrentArray(ByteBuffer.class);
		this.channelGroup = AsynchronousChannelGroup.withFixedThreadPool(config.getGroupSize(), new GroupThreadFactory(config.getGroupName(), config.getThreadClass(), config.getThreadPriority()));
		this.serverChannel = AsynchronousServerSocketChannel.open(this.channelGroup);
		this.acceptHandler = acceptHandler;
	}
	
	@Override
	public <A> void accept(A attachment, CompletionHandler<AsynchronousSocketChannel, ? super A> handler)
	{
		this.serverChannel.accept(attachment, handler);
	}
	
	@Override
	public void bind(SocketAddress address) throws IOException
	{
		this.serverChannel.bind(address);
		this.serverChannel.accept(this.serverChannel, this.acceptHandler);
	}
	
	@Override
	public NetworkConfig getConfig()
	{
		return this.config;
	}
	
	@Override
	public ByteBuffer getReadByteBuffer()
	{
		this.readBufferPool.writeLock();
		try
		{
			ByteBuffer buffer = this.readBufferPool.pop();
			if (buffer == null)
			{
				buffer = ByteBuffer.allocate(this.config.getReadBufferSize()).order(ByteOrder.LITTLE_ENDIAN);
			}
			ByteBuffer byteBuffer = buffer;
			return byteBuffer;
		}
		finally
		{
			this.readBufferPool.writeUnlock();
		}
	}
	
	@Override
	public ByteBuffer getWriteByteBuffer()
	{
		this.writeBufferPool.writeLock();
		try
		{
			ByteBuffer buffer = this.writeBufferPool.pop();
			if (buffer == null)
			{
				buffer = ByteBuffer.allocate(this.config.getWriteBufferSize()).order(ByteOrder.LITTLE_ENDIAN);
			}
			ByteBuffer byteBuffer = buffer;
			return byteBuffer;
		}
		finally
		{
			this.writeBufferPool.writeUnlock();
		}
	}
	
	@Override
	public void putReadByteBuffer(ByteBuffer buffer)
	{
		if (buffer == null)
		{
			return;
		}
		this.readBufferPool.add(buffer);
	}
	
	@Override
	public void putWriteByteBuffer(ByteBuffer buffer)
	{
		if (buffer == null)
		{
			return;
		}
		this.writeBufferPool.add(buffer);
	}
}

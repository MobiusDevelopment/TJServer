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
package rlib.network.server.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.locks.Lock;

import rlib.concurrent.Locks;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.network.NetworkConfig;
import rlib.network.packets.ReadeablePacket;
import rlib.network.packets.SendablePacket;
import rlib.network.server.ServerNetwork;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

public abstract class AbstractClientConnection<T extends Client, R extends ReadeablePacket<T>, S extends SendablePacket<T>> implements ClientConnection<T, R, S>
{
	protected static final Logger log = Loggers.getLogger(ClientConnection.class);
	protected final ServerNetwork network;
	protected final Array<S> waitPackets;
	protected final AsynchronousSocketChannel channel;
	protected final ByteBuffer readBuffer;
	protected final ByteBuffer writeBuffer;
	protected final NetworkConfig config;
	protected final Lock lock;
	protected volatile int write;
	protected volatile boolean closed;
	protected T client;
	protected long lastActive;
	private final CompletionHandler<Integer, AbstractClientConnection> readHandler;
	private final CompletionHandler<Integer, S> writeHandler;
	
	public AbstractClientConnection(ServerNetwork network, AsynchronousSocketChannel channel, Class<S> sendableType)
	{
		this.readHandler = new CompletionHandler<Integer, AbstractClientConnection>()
		{
			
			@Override
			public void completed(Integer result, AbstractClientConnection attachment)
			{
				if (result == -1)
				{
					AbstractClientConnection.this.client.close();
					return;
				}
				AbstractClientConnection.this.setLastActive(System.currentTimeMillis());
				AbstractClientConnection.this.readBuffer.flip();
				if (AbstractClientConnection.this.isReady(AbstractClientConnection.this.readBuffer))
				{
					AbstractClientConnection.this.readPacket(AbstractClientConnection.this.readBuffer);
				}
				AbstractClientConnection.this.readBuffer.clear();
				AbstractClientConnection.this.channel.read(AbstractClientConnection.this.readBuffer, attachment, this);
			}
			
			@Override
			public void failed(Throwable exc, AbstractClientConnection attachment)
			{
				if (AbstractClientConnection.this.config.isVesibleReadException())
				{
					AbstractClientConnection.log.warning(this, new Exception(exc));
				}
				if (AbstractClientConnection.this.isClosed())
				{
					return;
				}
				T client = AbstractClientConnection.this.getClient();
				if (client != null)
				{
					client.close();
				}
				else if (!AbstractClientConnection.this.isClosed())
				{
					AbstractClientConnection.this.close();
				}
			}
		};
		this.writeHandler = new CompletionHandler<Integer, S>()
		{
			
			@Override
			public void completed(Integer result, S packet)
			{
				if (result == -1)
				{
					AbstractClientConnection.this.client.close();
					return;
				}
				if (AbstractClientConnection.this.writeBuffer.remaining() > 0)
				{
					AbstractClientConnection.this.channel.write(AbstractClientConnection.this.writeBuffer, packet, this);
					return;
				}
				AbstractClientConnection.this.setLastActive(System.currentTimeMillis());
				--AbstractClientConnection.this.write;
				AbstractClientConnection.this.writeNextPacket();
			}
			
			@Override
			public void failed(Throwable exc, S packet)
			{
				if (AbstractClientConnection.this.config.isVesibleWriteException())
				{
					AbstractClientConnection.log.warning(this, new Exception("incorrect write packet " + packet.getName(), exc));
				}
				if (AbstractClientConnection.this.isClosed())
				{
					return;
				}
				--AbstractClientConnection.this.write;
				AbstractClientConnection.this.writeNextPacket();
			}
		};
		this.lock = Locks.newLock();
		this.channel = channel;
		this.waitPackets = Arrays.toArray(sendableType);
		this.network = network;
		this.readBuffer = network.getReadByteBuffer();
		this.writeBuffer = network.getWriteByteBuffer();
		this.config = network.getConfig();
	}
	
	@Override
	public void close()
	{
		this.lock.lock();
		try
		{
			if (this.isClosed())
			{
				return;
			}
			try
			{
				if (this.channel.isOpen())
				{
					this.channel.close();
				}
				this.setClosed(true);
				this.network.putReadByteBuffer(this.readBuffer);
				this.network.putWriteByteBuffer(this.writeBuffer);
				this.waitPackets.clear();
			}
			catch (IOException e)
			{
				log.warning(this, e);
			}
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	@Override
	public final T getClient()
	{
		return this.client;
	}
	
	@Override
	public final long getLastActive()
	{
		return this.lastActive;
	}
	
	protected final ByteBuffer getReadBuffer()
	{
		return this.readBuffer;
	}
	
	protected final ByteBuffer getWriteBuffer()
	{
		return this.writeBuffer;
	}
	
	@Override
	public final boolean isClosed()
	{
		return this.closed;
	}
	
	protected boolean isReady(ByteBuffer buffer)
	{
		return true;
	}
	
	protected abstract void movePacketToBuffer(S var1, ByteBuffer var2);
	
	protected abstract void readPacket(ByteBuffer var1);
	
	@Override
	public final void sendPacket(S packet)
	{
		this.lock.lock();
		try
		{
			this.waitPackets.add(packet);
		}
		finally
		{
			this.lock.unlock();
		}
		this.writeNextPacket();
	}
	
	@Override
	public final void setClient(T client)
	{
		this.client = client;
	}
	
	protected final void setClosed(boolean closed)
	{
		this.closed = closed;
	}
	
	@Override
	public final void setLastActive(long lastActive)
	{
		this.lastActive = lastActive;
	}
	
	@Override
	public final void startRead()
	{
		this.readBuffer.clear();
		this.channel.read(this.readBuffer, this, this.readHandler);
	}
	
	protected final void writeNextPacket()
	{
		this.lock.lock();
		try
		{
			if (this.isClosed() || (this.write > 0))
			{
				return;
			}
			S waitPacket = this.waitPackets.poll();
			if (waitPacket == null)
			{
				return;
			}
			++this.write;
			this.movePacketToBuffer(waitPacket, this.writeBuffer);
			this.channel.write(this.writeBuffer, waitPacket, this.writeHandler);
			waitPacket.complete();
		}
		finally
		{
			this.lock.unlock();
		}
	}
}

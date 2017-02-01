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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

public abstract class AbstractAsynchronousNetwork implements AsynchronousNetwork
{
	protected static final Logger log = Loggers.getLogger(AsynchronousNetwork.class);
	protected final Array<ByteBuffer> readBufferPool;
	protected final Array<ByteBuffer> writeBufferPool;
	protected final NetworkConfig config;
	
	protected AbstractAsynchronousNetwork(NetworkConfig config)
	{
		this.config = config;
		readBufferPool = Arrays.toConcurrentArray(ByteBuffer.class);
		writeBufferPool = Arrays.toConcurrentArray(ByteBuffer.class);
	}
	
	@Override
	public NetworkConfig getConfig()
	{
		return config;
	}
	
	@Override
	public ByteBuffer getReadByteBuffer()
	{
		readBufferPool.writeLock();
		try
		{
			ByteBuffer buffer = readBufferPool.pop();
			if (buffer == null)
			{
				buffer = ByteBuffer.allocate(config.getReadBufferSize()).order(ByteOrder.LITTLE_ENDIAN);
			}
			ByteBuffer byteBuffer = buffer;
			return byteBuffer;
		}
		finally
		{
			readBufferPool.writeUnlock();
		}
	}
	
	@Override
	public ByteBuffer getWriteByteBuffer()
	{
		writeBufferPool.writeLock();
		try
		{
			ByteBuffer buffer = writeBufferPool.pop();
			if (buffer == null)
			{
				buffer = ByteBuffer.allocate(config.getWriteBufferSize()).order(ByteOrder.LITTLE_ENDIAN);
			}
			ByteBuffer byteBuffer = buffer;
			return byteBuffer;
		}
		finally
		{
			writeBufferPool.writeUnlock();
		}
	}
	
	@Override
	public void putReadByteBuffer(ByteBuffer buffer)
	{
		if (buffer == null)
		{
			return;
		}
		readBufferPool.add(buffer);
	}
	
	@Override
	public void putWriteByteBuffer(ByteBuffer buffer)
	{
		if (buffer == null)
		{
			return;
		}
		writeBufferPool.add(buffer);
	}
}

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
package rlib.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;

import rlib.concurrent.Locks;
import rlib.util.Synchronized;

public class ByteGameLogger implements GameLogger, Synchronized
{
	private static final Logger log = Loggers.getLogger(ByteGameLogger.class);
	private final Lock lock;
	private final ByteBuffer cache;
	private final FileOutputStream out;
	private final FileChannel channel;
	
	protected ByteGameLogger(File outFile) throws IOException
	{
		if (!outFile.exists())
		{
			outFile.createNewFile();
		}
		this.out = new FileOutputStream(outFile);
		this.channel = this.out.getChannel();
		this.lock = Locks.newLock();
		this.cache = ByteBuffer.allocate(1048576).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	@Override
	public void finish()
	{
		this.lock.lock();
		try
		{
			this.writeCache();
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	@Override
	public void lock()
	{
		this.lock.lock();
	}
	
	@Override
	public void unlock()
	{
		this.lock.unlock();
	}
	
	@Override
	public void write(String text)
	{
		this.lock.lock();
		try
		{
			if (this.cache.remaining() < (text.length() * 2))
			{
				this.writeCache();
			}
			int i = 0;
			int length = text.length();
			while (i < length)
			{
				this.cache.putChar(text.charAt(i));
				++i;
			}
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	public void writeByte(int value)
	{
		if (this.cache.remaining() < 1)
		{
			this.writeCache();
		}
		this.cache.put((byte) value);
	}
	
	@Override
	public void writeCache()
	{
		try
		{
			this.cache.flip();
			this.channel.write(this.cache);
			this.cache.clear();
			this.out.flush();
		}
		catch (IOException e)
		{
			log.warning(e);
		}
	}
	
	public void writeFloat(float value)
	{
		if (this.cache.remaining() < 4)
		{
			this.writeCache();
		}
		this.cache.putFloat(value);
	}
	
	public void writeInt(int value)
	{
		if (this.cache.remaining() < 4)
		{
			this.writeCache();
		}
		this.cache.putInt(value);
	}
}

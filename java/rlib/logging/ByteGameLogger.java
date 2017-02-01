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
		out = new FileOutputStream(outFile);
		channel = out.getChannel();
		lock = Locks.newLock();
		cache = ByteBuffer.allocate(1048576).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	@Override
	public void finish()
	{
		lock.lock();
		try
		{
			writeCache();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	@Override
	public void lock()
	{
		lock.lock();
	}
	
	@Override
	public void unlock()
	{
		lock.unlock();
	}
	
	@Override
	public void write(String text)
	{
		lock.lock();
		try
		{
			if (cache.remaining() < (text.length() * 2))
			{
				writeCache();
			}
			int i = 0;
			int length = text.length();
			while (i < length)
			{
				cache.putChar(text.charAt(i));
				++i;
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public void writeByte(int value)
	{
		if (cache.remaining() < 1)
		{
			writeCache();
		}
		cache.put((byte) value);
	}
	
	@Override
	public void writeCache()
	{
		try
		{
			cache.flip();
			channel.write(cache);
			cache.clear();
			out.flush();
		}
		catch (IOException e)
		{
			log.warning(e);
		}
	}
	
	public void writeFloat(float value)
	{
		if (cache.remaining() < 4)
		{
			writeCache();
		}
		cache.putFloat(value);
	}
	
	public void writeInt(int value)
	{
		if (cache.remaining() < 4)
		{
			writeCache();
		}
		cache.putInt(value);
	}
}

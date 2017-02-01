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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;

import rlib.concurrent.Locks;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

public class StringGameLogger implements GameLogger
{
	private static final Logger log = Loggers.getLogger(StringGameLogger.class);
	private final Lock lock;
	private final Array<String> cache;
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final Date date = new Date();
	private final Writer out;
	
	protected StringGameLogger(File outFile) throws IOException
	{
		if (!outFile.exists())
		{
			outFile.createNewFile();
		}
		out = new FileWriter(outFile);
		lock = Locks.newLock();
		cache = Arrays.toArray(String.class);
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
	public void write(String text)
	{
		lock.lock();
		try
		{
			if (cache.size() > 1000)
			{
				writeCache();
			}
			date.setTime(System.currentTimeMillis());
			cache.add(String.valueOf(timeFormat.format(date)) + ": " + text + "\n");
		}
		finally
		{
			lock.unlock();
		}
	}
	
	@Override
	public void writeCache()
	{
		try
		{
			String[] array = cache.array();
			int i = 0;
			int length = cache.size();
			while (i < length)
			{
				out.write(array[i]);
				++i;
			}
			cache.clear();
			out.flush();
		}
		catch (IOException e)
		{
			log.warning(e);
		}
	}
}

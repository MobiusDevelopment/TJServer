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
		this.out = new FileWriter(outFile);
		this.lock = Locks.newLock();
		this.cache = Arrays.toArray(String.class);
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
	public void write(String text)
	{
		this.lock.lock();
		try
		{
			if (this.cache.size() > 1000)
			{
				this.writeCache();
			}
			this.date.setTime(System.currentTimeMillis());
			this.cache.add(String.valueOf(this.timeFormat.format(this.date)) + ": " + text + "\n");
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	@Override
	public void writeCache()
	{
		try
		{
			String[] array = this.cache.array();
			int i = 0;
			int length = this.cache.size();
			while (i < length)
			{
				this.out.write(array[i]);
				++i;
			}
			this.cache.clear();
			this.out.flush();
		}
		catch (IOException e)
		{
			log.warning(e);
		}
	}
}

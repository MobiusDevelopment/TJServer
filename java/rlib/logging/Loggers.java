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
import rlib.util.array.Arrays;
import rlib.util.table.Table;
import rlib.util.table.Tables;

public abstract class Loggers
{
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private static final Table<String, Logger> loggers = Tables.newObjectTable();
	private static final Lock lock = Locks.newLock();
	private static final Logger log = new Logger();
	private static LoggerListener[] listeners;
	private static Writer out;
	
	public static void addListener(LoggerListener listener)
	{
		listeners = Arrays.addToArray(listeners, listener, LoggerListener.class);
	}
	
	public static final Logger getLogger(Class<?> cs)
	{
		Logger logger = new Logger(cs.getSimpleName());
		loggers.put(cs.getSimpleName(), logger);
		return logger;
	}
	
	public static final Logger getLogger(String name)
	{
		Logger logger = new Logger(name);
		loggers.put(name, logger);
		return logger;
	}
	
	public static final void info(Class<?> cs, String message)
	{
		log.info(cs, message);
	}
	
	public static final void info(Object owner, String message)
	{
		log.info(owner, message);
	}
	
	public static final void info(String name, String message)
	{
		log.info(name, message);
	}
	
	public static final synchronized void println(String text)
	{
		block8:
		{
			if (listeners != null)
			{
				int i = 0;
				int length = listeners.length;
				while (i < length)
				{
					listeners[i].println(text);
					++i;
				}
			}
			if (out != null)
			{
				lock.lock();
				try
				{
					try
					{
						out.write(String.valueOf(text) + "\n");
						out.flush();
					}
					catch (IOException e)
					{
						e.printStackTrace();
						lock.unlock();
						break block8;
					}
				}
				catch (Throwable length)
				{
					lock.unlock();
					throw length;
				}
				lock.unlock();
			}
		}
		System.err.println(text);
	}
	
	public static final void setFile(String projectPath, boolean val)
	{
		if (!val)
		{
			return;
		}
		File directory = new File(String.valueOf(projectPath) + "/log/");
		if (!directory.exists())
		{
			directory.mkdir();
		}
		try
		{
			File file = new File(String.valueOf(directory.getAbsolutePath()) + "/" + timeFormat.format(new Date()) + ".log");
			if (!file.exists())
			{
				file.createNewFile();
			}
			out = new FileWriter(new File(String.valueOf(projectPath) + "/log/" + timeFormat.format(new Date()) + ".log"), true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			out = null;
		}
	}
	
	public static void warning(Class<?> cs, Exception exception)
	{
		log.warning(cs, exception);
	}
	
	public static final void warning(Class<?> cs, String message)
	{
		log.warning(cs, message);
	}
	
	public static void warning(Object owner, Exception exception)
	{
		log.warning(owner, exception);
	}
	
	public static final void warning(Object owner, String message)
	{
		log.warning(owner, message);
	}
	
	public static void warning(String name, Exception exception)
	{
		log.warning(name, exception);
	}
	
	public static final void warning(String name, String message)
	{
		log.warning(name, message);
	}
}

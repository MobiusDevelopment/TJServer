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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

public abstract class GameLoggers
{
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	private static String directory;
	private static Array<GameLogger> loggers;
	
	public static void finish()
	{
		for (GameLogger logger : loggers)
		{
			logger.finish();
		}
	}
	
	public static ByteGameLogger getByteLogger(String name)
	{
		File dir = new File(String.valueOf(directory) + "/" + name);
		if (!dir.exists())
		{
			dir.mkdir();
		}
		if (!dir.isDirectory())
		{
			throw new IllegalArgumentException("incorrect directory for game logger " + name);
		}
		File outFile = new File(String.valueOf(dir.getAbsolutePath()) + "/" + timeFormat.format(new Date()) + ".gamelog");
		try
		{
			ByteGameLogger logger = new ByteGameLogger(outFile);
			loggers.add(logger);
			return logger;
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException("incorrect create log file for game logger " + name);
		}
	}
	
	public static StringGameLogger getLogger(String name)
	{
		File dir = new File(String.valueOf(directory) + "/" + name);
		if (!dir.exists())
		{
			dir.mkdir();
		}
		if (!dir.isDirectory())
		{
			throw new IllegalArgumentException("incorrect directory for game logger " + name);
		}
		File outFile = new File(String.valueOf(dir.getAbsolutePath()) + "/" + timeFormat.format(new Date()) + ".gamelog");
		try
		{
			StringGameLogger logger = new StringGameLogger(outFile);
			loggers.add(logger);
			return logger;
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException("incorrect create log file for game logger " + name);
		}
	}
	
	public static void setDirectory(String directory)
	{
		GameLoggers.directory = directory;
		loggers = Arrays.toConcurrentArray(GameLogger.class);
	}
}

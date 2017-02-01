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

import rlib.util.Util;

public final class Logger
{
	private String name;
	
	public Logger()
	{
	}
	
	public Logger(String name)
	{
		this.name = name;
	}
	
	public void info(Class<?> cs, String message)
	{
		Loggers.println("INFO " + Util.formatTime(System.currentTimeMillis()) + " " + cs.getSimpleName() + ": " + message);
	}
	
	public void info(Object owner, String message)
	{
		Loggers.println("INFO " + Util.formatTime(System.currentTimeMillis()) + " " + owner.getClass().getSimpleName() + ": " + message);
	}
	
	public void info(String message)
	{
		Loggers.println("INFO " + Util.formatTime(System.currentTimeMillis()) + " " + name + ": " + message);
	}
	
	public void info(String name, String message)
	{
		Loggers.println("INFO " + Util.formatTime(System.currentTimeMillis()) + " " + name + ": " + message);
	}
	
	public void warning(Class<?> cs, Exception exception)
	{
		StringBuilder builder = new StringBuilder(String.valueOf(exception.getClass().getSimpleName()) + " : " + exception.getMessage());
		builder.append(" : stack trace:\n");
		StackTraceElement[] arrstackTraceElement = exception.getStackTrace();
		int n = arrstackTraceElement.length;
		int n2 = 0;
		while (n2 < n)
		{
			StackTraceElement stack = arrstackTraceElement[n2];
			builder.append(stack).append("\n");
			++n2;
		}
		Loggers.println("WARNING " + Util.formatTime(System.currentTimeMillis()) + " " + cs.getSimpleName() + ": " + builder);
	}
	
	public void warning(Class<?> cs, String message)
	{
		Loggers.println(" INFO " + Util.formatTime(System.currentTimeMillis()) + " " + cs.getSimpleName() + ": " + message);
	}
	
	public void warning(Exception exception)
	{
		StringBuilder builder = new StringBuilder(String.valueOf(exception.getClass().getSimpleName()) + " : " + exception.getMessage());
		builder.append(" : stack trace:\n");
		StackTraceElement[] arrstackTraceElement = exception.getStackTrace();
		int n = arrstackTraceElement.length;
		int n2 = 0;
		while (n2 < n)
		{
			StackTraceElement stack = arrstackTraceElement[n2];
			builder.append(stack).append("\n");
			++n2;
		}
		Loggers.println("WARNING " + Util.formatTime(System.currentTimeMillis()) + " " + name + ": " + builder);
	}
	
	public void warning(Object owner, Exception exception)
	{
		StringBuilder builder = new StringBuilder(String.valueOf(exception.getClass().getSimpleName()) + " : " + exception.getMessage());
		builder.append(" : stack trace:\n");
		StackTraceElement[] arrstackTraceElement = exception.getStackTrace();
		int n = arrstackTraceElement.length;
		int n2 = 0;
		while (n2 < n)
		{
			StackTraceElement stack = arrstackTraceElement[n2];
			builder.append(stack).append("\n");
			++n2;
		}
		Loggers.println("WARNING " + Util.formatTime(System.currentTimeMillis()) + " " + owner.getClass().getSimpleName() + ": " + builder);
	}
	
	public void warning(Object owner, String message)
	{
		Loggers.println("WARNING " + Util.formatTime(System.currentTimeMillis()) + " " + owner.getClass().getSimpleName() + ": " + message);
	}
	
	public void warning(String message)
	{
		Loggers.println("WARNING " + Util.formatTime(System.currentTimeMillis()) + " " + name + ": " + message);
	}
	
	public void warning(String name, Exception exception)
	{
		StringBuilder builder = new StringBuilder(String.valueOf(exception.getClass().getSimpleName()) + " : " + exception.getMessage());
		builder.append(" : stack trace:\n");
		StackTraceElement[] arrstackTraceElement = exception.getStackTrace();
		int n = arrstackTraceElement.length;
		int n2 = 0;
		while (n2 < n)
		{
			StackTraceElement stack = arrstackTraceElement[n2];
			builder.append(stack).append("\n");
			++n2;
		}
		Loggers.println("WARNING " + Util.formatTime(System.currentTimeMillis()) + " " + name + ": " + builder);
	}
	
	public void warning(String name, String message)
	{
		Loggers.println("WARNING " + Util.formatTime(System.currentTimeMillis()) + " " + name + ": " + message);
	}
}

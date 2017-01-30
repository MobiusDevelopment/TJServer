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
package rlib.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Util
{
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
	
	public static boolean checkFreePorts(String host, int port) throws InterruptedException
	{
		try
		{
			ServerSocket serverSocket = host.equalsIgnoreCase("*") ? new ServerSocket(port) : new ServerSocket(port, 50, InetAddress.getByName(host));
			serverSocket.close();
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}
	
	public static boolean checkFreePorts(String host, int[] ports) throws InterruptedException
	{
		int[] arrn = ports;
		int n = arrn.length;
		int n2 = 0;
		while (n2 < n)
		{
			int port = arrn[n2];
			try
			{
				ServerSocket serverSocket = host.equalsIgnoreCase("*") ? new ServerSocket(port) : new ServerSocket(port, 50, InetAddress.getByName(host));
				serverSocket.close();
			}
			catch (IOException e)
			{
				return false;
			}
			++n2;
		}
		return true;
	}
	
	public static String formatTime(long time)
	{
		return timeFormat.format(new Date(time));
	}
	
	public static int getFreePort(int port)
	{
		int limit = 65534;
		while (port < limit)
		{
			try
			{
				new ServerSocket(port).close();
				return port;
			}
			catch (IOException e)
			{
				++port;
			}
		}
		return -1;
	}
	
	public static String getRootPath()
	{
		return ".";
	}
	
	public static short getShort(byte[] bytes, int offset)
	{
		return (short) ((bytes[offset + 1] << 8) | (bytes[offset] & 255));
	}
	
	public static final String getUserName()
	{
		return System.getProperty("user.name");
	}
	
	public static String hexdump(byte[] array, int size)
	{
		StringBuilder builder = new StringBuilder();
		int count = 0;
		int end = size - 1;
		char[] chars = new char[16];
		int g = 0;
		while (g < 16)
		{
			chars[g] = 46;
			++g;
		}
		int i = 0;
		while (i < size)
		{
			int ch;
			String text;
			int val = array[i];
			if (val < 0)
			{
				val += 256;
			}
			if ((text = Integer.toHexString(val).toUpperCase()).length() == 1)
			{
				text = "0" + text;
			}
			if ((ch = val) < 33)
			{
				ch = 46;
			}
			if (i == end)
			{
				chars[count] = (char) ch;
				builder.append(text);
				int j = 0;
				while (j < (15 - count))
				{
					builder.append("   ");
					++j;
				}
				builder.append("    ").append(chars).append('\n');
			}
			else if (count < 15)
			{
				chars[count++] = (char) ch;
				builder.append(text).append(' ');
			}
			else
			{
				chars[15] = (char) ch;
				builder.append(text).append("    ").append(chars).append('\n');
				count = 0;
				int g2 = 0;
				while (g2 < 16)
				{
					chars[g2] = 46;
					++g2;
				}
			}
			++i;
		}
		return builder.toString();
	}
}

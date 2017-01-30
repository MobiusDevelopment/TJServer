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
package tera.gameserver;

import tera.util.LocalObjects;

/**
 * @author Ronn
 */
public class ServerThread extends Thread
{
	/**
	 * Method currentThread.
	 * @return ServerThread
	 */
	public static ServerThread currentThread()
	{
		return (ServerThread) Thread.currentThread();
	}
	
	private final LocalObjects local;
	
	public ServerThread()
	{
		local = new LocalObjects();
	}
	
	/**
	 * Constructor for ServerThread.
	 * @param group ThreadGroup
	 * @param target Runnable
	 * @param name String
	 */
	public ServerThread(ThreadGroup group, Runnable target, String name)
	{
		super(group, target, name);
		local = new LocalObjects();
	}
	
	/**
	 * Method getLocal.
	 * @return LocalObjects
	 */
	public final LocalObjects getLocal()
	{
		return local;
	}
}

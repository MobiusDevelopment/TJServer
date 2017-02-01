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
package tera.remotecontrol;

import java.io.IOException;
import java.net.ServerSocket;

import tera.Config;
import tera.gameserver.ServerThread;

import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 * @created 26.03.2012
 */
public final class ServerControl extends ServerThread
{
	private static final Logger log = Loggers.getLogger(ServerControl.class);
	private static ServerControl instance;
	private static ServerSocket serverSocket;
	private static Client client;
	public static boolean authed;
	
	/**
	 * Method init.
	 * @throws IOException
	 */
	public static void init() throws IOException
	{
		instance = new ServerControl();
		serverSocket = new ServerSocket(Config.DIST_CONTROL_PORT);
		log.info("open server socket on " + Config.DIST_CONTROL_PORT + " port.");
		instance.start();
	}
	
	private ServerControl()
	{
		setName("RemoteControl");
		setPriority(MIN_PRIORITY);
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			client = new Client(serverSocket.accept());
			client.start();
			
			while (true)
			{
				try
				{
					Thread.sleep(Config.DIST_CONTROL_CLIENT_INTERVAL);
					
					if (client == null)
					{
						client = new Client(serverSocket.accept());
						client.start();
					}
					
					if (client.isClosed())
					{
						authed = false;
						client.interrupt();
						client = null;
					}
					
					if (client.getState() == State.BLOCKED)
					{
						Thread.sleep(5000);
						
						if (client.getState() == State.BLOCKED)
						{
							authed = false;
							client.interrupt();
							client = null;
						}
					}
				}
				catch (Exception e)
				{
					log.warning(e);
				}
			}
		}
		catch (Exception e)
		{
			log.warning(e);
		}
	}
}
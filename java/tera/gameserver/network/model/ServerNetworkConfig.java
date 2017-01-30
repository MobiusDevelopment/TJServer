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
package tera.gameserver.network.model;

import tera.Config;
import tera.gameserver.ServerThread;

import rlib.network.NetworkConfig;

/**
 * @author Ronn
 */
public class ServerNetworkConfig implements NetworkConfig
{
	private static ServerNetworkConfig instance;
	
	/**
	 * Method getInstance.
	 * @return ServerNetworkConfig
	 */
	public static ServerNetworkConfig getInstance()
	{
		if (instance == null)
		{
			instance = new ServerNetworkConfig();
		}
		
		return instance;
	}
	
	/**
	 * Method getGroupName.
	 * @return String
	 * @see rlib.network.NetworkConfig#getGroupName()
	 */
	@Override
	public String getGroupName()
	{
		return "Network";
	}
	
	/**
	 * Method getGroupSize.
	 * @return int
	 * @see rlib.network.NetworkConfig#getGroupSize()
	 */
	@Override
	public int getGroupSize()
	{
		return Config.NETWORK_GROUP_SIZE;
	}
	
	/**
	 * Method getReadBufferSize.
	 * @return int
	 * @see rlib.network.NetworkConfig#getReadBufferSize()
	 */
	@Override
	public int getReadBufferSize()
	{
		return Config.NETWORK_READ_BUFFER_SIZE;
	}
	
	/**
	 * Method getThreadClass.
	 * @return Class<? extends Thread>
	 * @see rlib.network.NetworkConfig#getThreadClass()
	 */
	@Override
	public Class<? extends Thread> getThreadClass()
	{
		return ServerThread.class;
	}
	
	/**
	 * Method getThreadPriority.
	 * @return int
	 * @see rlib.network.NetworkConfig#getThreadPriority()
	 */
	@Override
	public int getThreadPriority()
	{
		return Config.NETWORK_THREAD_PRIORITY;
	}
	
	/**
	 * Method getWriteBufferSize.
	 * @return int
	 * @see rlib.network.NetworkConfig#getWriteBufferSize()
	 */
	@Override
	public int getWriteBufferSize()
	{
		return Config.NETWORK_WRITE_BUFFER_SIZE;
	}
	
	/**
	 * Method isVesibleReadException.
	 * @return boolean
	 * @see rlib.network.NetworkConfig#isVesibleReadException()
	 */
	@Override
	public boolean isVesibleReadException()
	{
		return false;
	}
	
	/**
	 * Method isVesibleWriteException.
	 * @return boolean
	 * @see rlib.network.NetworkConfig#isVesibleWriteException()
	 */
	@Override
	public boolean isVesibleWriteException()
	{
		return false;
	}
}
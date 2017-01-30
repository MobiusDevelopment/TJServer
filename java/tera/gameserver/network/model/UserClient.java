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

import tera.gameserver.manager.AccountManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Account;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.crypt.CryptorState;
import tera.gameserver.network.crypt.TeraCrypt;
import tera.gameserver.network.serverpackets.ConnectAccepted;
import tera.gameserver.network.serverpackets.ServerPacket;

import rlib.network.packets.ReadeablePacket;
import rlib.network.server.client.AbstractClient;

/**
 * @author Ronn
 * @created 24.03.2012
 */
@SuppressWarnings("rawtypes")
public final class UserClient extends AbstractClient<Account, Player, UserAsynConnection, TeraCrypt> implements Runnable
{
	/**
	 * Constructor for UserClient.
	 * @param connection UserAsynConnection
	 */
	public UserClient(UserAsynConnection connection)
	{
		super(connection, new TeraCrypt());
	}
	
	/**
	 * Method close.
	 * @see rlib.network.server.client.Client#close()
	 */
	@Override
	public void close()
	{
		lock();
		
		try
		{
			setClosed(true);
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.execute(this);
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method executePacket.
	 * @param packet ReadeablePacket
	 */
	@Override
	protected void executePacket(ReadeablePacket packet)
	{
		final ExecutorManager executor = ExecutorManager.getInstance();
		
		if (packet.isSynchronized())
		{
			executor.runSynchPacket(packet);
		}
		else
		{
			executor.runAsynchPacket(packet);
		}
	}
	
	/**
	 * Method getCryptorState.
	 * @return CryptorState
	 */
	public CryptorState getCryptorState()
	{
		return crypt.getState();
	}
	
	/**
	 * Method getHostAddress.
	 * @return String
	 * @see rlib.network.server.client.Client#getHostAddress()
	 */
	@Override
	public String getHostAddress()
	{
		return "unknown";
	}
	
	/**
	 * Method getLastActive.
	 * @return long
	 */
	public long getLastActive()
	{
		return connection.getLastActive();
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
			final Player owner = getOwner();
			
			if (owner != null)
			{
				owner.setClient(null);
				owner.deleteMe();
				setOwner(null);
			}
		}
		catch (Exception e)
		{
			log.warning(e);
		}
		
		lock();
		
		try
		{
			final UserAsynConnection connection = getConnection();
			
			if (connection != null)
			{
				connection.close();
				connection.setClient(null);
				setConnection(null);
			}
			
			final Account account = getAccount();
			
			if (account != null)
			{
				final AccountManager accountManager = AccountManager.getInstance();
				accountManager.closeAccount(account);
				setAccount(null);
			}
		}
		catch (Exception e)
		{
			log.warning(e);
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method sendPacket.
	 * @param packet ServerPacket
	 * @param increase boolean
	 */
	public void sendPacket(ServerPacket packet, boolean increase)
	{
		if (increase)
		{
			packet.increaseSends();
		}
		
		sendPacket(packet);
	}
	
	/**
	 * Method setClosed.
	 * @param closed boolean
	 */
	public void setClosed(boolean closed)
	{
		this.closed = closed;
	}
	
	/**
	 * Method setConnection.
	 * @param connection UserAsynConnection
	 */
	public void setConnection(UserAsynConnection connection)
	{
		this.connection = connection;
	}
	
	/**
	 * Method setCryptorState.
	 * @param state CryptorState
	 */
	public void setCryptorState(CryptorState state)
	{
		crypt.setState(state);
	}
	
	/**
	 * Method successfulConnection.
	 * @see rlib.network.server.client.Client#successfulConnection()
	 */
	@Override
	public void successfulConnection()
	{
		final ConnectAccepted packet = ConnectAccepted.getInstance();
		packet.increaseSends();
		connection.sendPacket(packet);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "Client account = " + account + ", connection = " + connection;
	}
}
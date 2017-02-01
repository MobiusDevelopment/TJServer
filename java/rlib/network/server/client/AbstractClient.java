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
package rlib.network.server.client;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;

import rlib.concurrent.Locks;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.network.AsynConnection;
import rlib.network.GameCrypt;
import rlib.network.packets.ReadeablePacket;
import rlib.network.packets.SendablePacket;

public abstract class AbstractClient<A, O, C extends AsynConnection, T extends GameCrypt> implements Client<A, O, C>
{
	protected static final Logger log = Loggers.getLogger("Client");
	private final Lock lock = Locks.newLock();
	protected O owner;
	protected A account;
	protected C connection;
	protected T crypt;
	protected boolean closed;
	
	public AbstractClient(C connection, T crypt)
	{
		this.connection = connection;
		this.crypt = crypt;
	}
	
	@Override
	public void close()
	{
		C connection = this.getConnection();
		if (connection != null)
		{
			connection.close();
		}
		this.closed = true;
	}
	
	@Override
	public void decrypt(ByteBuffer data, int offset, int length)
	{
		this.crypt.decrypt(data.array(), offset, length);
	}
	
	@Override
	public void encrypt(ByteBuffer data, int offset, int length)
	{
		this.crypt.encrypt(data.array(), offset, length);
	}
	
	protected abstract void executePacket(ReadeablePacket var1);
	
	@Override
	public final A getAccount()
	{
		return this.account;
	}
	
	@Override
	public final C getConnection()
	{
		return this.connection;
	}
	
	@Override
	public final O getOwner()
	{
		return this.owner;
	}
	
	@Override
	public final boolean isConnected()
	{
		if (!this.closed && (this.connection != null) && !this.connection.isClosed())
		{
			return true;
		}
		return false;
	}
	
	@Override
	public final void lock()
	{
		this.lock.lock();
	}
	
	@Override
	public final void readPacket(ReadeablePacket packet, ByteBuffer buffer)
	{
		if (packet != null)
		{
			packet.setBuffer(buffer);
			packet.setOwner(this);
			if (packet.read())
			{
				this.executePacket(packet);
			}
			packet.setBuffer(null);
		}
	}
	
	@Override
	public final void sendPacket(SendablePacket packet)
	{
		if (this.closed)
		{
			return;
		}
		this.lock();
		try
		{
			C connection = this.getConnection();
			if (connection != null)
			{
				connection.sendPacket(packet);
			}
		}
		finally
		{
			this.unlock();
		}
	}
	
	@Override
	public final void setAccount(A account)
	{
		this.account = account;
	}
	
	@Override
	public final void setOwner(O owner)
	{
		this.owner = owner;
	}
	
	@Override
	public void successfulConnection()
	{
		log.info(this, String.valueOf(getHostAddress()) + " successful connection.");
	}
	
	@Override
	public final void unlock()
	{
		this.lock.unlock();
	}
}

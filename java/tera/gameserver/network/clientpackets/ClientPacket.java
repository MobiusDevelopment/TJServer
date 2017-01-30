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
package tera.gameserver.network.clientpackets;

import tera.gameserver.network.ClientPacketType;
import tera.gameserver.network.model.UserClient;

import rlib.network.packets.AbstractReadeablePacket;
import rlib.util.pools.FoldablePool;

/**
 */
public abstract class ClientPacket extends AbstractReadeablePacket<UserClient>
{
	private ClientPacketType type;
	
	/**
	 * Method getClient.
	 * @return UserClient
	 */
	public final UserClient getClient()
	{
		return getOwner();
	}
	
	/**
	 * Method getPacketType.
	 * @return ClientPacketType
	 */
	public final ClientPacketType getPacketType()
	{
		return type;
	}
	
	/**
	 * Method getPool.
	 * @return FoldablePool<ClientPacket>
	 */
	@Override
	protected final FoldablePool<ClientPacket> getPool()
	{
		return type.getPool();
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.ReadeablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return true;
	}
	
	/**
	 * Method newInstance.
	 * @return ClientPacket
	 * @see rlib.network.packets.ReadeablePacket#newInstance()
	 */
	@Override
	public final ClientPacket newInstance()
	{
		ClientPacket packet = getPool().take();
		
		if (packet == null)
		{
			try
			{
				packet = getClass().newInstance();
				packet.setPacketType(type);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				log.warning(this, e);
			}
		}
		
		return packet;
	}
	
	/**
	 * Method readPassword.
	 * @return String
	 */
	protected final String readPassword()
	{
		final StringBuilder builder = new StringBuilder();
		
		while (buffer.hasRemaining())
		{
			builder.append((char) buffer.get());
		}
		
		return builder.toString();
	}
	
	/**
	 * Method readS.
	 * @return String
	 */
	protected final String readS()
	{
		final StringBuilder builder = new StringBuilder();
		byte ch;
		
		while (buffer.remaining() > 2)
		{
			buffer.get();
			ch = buffer.get();
			
			if (ch == 0)
			{
				break;
			}
			
			builder.append((char) ch);
		}
		
		return builder.toString();
	}
	
	/**
	 * Method setPacketType.
	 * @param type ClientPacketType
	 */
	public final void setPacketType(ClientPacketType type)
	{
		this.type = type;
	}
}
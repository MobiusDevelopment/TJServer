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

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import tera.Config;
import tera.gameserver.network.ClientPacketType;
import tera.gameserver.network.clientpackets.ClientPacket;
import tera.gameserver.network.crypt.CryptorState;
import tera.gameserver.network.serverpackets.ServerPacket;

import rlib.network.server.ServerNetwork;
import rlib.network.server.client.AbstractClientConnection;
import rlib.util.Util;

/**
 * @author Ronn
 */
public final class UserAsynConnection extends AbstractClientConnection<UserClient, ClientPacket, ServerPacket>
{
	private static final ClientPacket CLIENT_KEY = ClientPacketType.CLIENT_KEY.getPacket();
	private final ByteBuffer waitBuffer;
	private int waitSize;
	private boolean canDecrypt;
	
	/**
	 * Constructor for UserAsynConnection.
	 * @param network ServerNetwork
	 * @param channel AsynchronousSocketChannel
	 */
	public UserAsynConnection(ServerNetwork network, AsynchronousSocketChannel channel)
	{
		super(network, channel, ServerPacket.class);
		waitBuffer = network.getReadByteBuffer();
		waitBuffer.clear();
		waitSize = -1;
		canDecrypt = true;
	}
	
	/**
	 * Method close.
	 * @see rlib.network.AsynConnection#close()
	 */
	@Override
	public void close()
	{
		super.close();
		lock.lock();
		
		try
		{
			if (isClosed())
			{
				return;
			}
			
			network.putReadByteBuffer(waitBuffer);
		}
		
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Method getPacket.
	 * @param buffer ByteBuffer
	 * @param client UserClient
	 * @return ClientPacket
	 */
	private ClientPacket getPacket(ByteBuffer buffer, UserClient client)
	{
		int opcode = -1;
		
		if (buffer.remaining() < 2)
		{
			return null;
		}
		
		opcode = buffer.getShort() & 0xFFFF;
		final ClientPacket packet = ClientPacketType.createPacket(opcode);
		return packet;
	}
	
	/**
	 * Method getWaitBuffer.
	 * @return ByteBuffer
	 */
	protected ByteBuffer getWaitBuffer()
	{
		return waitBuffer;
	}
	
	/**
	 * Method isCanDecrypt.
	 * @return boolean
	 */
	private boolean isCanDecrypt()
	{
		return canDecrypt;
	}
	
	/**
	 * Method isReady.
	 * @param buffer ByteBuffer
	 * @return boolean
	 */
	@Override
	protected boolean isReady(ByteBuffer buffer)
	{
		final ByteBuffer waitBuffer = getWaitBuffer();
		
		if ((waitBuffer.position() == 0) && (buffer.limit() < 1000))
		{
			return true;
		}
		
		final UserClient client = getClient();
		client.decrypt(buffer, 0, buffer.limit());
		
		if (waitSize != -1)
		{
			waitBuffer.put(buffer);
		}
		else
		{
			waitBuffer.put(buffer);
			waitBuffer.position(0);
			waitSize = waitBuffer.getShort() & 0xFFFF;
			waitBuffer.position(buffer.limit());
		}
		
		if (waitSize <= waitBuffer.position())
		{
			waitBuffer.flip();
			buffer.clear();
			buffer.put(waitBuffer);
			waitBuffer.clear();
			buffer.flip();
			waitSize = -1;
			setCanDecrypt(false);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method movePacketToBuffer.
	 * @param packet ServerPacket
	 * @param buffer ByteBuffer
	 */
	@Override
	protected void movePacketToBuffer(ServerPacket packet, ByteBuffer buffer)
	{
		final UserClient client = getClient();
		buffer.clear();
		
		if (client.getCryptorState() == CryptorState.READY_TO_WORK)
		{
			buffer.position(2);
		}
		
		if (packet.isSynchronized())
		{
			synchronized (packet)
			{
				packet.setBuffer(buffer);
				packet.setOwner(client);
				packet.writeLocal();
				packet.setBuffer(null);
			}
		}
		else
		{
			packet.setOwner(client);
			packet.write(buffer);
		}
		
		buffer.flip();
		
		if (client.getCryptorState() == CryptorState.READY_TO_WORK)
		{
			packet.writeHeader(buffer, buffer.limit());
		}
		
		if (Config.DEVELOPER_DEBUG_SERVER_PACKETS)
		{
			System.out.println("Server packet " + packet.getName() + ", dump(size: " + buffer.limit() + "):");
			System.out.println(Util.hexdump(buffer.array(), buffer.limit()));
		}
		
		client.encrypt(buffer, 0, buffer.limit());
	}
	
	/**
	 * Method readPacket.
	 * @param buffer ByteBuffer
	 */
	@Override
	protected void readPacket(ByteBuffer buffer)
	{
		final UserClient client = getClient();
		
		if (!isCanDecrypt())
		{
			setCanDecrypt(true);
		}
		else
		{
			client.decrypt(buffer, 0, buffer.limit());
		}
		
		if (Config.DEVELOPER_DEBUG_CLIENT_PACKETS)
		{
			System.out.println("Client dump(size: " + buffer.limit() + "):\n" + Util.hexdump(buffer.array(), buffer.limit()));
		}
		
		if ((client.getCryptorState() == CryptorState.READY_TO_WORK) && (buffer.remaining() > 1))
		{
			try
			{
				int length = buffer.getShort() & 0xFFFF;
				
				if (length >= buffer.remaining())
				{
					client.readPacket(getPacket(buffer, client), buffer);
				}
				else
				{
					client.readPacket(getPacket(buffer, client), buffer);
					buffer.position(length);
					
					for (int i = 0; (buffer.remaining() > 2) && (i < Config.NETWORK_MAXIMUM_PACKET_CUT); i++)
					{
						length += buffer.getShort() & 0xFFFF;
						client.readPacket(getPacket(buffer, client), buffer);
						
						if ((length < 4) || (length > buffer.limit()))
						{
							break;
						}
						
						buffer.position(length);
					}
				}
			}
			catch (Exception e)
			{
				log.warning(e);
			}
		}
		else if (client.getCryptorState() != CryptorState.READY_TO_WORK)
		{
			client.readPacket(CLIENT_KEY.newInstance(), buffer);
		}
	}
	
	/**
	 * Method setCanDecrypt.
	 * @param canDecrypt boolean
	 */
	private void setCanDecrypt(boolean canDecrypt)
	{
		this.canDecrypt = canDecrypt;
	}
}
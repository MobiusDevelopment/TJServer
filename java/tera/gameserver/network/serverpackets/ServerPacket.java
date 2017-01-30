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
package tera.gameserver.network.serverpackets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import tera.gameserver.network.ServerPacketType;
import tera.gameserver.network.model.UserClient;

import rlib.network.packets.AbstractSendablePacket;
import rlib.util.Strings;
import rlib.util.Util;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 */
@SuppressWarnings("unchecked")
public abstract class ServerPacket extends AbstractSendablePacket<UserClient>
{
	private static final StringBuilder EMPTY_STRING_BUILDER = new StringBuilder();
	private static FoldablePool<ServerPacket>[] pools = new FoldablePool[ServerPacketType.LENGTH];
	private final ServerPacketType type;
	private Constructor<? extends ServerPacket> constructor;
	private final int index;
	
	public ServerPacket()
	{
		try
		{
			type = getPacketType();
			index = type.ordinal();
			constructor = getClass().getConstructor();
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			log.warning(this, e);
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Method complete.
	 * @see rlib.network.packets.SendablePacket#complete()
	 */
	@Override
	public void complete()
	{
		decreaseSends();
		
		if (counter == 0)
		{
			synchronized (this)
			{
				if (counter == 0)
				{
					counter -= 1;
					getPool().put(this);
				}
			}
		}
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	public abstract ServerPacketType getPacketType();
	
	/**
	 * Method getPool.
	 * @return FoldablePool<ServerPacket>
	 */
	protected final FoldablePool<ServerPacket> getPool()
	{
		FoldablePool<ServerPacket> pool = pools[index];
		
		if (pool == null)
		{
			synchronized (pools)
			{
				pool = pools[index];
				
				if (pool == null)
				{
					pool = Pools.newConcurrentFoldablePool(ServerPacket.class);
					pools[index] = pool;
				}
			}
		}
		
		return pool;
	}
	
	/**
	 * Method newInstance.
	 * @return ServerPacket
	 */
	public final ServerPacket newInstance()
	{
		ServerPacket packet = getPool().take();
		
		if (packet == null)
		{
			try
			{
				packet = constructor.newInstance();
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				log.warning(this, e);
			}
		}
		
		return packet;
	}
	
	/**
	 * Method write.
	 * @param buffer ByteBuffer
	 * @see rlib.network.packets.SendablePacket#write(ByteBuffer)
	 */
	@Override
	public void write(ByteBuffer buffer)
	{
		if (counter < 0)
		{
			log.warning(this, "write pooled packet");
			return;
		}
		
		try
		{
			writeImpl(buffer);
		}
		catch (Exception e)
		{
			counter = Integer.MIN_VALUE;
			log.warning(this, e);
			log.warning(this, "Buffer " + buffer + "\n" + Util.hexdump(buffer.array(), buffer.position()));
		}
	}
	
	/**
	 * Method writeHeader.
	 * @param buffer ByteBuffer
	 * @param length int
	 * @see rlib.network.packets.SendablePacket#writeHeader(ByteBuffer, int)
	 */
	@Override
	public final void writeHeader(ByteBuffer buffer, int length)
	{
		buffer.putShort(0, (short) length);
	}
	
	protected final void writeOpcode()
	{
		writeOpcode(buffer);
	}
	
	/**
	 * Method writeOpcode.
	 * @param buffer ByteBuffer
	 */
	protected final void writeOpcode(ByteBuffer buffer)
	{
		writeShort(buffer, type.getOpcode());
	}
	
	/**
	 * Method writePosition.
	 * @param buffer ByteBuffer
	 * @see rlib.network.packets.SendablePacket#writePosition(ByteBuffer)
	 */
	@Override
	public final void writePosition(ByteBuffer buffer)
	{
		buffer.position(2);
	}
	
	/**
	 * Method writeS.
	 * @param charSequence CharSequence
	 */
	protected final void writeS(CharSequence charSequence)
	{
		writeS(charSequence, true);
	}
	
	/**
	 * Method writeS.
	 * @param charSequence CharSequence
	 * @param isNull boolean
	 */
	protected final void writeS(CharSequence charSequence, boolean isNull)
	{
		if (charSequence == null)
		{
			charSequence = Strings.EMPTY;
		}
		
		buffer.order(ByteOrder.BIG_ENDIAN);
		
		for (int i = 0, length = charSequence.length(); i < length; i++)
		{
			buffer.putChar(charSequence.charAt(i));
		}
		
		if (isNull)
		{
			buffer.putShort((short) 0x0000);
		}
		
		buffer.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Method writeString.
	 * @param buffer ByteBuffer
	 * @param string String
	 */
	@Override
	protected final void writeString(ByteBuffer buffer, String string)
	{
		if (string == null)
		{
			string = Strings.EMPTY;
		}
		
		for (int i = 0, length = string.length(); i < length; i++)
		{
			buffer.putChar(string.charAt(i));
		}
		
		buffer.putShort((short) 0x0000);
	}
	
	/**
	 * Method writeStringBuilder.
	 * @param buffer ByteBuffer
	 * @param builder StringBuilder
	 */
	protected final void writeStringBuilder(ByteBuffer buffer, StringBuilder builder)
	{
		if (builder == null)
		{
			builder = EMPTY_STRING_BUILDER;
		}
		
		for (int i = 0, length = builder.length(); i < length; i++)
		{
			buffer.putChar(builder.charAt(i));
		}
		
		buffer.putShort((short) 0x0000);
	}
}
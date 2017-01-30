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
package rlib.network.packets;

import java.nio.ByteBuffer;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Strings;
import rlib.util.Util;
import rlib.util.pools.Foldable;

public abstract class AbstractSendablePacket<C> extends AbstractPacket<C> implements SendablePacket<C>, Foldable
{
	protected static final Logger log = Loggers.getLogger(SendablePacket.class);
	protected volatile int counter;
	
	@Override
	public abstract void complete();
	
	@Override
	public final void decreaseSends()
	{
		--this.counter;
	}
	
	@Override
	public void decreaseSends(int count)
	{
		this.counter -= count;
	}
	
	@Override
	public void finalyze()
	{
	}
	
	@Override
	public final void increaseSends()
	{
		++this.counter;
	}
	
	@Override
	public void increaseSends(int count)
	{
		this.counter += count;
	}
	
	@Override
	public boolean isSynchronized()
	{
		return true;
	}
	
	@Override
	public void reinit()
	{
		this.counter = 0;
	}
	
	@Override
	public void write(ByteBuffer buffer)
	{
		if (this.counter < 0)
		{
			log.warning(this, "write pooled packet");
			return;
		}
		try
		{
			this.writeImpl(buffer);
		}
		catch (Exception e)
		{
			log.warning(this, e);
			log.warning(this, "Buffer " + buffer + "\n" + Util.hexdump(buffer.array(), buffer.position()));
		}
	}
	
	protected final void writeByte(ByteBuffer buffer, int value)
	{
		buffer.put((byte) value);
	}
	
	protected final void writeByte(int value)
	{
		this.writeByte(this.buffer, value);
	}
	
	protected final void writeChar(ByteBuffer buffer, char value)
	{
		buffer.putChar(value);
	}
	
	protected final void writeChar(ByteBuffer buffer, int value)
	{
		buffer.putChar((char) value);
	}
	
	protected final void writeChar(char value)
	{
		this.writeChar(this.buffer, value);
	}
	
	protected final void writeChar(int value)
	{
		this.writeChar(this.buffer, value);
	}
	
	protected final void writeFloat(ByteBuffer buffer, float value)
	{
		buffer.putFloat(value);
	}
	
	protected final void writeFloat(float value)
	{
		this.writeFloat(this.buffer, value);
	}
	
	protected void writeImpl()
	{
		log.warning(this, new Exception("unsupperted method"));
	}
	
	protected void writeImpl(ByteBuffer buffer)
	{
		log.warning(this, new Exception("unsupperted method"));
	}
	
	protected final void writeInt(ByteBuffer buffer, int value)
	{
		buffer.putInt(value);
	}
	
	protected final void writeInt(int value)
	{
		this.writeInt(this.buffer, value);
	}
	
	@Override
	public void writeLocal()
	{
		if (this.counter < 0)
		{
			log.warning(this, "write local pooled packet");
			return;
		}
		try
		{
			this.writeImpl();
		}
		catch (Exception e)
		{
			log.warning(this, e);
			log.warning(this, "Buffer " + this.buffer + "\n" + Util.hexdump(this.buffer.array(), this.buffer.position()));
		}
	}
	
	protected final void writeLong(ByteBuffer buffer, long value)
	{
		buffer.putLong(value);
	}
	
	protected final void writeLong(long value)
	{
		this.writeLong(this.buffer, value);
	}
	
	protected void writeShort(ByteBuffer buffer, int value)
	{
		buffer.putShort((short) value);
	}
	
	protected void writeShort(int value)
	{
		this.writeShort(this.buffer, value);
	}
	
	protected void writeString(ByteBuffer buffer, String string)
	{
		if (string == null)
		{
			string = Strings.EMPTY;
		}
		int i = 0;
		int length = string.length();
		while (i < length)
		{
			buffer.putChar(string.charAt(i));
			++i;
		}
	}
	
	protected void writeString(String string)
	{
		this.writeString(this.buffer, string);
	}
}

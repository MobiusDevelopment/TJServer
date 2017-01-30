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

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Util;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;

public abstract class AbstractReadeablePacket<C> extends AbstractPacket<C> implements ReadeablePacket<C>, Foldable
{
	protected static final Logger log = Loggers.getLogger(ReadeablePacket.class);
	
	@Override
	public void finalyze()
	{
	}
	
	@Override
	public final int getAvaliableBytes()
	{
		return this.buffer.remaining();
	}
	
	protected abstract FoldablePool getPool();
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	public ReadeablePacket<C> newInstance()
	{
		return (ReadeablePacket) this.getPool().take();
	}
	
	@Override
	public final boolean read()
	{
		try
		{
			this.readImpl();
			return true;
		}
		catch (Exception e)
		{
			log.warning(this, e);
			log.warning(this, "buffer " + this.buffer + "\n" + Util.hexdump(this.buffer.array(), this.buffer.limit()));
			return false;
		}
	}
	
	protected final int readByte()
	{
		return this.buffer.get() & 255;
	}
	
	protected final void readBytes(byte[] array)
	{
		this.buffer.get(array);
	}
	
	protected final void readBytes(byte[] array, int offset, int length)
	{
		this.buffer.get(array, offset, length);
	}
	
	protected final float readFloat()
	{
		return this.buffer.getFloat();
	}
	
	protected abstract void readImpl();
	
	protected final int readInt()
	{
		return this.buffer.getInt();
	}
	
	protected final long readLong()
	{
		return this.buffer.getLong();
	}
	
	protected final int readShort()
	{
		return this.buffer.getShort() & 65535;
	}
	
	protected final String readString()
	{
		StringBuilder builder = new StringBuilder();
		while (this.buffer.remaining() > 1)
		{
			char cha = this.buffer.getChar();
			if (cha == '\u0000')
			{
				break;
			}
			builder.append(cha);
		}
		return builder.toString();
	}
	
	protected final String readString(int length)
	{
		char[] array = new char[length];
		int i = 0;
		while ((i < length) && (this.buffer.remaining() > 1))
		{
			array[i] = this.buffer.getChar();
			++i;
		}
		return new String(array);
	}
	
	@Override
	public void reinit()
	{
	}
	
	@Override
	public void run()
	{
		try
		{
			try
			{
				this.runImpl();
			}
			catch (Exception e)
			{
				log.warning(this, e);
				this.getPool().put(this);
			}
		}
		finally
		{
			this.getPool().put(this);
		}
	}
	
	protected abstract void runImpl();
}

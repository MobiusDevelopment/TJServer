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
		return buffer.remaining();
	}
	
	@SuppressWarnings("rawtypes")
	protected abstract FoldablePool getPool();
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ReadeablePacket<C> newInstance()
	{
		return (ReadeablePacket<C>) getPool().take();
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
			log.warning(this, "buffer " + buffer + "\n" + Util.hexdump(buffer.array(), buffer.limit()));
			return false;
		}
	}
	
	protected final int readByte()
	{
		return buffer.get() & 255;
	}
	
	protected final void readBytes(byte[] array)
	{
		buffer.get(array);
	}
	
	protected final void readBytes(byte[] array, int offset, int length)
	{
		buffer.get(array, offset, length);
	}
	
	protected final float readFloat()
	{
		return buffer.getFloat();
	}
	
	protected abstract void readImpl();
	
	protected final int readInt()
	{
		return buffer.getInt();
	}
	
	protected final long readLong()
	{
		return buffer.getLong();
	}
	
	protected final int readShort()
	{
		return buffer.getShort() & 65535;
	}
	
	protected final String readString()
	{
		StringBuilder builder = new StringBuilder();
		while (buffer.remaining() > 1)
		{
			char cha = buffer.getChar();
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
		while ((i < length) && (buffer.remaining() > 1))
		{
			array[i] = buffer.getChar();
			++i;
		}
		return new String(array);
	}
	
	@Override
	public void reinit()
	{
	}
	
	@SuppressWarnings("unchecked")
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
				getPool().put(this);
			}
		}
		finally
		{
			getPool().put(this);
		}
	}
	
	protected abstract void runImpl();
}

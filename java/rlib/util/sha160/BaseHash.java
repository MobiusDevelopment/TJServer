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
package rlib.util.sha160;

public abstract class BaseHash
{
	protected String name;
	protected int hashSize;
	protected int blockSize;
	protected long count;
	protected byte[] buffer;
	
	protected BaseHash(String name, int hashSize, int blockSize)
	{
		this.name = name;
		this.hashSize = hashSize;
		this.blockSize = blockSize;
		buffer = new byte[blockSize];
		resetContext();
	}
	
	@Override
	public abstract Object clone();
	
	public byte[] digest()
	{
		byte[] tail = padBuffer();
		update(tail, 0, tail.length);
		byte[] result = getResult();
		reset();
		return result;
	}
	
	protected abstract byte[] getResult();
	
	public String name()
	{
		return name;
	}
	
	protected abstract byte[] padBuffer();
	
	public void reset()
	{
		count = 0;
		int i = 0;
		while (i < blockSize)
		{
			buffer[i++] = 0;
		}
		resetContext();
	}
	
	protected abstract void resetContext();
	
	protected abstract void transform(byte[] var1, int var2);
	
	public void update(byte[] b, int offset, int len)
	{
		int n = (int) (count % blockSize);
		count += len;
		int partLen = blockSize - n;
		int i = 0;
		if (len >= partLen)
		{
			System.arraycopy(b, offset, buffer, n, partLen);
			transform(buffer, 0);
			i = partLen;
			while (((i + blockSize) - 1) < len)
			{
				transform(b, offset + i);
				i += blockSize;
			}
			n = 0;
		}
		if (i < len)
		{
			System.arraycopy(b, offset + i, buffer, n, len - i);
		}
	}
}

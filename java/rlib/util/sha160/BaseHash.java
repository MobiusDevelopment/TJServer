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
		this.buffer = new byte[blockSize];
		this.resetContext();
	}
	
	@Override
	public abstract Object clone();
	
	public byte[] digest()
	{
		byte[] tail = this.padBuffer();
		this.update(tail, 0, tail.length);
		byte[] result = this.getResult();
		this.reset();
		return result;
	}
	
	protected abstract byte[] getResult();
	
	public String name()
	{
		return this.name;
	}
	
	protected abstract byte[] padBuffer();
	
	public void reset()
	{
		this.count = 0;
		int i = 0;
		while (i < this.blockSize)
		{
			this.buffer[i++] = 0;
		}
		this.resetContext();
	}
	
	protected abstract void resetContext();
	
	protected abstract void transform(byte[] var1, int var2);
	
	public void update(byte[] b, int offset, int len)
	{
		int n = (int) (this.count % this.blockSize);
		this.count += len;
		int partLen = this.blockSize - n;
		int i = 0;
		if (len >= partLen)
		{
			System.arraycopy(b, offset, this.buffer, n, partLen);
			this.transform(this.buffer, 0);
			i = partLen;
			while (((i + this.blockSize) - 1) < len)
			{
				this.transform(b, offset + i);
				i += this.blockSize;
			}
			n = 0;
		}
		if (i < len)
		{
			System.arraycopy(b, offset + i, this.buffer, n, len - i);
		}
	}
}

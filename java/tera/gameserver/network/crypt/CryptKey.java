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
package tera.gameserver.network.crypt;

public final class CryptKey
{
	protected int size;
	protected int firstPos;
	protected int secondPos;
	protected int maxPos;
	protected int key;
	protected byte[] buffer;
	protected long sum;
	
	/**
	 * Constructor for CryptKey.
	 * @param pos int
	 * @param size int
	 */
	protected CryptKey(int pos, int size)
	{
		secondPos = pos;
		maxPos = pos;
		this.size = size;
		buffer = new byte[size * 4];
	}
	
	/**
	 * Method getBuffer.
	 * @return byte[]
	 */
	public byte[] getBuffer()
	{
		return buffer;
	}
	
	/**
	 * Method getFirstPos.
	 * @return int
	 */
	public int getFirstPos()
	{
		return firstPos;
	}
	
	/**
	 * Method getKey.
	 * @return int
	 */
	public int getKey()
	{
		return key;
	}
	
	/**
	 * Method getSecondPos.
	 * @return int
	 */
	public int getSecondPos()
	{
		return secondPos;
	}
	
	/**
	 * Method getSize.
	 * @return int
	 */
	public int getSize()
	{
		return size;
	}
	
	/**
	 * Method getSum.
	 * @return long
	 */
	public long getSum()
	{
		return sum;
	}
	
	/**
	 * Method setBuffer.
	 * @param buffer byte[]
	 */
	public void setBuffer(byte[] buffer)
	{
		this.buffer = buffer;
	}
	
	/**
	 * Method setFirstPos.
	 * @param firstPos int
	 */
	public void setFirstPos(int firstPos)
	{
		this.firstPos = firstPos;
	}
	
	/**
	 * Method setKey.
	 * @param key int
	 */
	public void setKey(int key)
	{
		this.key = key;
	}
	
	/**
	 * Method setSecondPos.
	 * @param secondPos int
	 */
	public void setSecondPos(int secondPos)
	{
		this.secondPos = secondPos;
	}
	
	/**
	 * Method setSum.
	 * @param sum long
	 */
	public void setSum(long sum)
	{
		this.sum = sum;
	}
}
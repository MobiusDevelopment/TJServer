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

import rlib.util.array.Arrays;
import rlib.util.sha160.Sha160;

public final class Crypt
{
	public static final byte EMPTY_BYTE_ARRAY[] = new byte[0];
	
	/**
	 * Method bytesToUInts.
	 * @param bytes byte[]
	 * @param offset int
	 * @return long
	 */
	private static long bytesToUInts(byte bytes[], int offset)
	{
		final int i = ((0xFF & bytes[offset + 3]) << 24) | ((0xFF & bytes[offset + 2]) << 16) | ((0xFF & bytes[offset + 1]) << 8) | (0xFF & bytes[offset]);
		return i & 0xFFFFFFFFL;
	}
	
	/**
	 * Method intToBytes.
	 * @param containerInt byte[]
	 * @param unsignedInt long
	 * @param offset int
	 */
	private static void intToBytes(byte containerInt[], long unsignedInt, int offset)
	{
		containerInt[offset + 3] = (byte) (int) ((unsignedInt & 0xff000000L) >> 24);
		containerInt[offset + 2] = (byte) (int) ((unsignedInt & 0xff0000L) >> 16);
		containerInt[offset + 1] = (byte) (int) ((unsignedInt & 65280L) >> 8);
		containerInt[offset] = (byte) (int) (unsignedInt & 255L);
	}
	
	/**
	 * Method shiftKey.
	 * @param src byte[]
	 * @param dest byte[]
	 * @param n int
	 * @param direction boolean
	 */
	public static void shiftKey(byte[] src, byte[] dest, int n, boolean direction)
	{
		final byte[] tmp = new byte[128];
		
		for (int i = 0; i < 128; i++)
		{
			if (direction)
			{
				tmp[(i + n) % 128] = src[i];
			}
			else
			{
				tmp[i] = src[(i + n) % 128];
			}
		}
		
		for (int i = 0; i < 128; i++)
		{
			dest[i] = tmp[i];
		}
	}
	
	/**
	 * Method subarray.
	 * @param buffer byte[]
	 * @param offset int
	 * @param end int
	 * @return byte[]
	 */
	public static byte[] subarray(byte buffer[], int offset, int end)
	{
		if (buffer == null)
		{
			return null;
		}
		
		if (offset < 0)
		{
			offset = 0;
		}
		
		if (end > buffer.length)
		{
			end = buffer.length;
		}
		
		final int newSize = end - offset;
		
		if (newSize <= 0)
		{
			return EMPTY_BYTE_ARRAY;
		}
		final byte subarray[] = new byte[newSize];
		System.arraycopy(buffer, offset, subarray, 0, newSize);
		return subarray;
	}
	
	/**
	 * Method xorKey.
	 * @param src1 byte[]
	 * @param src2 byte[]
	 * @param dst byte[]
	 */
	public static void xorKey(byte[] src1, byte[] src2, byte[] dst)
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				dst[(i * 16) + j] = (byte) ((src1[(i * 16) + j] & 0xff) ^ (src2[(i * 16) + j] & 0xff));
			}
		}
	}
	
	private int changeData;
	private int changeLenght;
	private final CryptKey[] keys;
	private final CryptKey first;
	private final CryptKey second;
	private final CryptKey thrid;
	private final byte[] containerInt;
	
	public Crypt()
	{
		first = new CryptKey(31, 55);
		second = new CryptKey(50, 57);
		thrid = new CryptKey(39, 58);
		keys = Arrays.toGenericArray(first, second, thrid);
		containerInt = new byte[4];
	}
	
	/**
	 * Method applyCryptor.
	 * @param buffer byte[]
	 * @param size int
	 */
	public void applyCryptor(byte buffer[], int size)
	{
		int changeLenght = getChangeLenght();
		int changeData = getChangeData();
		final int pre = size >= changeLenght ? changeLenght : size;
		final byte[] containerInt = getContainerInt();
		
		if (pre != 0)
		{
			if (pre > 0)
			{
				intToBytes(containerInt, changeData, 0);
				
				for (int j = 0; j < pre; j++)
				{
					buffer[j] ^= containerInt[(4 - changeLenght) + j];
				}
			}
			
			changeLenght -= pre;
			size -= pre;
		}
		
		final int offset = pre;
		final CryptKey[] keys = getKeys();
		final CryptKey first = getFirst();
		final CryptKey second = getSecond();
		final CryptKey thrid = getThrid();
		
		for (int i = 0; i < (size / 4); i++)
		{
			final int result = (first.getKey() & second.getKey()) | (thrid.getKey() & (first.getKey() | second.getKey()));
			
			for (int j = 0; j < 3; j++)
			{
				final CryptKey key = keys[j];
				
				if (result == key.getKey())
				{
					final long t1 = bytesToUInts(key.getBuffer(), key.getFirstPos() * 4);
					final long t2 = bytesToUInts(key.getBuffer(), key.getSecondPos() * 4);
					final long t3 = t1 > t2 ? t2 : t1;
					long sum = t1 + t2;
					sum = sum > 0xFFFFFFFFL ? ((int) t1 + (int) t2) & 0xFFFFFFFFL : sum;
					key.setSum(sum);
					key.setKey(t3 <= sum ? 0 : 1);
					key.setFirstPos((key.getFirstPos() + 1) % key.getSize());
					key.setSecondPos((key.getSecondPos() + 1) % key.getSize());
				}
				
				final long unsBuf = bytesToUInts(buffer, offset + (i * 4)) ^ key.getSum();
				intToBytes(buffer, unsBuf, offset + (i * 4));
			}
		}
		
		final int remain = size & 3;
		
		if (remain != 0)
		{
			final int result = (first.getKey() & second.getKey()) | (thrid.getKey() & (first.getKey() | second.getKey()));
			changeData = 0;
			
			for (int j = 0; j < 3; j++)
			{
				final CryptKey key = keys[j];
				
				if (result == key.getKey())
				{
					final long t1 = bytesToUInts(key.getBuffer(), key.getFirstPos() * 4);
					final long t2 = bytesToUInts(key.getBuffer(), key.getSecondPos() * 4);
					final long t3 = t1 > t2 ? t2 : t1;
					long sum = t1 + t2;
					sum = sum > 0xFFFFFFFFL ? ((int) t1 + (int) t2) & 0xFFFFFFFFL : sum;
					key.setSum(sum);
					key.setKey(t3 <= sum ? 0 : 1);
					key.setFirstPos((key.getFirstPos() + 1) % key.getSize());
					key.setSecondPos((key.getSecondPos() + 1) % key.getSize());
				}
				
				changeData ^= key.getSum();
			}
			
			intToBytes(containerInt, changeData, 0);
			
			for (int j = 0; j < remain; j++)
			{
				buffer[((size + pre) - remain) + j] ^= containerInt[j];
			}
			
			changeLenght = 4 - remain;
		}
		
		setChangeData(changeData);
		setChangeLenght(changeLenght);
	}
	
	/**
	 * Method fillKey.
	 * @param src byte[]
	 * @param dst byte[]
	 */
	private void fillKey(byte src[], byte dst[])
	{
		for (int i = 0; i < 680; i++)
		{
			dst[i] = src[i % 128];
		}
		
		dst[0] = -128;
	}
	
	/**
	 * Method generateKey.
	 * @param source byte[]
	 */
	public void generateKey(byte source[])
	{
		final byte buffer[] = new byte[680];
		fillKey(source, buffer);
		final Sha160 sha160 = new Sha160();
		
		for (int i = 0; i < 680; i += 20)
		{
			sha160.update(buffer, 0, 680);
			final byte digest2[] = sha160.digest();
			int j = i;
			
			for (int l = 0; j < (i + 20); l++)
			{
				buffer[j] = digest2[l];
				j++;
			}
		}
		
		first.setBuffer(subarray(buffer, 0, 220));
		second.setBuffer(subarray(buffer, 220, 448));
		thrid.setBuffer(subarray(buffer, 448, 680));
	}
	
	/**
	 * Method getChangeData.
	 * @return int
	 */
	public int getChangeData()
	{
		return changeData;
	}
	
	/**
	 * Method getChangeLenght.
	 * @return int
	 */
	public int getChangeLenght()
	{
		return changeLenght;
	}
	
	/**
	 * Method getContainerInt.
	 * @return byte[]
	 */
	public byte[] getContainerInt()
	{
		return containerInt;
	}
	
	/**
	 * Method getFirst.
	 * @return CryptKey
	 */
	public CryptKey getFirst()
	{
		return first;
	}
	
	/**
	 * Method getKeys.
	 * @return CryptKey[]
	 */
	public CryptKey[] getKeys()
	{
		return keys;
	}
	
	/**
	 * Method getSecond.
	 * @return CryptKey
	 */
	public CryptKey getSecond()
	{
		return second;
	}
	
	/**
	 * Method getThrid.
	 * @return CryptKey
	 */
	public CryptKey getThrid()
	{
		return thrid;
	}
	
	/**
	 * Method setChangeData.
	 * @param changeData int
	 */
	public void setChangeData(int changeData)
	{
		this.changeData = changeData;
	}
	
	/**
	 * Method setChangeLenght.
	 * @param changeLenght int
	 */
	public void setChangeLenght(int changeLenght)
	{
		this.changeLenght = changeLenght;
	}
}
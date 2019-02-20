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

import rlib.network.GameCrypt;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class TeraCrypt implements GameCrypt
{
	private Crypt decrypt;
	private Crypt encrypt;
	private final byte[][] temps;
	private CryptorState state;
	
	public TeraCrypt()
	{
		decrypt = new Crypt();
		encrypt = new Crypt();
		temps = new byte[4][];
		state = CryptorState.WAIT_FIRST_CLIENT_KEY;
	}
	
	public void clear()
	{
		state = CryptorState.WAIT_FIRST_CLIENT_KEY;
		decrypt = new Crypt();
		encrypt = new Crypt();
	}
	
	/**
	 * Method decrypt.
	 * @param data byte[]
	 * @param offset int
	 * @param length int
	 * @see rlib.network.GameCrypt#decrypt(byte[], int, int)
	 */
	@Override
	public void decrypt(byte[] data, int offset, int length)
	{
		switch (state)
		{
			case READY_TO_WORK:
				decrypt.applyCryptor(data, length);
				break;
			
			case WAIT_FIRST_CLIENT_KEY:
			{
				if (length == 128)
				{
					temps[0] = Arrays.copyOf(data, 128);
					state = CryptorState.WAIT_FIRST_SERVER_KEY;
				}
				
				break;
			}
			
			case WAIT_SECOND_CLIENT_KEY:
			{
				if (length == 128)
				{
					temps[2] = Arrays.copyOf(data, 128);
					state = CryptorState.WAIT_SECOND_SERCER_KEY;
				}
			}
		}
	}
	
	/**
	 * Method encrypt.
	 * @param data byte[]
	 * @param offset int
	 * @param length int
	 * @see rlib.network.GameCrypt#encrypt(byte[], int, int)
	 */
	@Override
	public void encrypt(byte[] data, int offset, int length)
	{
		switch (state)
		{
			case READY_TO_WORK:
				encrypt.applyCryptor(data, length);
				break;
			
			case WAIT_FIRST_SERVER_KEY:
			{
				if (length == 128)
				{
					temps[1] = Arrays.copyOf(data, 128);
					state = CryptorState.WAIT_SECOND_CLIENT_KEY;
				}
				
				break;
			}
			
			case WAIT_SECOND_SERCER_KEY:
			{
				if (length == 128)
				{
					temps[3] = Arrays.copyOf(data, 128);
					final byte[] firstTemp = new byte[128];
					final byte[] secondTemp = new byte[128];
					final byte[] cryptKey = new byte[128];
					Crypt.shiftKey(temps[1], firstTemp, 31, true);
					Crypt.xorKey(firstTemp, temps[0], secondTemp);
					Crypt.shiftKey(temps[2], firstTemp, 17, false);
					Crypt.xorKey(firstTemp, secondTemp, cryptKey);
					decrypt.generateKey(cryptKey);
					Crypt.shiftKey(temps[3], firstTemp, 79, true);
					decrypt.applyCryptor(firstTemp, 128);
					encrypt.generateKey(firstTemp);
					Arrays.clear(temps);
					state = CryptorState.READY_TO_WORK;
				}
			}
		}
	}
	
	/**
	 * Method getState.
	 * @return CryptorState
	 */
	public CryptorState getState()
	{
		return state;
	}
	
	/**
	 * Method setState.
	 * @param state CryptorState
	 */
	public void setState(CryptorState state)
	{
		this.state = state;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "TeraCrypt decrypt = " + decrypt + ", encrypt = " + encrypt;
	}
}
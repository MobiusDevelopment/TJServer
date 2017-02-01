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
package rlib.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

public class SymmetryCrypt
{
	private final Cipher ecipher = Cipher.getInstance("RC4");
	private final Cipher dcipher = Cipher.getInstance("RC4");
	private SecretKey secretKey;
	
	public SymmetryCrypt(String key) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException
	{
		final byte[] bytes = key.getBytes("UTF-8");
		secretKey = new SecretKey()
		{
			private static final long serialVersionUID = -8907627571317506056L;
			
			@Override
			public String getAlgorithm()
			{
				return "RC4";
			}
			
			@Override
			public byte[] getEncoded()
			{
				return bytes;
			}
			
			@Override
			public String getFormat()
			{
				return "RAW";
			}
		};
		ecipher.init(1, secretKey);
		dcipher.init(2, secretKey);
	}
	
	public void decrypt(byte[] in, int offset, int length, byte[] out) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
	{
		dcipher.doFinal(in, offset, length, out, offset);
	}
	
	public void encrypt(byte[] in, int offset, int length, byte[] out) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
	{
		ecipher.doFinal(in, offset, length, out, offset);
	}
	
}

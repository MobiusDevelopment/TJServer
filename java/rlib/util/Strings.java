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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rlib.logging.Loggers;

public class Strings
{
	public static final String EMPTY = "".intern();
	public static final String[] EMPTY_ARRAY = new String[0];
	public static final Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", 42);
	private static final MessageDigest hashMD5 = Strings.getHashMD5();
	
	public static boolean checkEmail(String email)
	{
		Matcher matcher = emailPattern.matcher(email);
		return matcher.matches();
	}
	
	private static MessageDigest getHashMD5()
	{
		try
		{
			return MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			Loggers.warning("Strings", e);
			return null;
		}
	}
	
	public static int length(String string)
	{
		if ((string == null) || string.isEmpty())
		{
			return 2;
		}
		return (string.length() * 2) + 2;
	}
	
	public static synchronized String passwordToHash(String password)
	{
		hashMD5.update(password.getBytes(), 0, password.length());
		return new BigInteger(1, hashMD5.digest()).toString(16);
	}
}

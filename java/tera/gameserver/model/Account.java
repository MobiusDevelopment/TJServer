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
package tera.gameserver.model;

import tera.gameserver.network.model.UserClient;

import rlib.util.Strings;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class Account implements Foldable
{
	private static final FoldablePool<Account> pool = Pools.newConcurrentFoldablePool(Account.class);
	public static final String EMPTY_ALLOW_IPS = "*";
	public static final String EMPTY_EMAIL = "null@null";
	
	/**
	 * Method valueOf.
	 * @param name String
	 * @param password String
	 * @param lastIP String
	 * @param comments String
	 * @return Account
	 */
	public static final Account valueOf(String name, String password, String lastIP, String comments)
	{
		Account account = pool.take();
		
		if (account == null)
		{
			account = new Account(name, password, lastIP, comments);
		}
		else
		{
			account.name = name;
			account.password = password;
			account.lastIP = lastIP;
			account.comments = comments;
		}
		
		return account;
	}
	
	/**
	 * Method valueOf.
	 * @param name String
	 * @param password String
	 * @param email String
	 * @param lastIP String
	 * @param allowIPs String
	 * @param comments String
	 * @param endBlock long
	 * @param endPay long
	 * @param accessLevel int
	 * @return Account
	 */
	public static final Account valueOf(String name, String password, String email, String lastIP, String allowIPs, String comments, long endBlock, long endPay, int accessLevel)
	{
		Account account = pool.take();
		
		if ("*".equals(allowIPs))
		{
			allowIPs = Account.EMPTY_ALLOW_IPS;
		}
		
		if (account == null)
		{
			account = new Account(name, password, email, lastIP, allowIPs, comments, endBlock, endPay, accessLevel);
		}
		else
		{
			account.name = name;
			account.password = password;
			account.email = email;
			account.lastIP = lastIP;
			account.allowIPs = allowIPs;
			account.comments = comments;
			account.endBlock = endBlock;
			account.endPay = endPay;
			account.accessLevel = accessLevel;
		}
		
		return account;
	}
	
	private UserClient client;
	private String name;
	private String lowerName;
	private String password;
	private String email;
	private String lastIP;
	private String allowIPs;
	private String comments;
	private long endBlock;
	private long endPay;
	private int accessLevel;
	private int bankId;
	
	/**
	 * Constructor for Account.
	 * @param name String
	 * @param password String
	 * @param lastIP String
	 * @param comments String
	 */
	public Account(String name, String password, String lastIP, String comments)
	{
		this.name = name;
		lowerName = (name == Strings.EMPTY) || name.isEmpty() ? Strings.EMPTY : name.toLowerCase();
		this.password = password;
		this.lastIP = lastIP;
		this.comments = comments;
		endBlock = -1L;
		endPay = -1L;
		accessLevel = 0;
		email = EMPTY_EMAIL;
		allowIPs = EMPTY_ALLOW_IPS;
	}
	
	/**
	 * Constructor for Account.
	 * @param name String
	 * @param password String
	 * @param email String
	 * @param lastIP String
	 * @param allowIPs String
	 * @param comments String
	 * @param endBlock long
	 * @param endPay long
	 * @param accessLevel int
	 */
	public Account(String name, String password, String email, String lastIP, String allowIPs, String comments, long endBlock, long endPay, int accessLevel)
	{
		this.name = name;
		lowerName = (name == Strings.EMPTY) || name.isEmpty() ? Strings.EMPTY : name.toLowerCase();
		this.password = password;
		this.email = email;
		this.lastIP = lastIP;
		
		if (allowIPs.equals(EMPTY_ALLOW_IPS))
		{
			this.allowIPs = EMPTY_ALLOW_IPS;
		}
		else
		{
			this.allowIPs = allowIPs;
		}
		
		this.comments = comments;
		this.endBlock = endBlock;
		this.endPay = endPay;
		this.accessLevel = accessLevel;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
		lowerName = null;
		password = null;
		email = EMPTY_EMAIL;
		lastIP = null;
		allowIPs = EMPTY_ALLOW_IPS;
		comments = null;
		endBlock = -1L;
		endPay = -1L;
		accessLevel = 0;
		client = null;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getAccessLevel.
	 * @return int
	 */
	public final int getAccessLevel()
	{
		return accessLevel;
	}
	
	/**
	 * Method getAllowIPs.
	 * @return String
	 */
	public final String getAllowIPs()
	{
		return allowIPs;
	}
	
	/**
	 * Method getBankId.
	 * @return int
	 */
	public final int getBankId()
	{
		return bankId;
	}
	
	/**
	 * Method getClient.
	 * @return UserClient
	 */
	public final UserClient getClient()
	{
		return client;
	}
	
	/**
	 * Method getComments.
	 * @return String
	 */
	public final String getComments()
	{
		return comments;
	}
	
	/**
	 * Method getEmail.
	 * @return String
	 */
	public final String getEmail()
	{
		return email;
	}
	
	/**
	 * Method getEndBlock.
	 * @return long
	 */
	public final long getEndBlock()
	{
		return endBlock;
	}
	
	/**
	 * Method getEndPay.
	 * @return long
	 */
	public final long getEndPay()
	{
		return endPay;
	}
	
	/**
	 * Method getLastActive.
	 * @return long
	 */
	public long getLastActive()
	{
		return client.getLastActive();
	}
	
	/**
	 * Method getLastIP.
	 * @return String
	 */
	public final String getLastIP()
	{
		return lastIP;
	}
	
	/**
	 * Method getLowerName.
	 * @return String
	 */
	public final String getLowerName()
	{
		return lowerName;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getPassword.
	 * @return String
	 */
	public final String getPassword()
	{
		return password;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	/**
	 * Method setAccessLevel.
	 * @param accessLevel int
	 */
	public final void setAccessLevel(int accessLevel)
	{
		this.accessLevel = accessLevel;
	}
	
	/**
	 * Method setAllowIPs.
	 * @param allowIPs String
	 */
	public final void setAllowIPs(String allowIPs)
	{
		if (allowIPs.equals(EMPTY_ALLOW_IPS))
		{
			allowIPs = EMPTY_ALLOW_IPS;
		}
		
		this.allowIPs = allowIPs;
	}
	
	/**
	 * Method setBankId.
	 * @param bankId int
	 */
	public final void setBankId(int bankId)
	{
		this.bankId = bankId;
	}
	
	/**
	 * Method setClient.
	 * @param client UserClient
	 */
	public final void setClient(UserClient client)
	{
		this.client = client;
	}
	
	/**
	 * Method setComments.
	 * @param comments String
	 */
	public final void setComments(String comments)
	{
		this.comments = comments;
	}
	
	/**
	 * Method setEmail.
	 * @param email String
	 */
	public final void setEmail(String email)
	{
		if (email.equals(EMPTY_EMAIL))
		{
			email = EMPTY_EMAIL;
		}
		
		this.email = email;
	}
	
	/**
	 * Method setEndBlock.
	 * @param endBlock long
	 */
	public final void setEndBlock(long endBlock)
	{
		this.endBlock = endBlock;
	}
	
	/**
	 * Method setEndPay.
	 * @param endPay long
	 */
	public final void setEndPay(long endPay)
	{
		this.endPay = endPay;
	}
	
	/**
	 * Method setLastIP.
	 * @param lastIP String
	 */
	public final void setLastIP(String lastIP)
	{
		this.lastIP = lastIP;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "Account  " + (name != null ? "name = " + name + ", " : "") + (password != null ? "password = " + password + ", " : "") + (email != null ? "email = " + email + ", " : "") + (lastIP != null ? "lastIP = " + lastIP + ", " : "") + (allowIPs != null ? "allowIPs = " + allowIPs + ", " : "") + (comments != null ? "comments = " + comments + ", " : "") + "endBlock = " + endBlock + ", endPay = " + endPay + ", accessLevel = " + accessLevel;
	}
}
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
package tera.gameserver.manager;

import tera.Config;
import tera.gameserver.model.Account;
import tera.gameserver.network.model.UserClient;
import tera.gameserver.network.serverpackets.AuthAttempt;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 11.03.2012
 */
public final class AccountManager
{
	static final Logger log = Loggers.getLogger(AccountManager.class);
	private static AccountManager instance;
	
	/**
	 * Method getInstance.
	 * @return AccountManager
	 */
	public static AccountManager getInstance()
	{
		if (instance == null)
		{
			instance = new AccountManager();
		}
		
		return instance;
	}
	
	private final Array<Account> accounts;
	
	private AccountManager()
	{
		accounts = Arrays.toArray(Account.class);
		final SafeTask task = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				final Array<Account> accounts = getAccounts();
				
				if (accounts.isEmpty())
				{
					return;
				}
				
				accounts.writeLock();
				
				try
				{
					final long currentTime = System.currentTimeMillis();
					final Account[] array = accounts.array();
					
					for (int i = 0, length = accounts.size(); i < length; i++)
					{
						final Account account = array[i];
						
						if (account == null)
						{
							accounts.fastRemove(i--);
							length--;
							continue;
						}
						
						final UserClient client = account.getClient();
						
						if (client == null)
						{
							removeAccount(account);
							i--;
							length--;
							log.info("empty client for account " + account.getName());
							continue;
						}
						
						if (!client.isConnected() || ((client.getLastActive() - currentTime) > 600000))
						{
							client.close();
							i--;
							length--;
						}
					}
				}
				
				finally
				{
					accounts.writeUnlock();
				}
			}
		};
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleGeneralAtFixedRate(task, 600000, 600000);
		log.info("initialized.");
	}
	
	/**
	 * Method closeAccount.
	 * @param account Account
	 */
	public void closeAccount(Account account)
	{
		removeAccount(account);
		log.info("close " + account.getName());
	}
	
	/**
	 * Method getAccount.
	 * @param name String
	 * @return Account
	 */
	public Account getAccount(String name)
	{
		final Array<Account> accounts = getAccounts();
		accounts.readLock();
		
		try
		{
			final Account[] array = accounts.array();
			
			for (int i = 0, length = accounts.size(); i < length; i++)
			{
				final Account account = array[i];
				
				if (name.equalsIgnoreCase(account.getName()))
				{
					return account;
				}
			}
			
			return null;
		}
		
		finally
		{
			accounts.readUnlock();
		}
	}
	
	/**
	 * Method getAccounts.
	 * @return Array<Account>
	 */
	public Array<Account> getAccounts()
	{
		return accounts;
	}
	
	/**
	 * Method login.
	 * @param accountName String
	 * @param password String
	 * @param client UserClient
	 */
	public final void login(String accountName, String password, UserClient client)
	{
		if ((accountName == null) || (password == null) || (password.length() < 6) || (password.length() > 45) || (accountName.length() < 4) || (accountName.length() > 14))
		{
			return;
		}
		
		final Account old = getAccount(accountName);
		
		if ((old != null) && old.getPassword().equals(password))
		{
			final UserClient oldClient = old.getClient();
			
			if (oldClient != client)
			{
				oldClient.close();
			}
		}
		
		final DataBaseManager dbManagaer = DataBaseManager.getInstance();
		Account account = dbManagaer.restoreAccount(accountName);
		
		if ((account == null) && Config.ACCOUNT_AUTO_CREATE)
		{
			account = dbManagaer.createAccount(accountName, password, null);
		}
		
		if (account == null)
		{
			log.info("not found account: " + accountName);
			client.sendPacket(AuthAttempt.getInstance(AuthAttempt.INCORRECT), true);
			return;
		}
		
		if (!account.getPassword().equals(password))
		{
			log.info("incorrect password for account: " + accountName);
			client.sendPacket(AuthAttempt.getInstance(AuthAttempt.INCORRECT), true);
			return;
		}
		
		if (account.getAccessLevel() < Config.ACCOUNT_MIN_ACCESS_LEVEL)
		{
			log.info("incorrect access level for account: " + accountName);
			client.sendPacket(AuthAttempt.getInstance(AuthAttempt.INCORRECT), true);
			return;
		}
		
		if ((account.getEndBlock() > 0L) && (System.currentTimeMillis() < account.getEndBlock()))
		{
			log.info("account banned: " + accountName);
			client.sendPacket(AuthAttempt.getInstance(AuthAttempt.INCORRECT), true);
			return;
		}
		
		if (Config.ACCOUNT_ONLY_PAID && (System.currentTimeMillis() > account.getEndPay()))
		{
			log.info("account not paid: " + accountName);
			client.sendPacket(AuthAttempt.getInstance(AuthAttempt.INCORRECT), true);
			return;
		}
		
		int bankId = dbManagaer.restoreAccountBank(accountName);
		
		if (bankId == -1)
		{
			synchronized (this)
			{
				bankId = dbManagaer.restoreAccountBank(accountName);
				
				if (bankId == -1)
				{
					dbManagaer.createAccountBank(accountName);
					bankId = dbManagaer.restoreAccountBank(accountName);
				}
			}
		}
		
		if (bankId == -1)
		{
			log.warning(new Exception("incorrect restor bank id for account " + accountName));
			client.sendPacket(AuthAttempt.getInstance(AuthAttempt.INCORRECT), true);
			return;
		}
		
		account.setBankId(bankId);
		dbManagaer.updateAccount(account, null);
		client.setAccount(account);
		account.setClient(client);
		accounts.add(account);
		log.info("authed " + accountName);
		client.sendPacket(AuthAttempt.getInstance(AuthAttempt.SUCCESSFUL), true);
	}
	
	/**
	 * Method removeAccount.
	 * @param account Account
	 */
	public void removeAccount(Account account)
	{
		accounts.fastRemove(account);
	}
}
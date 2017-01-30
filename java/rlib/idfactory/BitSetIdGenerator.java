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
package rlib.idfactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.BitSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rlib.database.ConnectFactory;
import rlib.database.DBUtils;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.array.Arrays;
import rlib.util.array.IntegerArray;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

public final class BitSetIdGenerator extends SafeTask implements IdGenerator
{
	private static final Logger log = Loggers.getLogger(BitSetIdGenerator.class);
	public static final int FIRST_ID = 268435456;
	public static final int LAST_ID = Integer.MAX_VALUE;
	public static final int FREE_ID_SIZE = 1879048191;
	private final ScheduledExecutorService executor;
	private final ConnectFactory connects;
	private volatile BitSet freeIds;
	private AtomicInteger freeIdCount;
	private AtomicInteger nextFreeId;
	private final String[][] tables;
	
	public BitSetIdGenerator(ConnectFactory connects, ScheduledExecutorService executor, String[][] tables)
	{
		this.executor = executor;
		this.connects = connects;
		this.tables = tables;
	}
	
	@Override
	public synchronized int getNextId()
	{
		int newID = this.nextFreeId.get();
		this.freeIds.set(newID);
		this.freeIdCount.decrementAndGet();
		int nextFree = this.freeIds.nextClearBit(newID);
		if (nextFree < 0)
		{
			nextFree = this.freeIds.nextClearBit(0);
		}
		if (nextFree < 0)
		{
			if (this.freeIds.size() < 1879048191)
			{
				this.increaseBitSetCapacity();
			}
			else
			{
				throw new NullPointerException("Ran out of valid Id's.");
			}
		}
		this.nextFreeId.set(nextFree);
		return newID + 268435456;
	}
	
	protected synchronized void increaseBitSetCapacity()
	{
		BitSet newBitSet = new BitSet(PrimeFinder.nextPrime((this.usedIds() * 11) / 10));
		newBitSet.or(this.freeIds);
		this.freeIds = newBitSet;
	}
	
	@Override
	public void prepare()
	{
		block14:
		{
			try
			{
				IntegerArray extractedIds;
				this.freeIds = new BitSet(PrimeFinder.nextPrime(100000));
				this.freeIds.clear();
				this.freeIdCount = new AtomicInteger(1879048191);
				if (this.tables == null)
				{
					break block14;
				}
				Table<IntKey, String> useIds = Tables.newIntegerTable();
				IntegerArray clearIds = Arrays.newIntegerArray();
				extractedIds = Arrays.newIntegerArray();
				Connection con = null;
				Statement statement = null;
				ResultSet rset = null;
				try
				{
					con = this.connects.getConnection();
					statement = con.createStatement();
					String[][] arrstring = this.tables;
					int n = arrstring.length;
					int n2 = 0;
					while (n2 < n)
					{
						String[] table = arrstring[n2];
						rset = statement.executeQuery("SELECT " + table[1] + " FROM " + table[0]);
						while (rset.next())
						{
							int objectId = rset.getInt(1);
							if (!useIds.containsKey(objectId))
							{
								extractedIds.add(objectId);
								useIds.put(objectId, table[0]);
								continue;
							}
							clearIds.add(objectId);
							log.warning("recurrence was found and '" + objectId + "' in the table `" + table[0] + "`, which is already in the table `" + useIds.get(objectId) + "`.");
						}
						if (!clearIds.isEmpty())
						{
							DBUtils.closeResultSet(rset);
							int[] arrn = clearIds.array();
							int n3 = arrn.length;
							int n4 = 0;
							while (n4 < n3)
							{
								int id = arrn[n4];
								statement.executeUpdate("DELETE FROM " + table[0] + " WHERE " + table[1] + " = " + id + " LIMIT 1");
								++n4;
							}
						}
						++n2;
					}
				}
				finally
				{
					DBUtils.closeDatabaseCSR(con, statement, rset);
				}
				int[] extracted = new int[extractedIds.size()];
				int i = 0;
				int length = extractedIds.size();
				while (i < length)
				{
					extracted[i] = extractedIds.get(i);
					++i;
				}
				log.info("extracted " + extracted.length + " ids.");
				Arrays.sort(extracted);
				int[] id = extracted;
				int n = id.length;
				length = 0;
				while (length < n)
				{
					int objectId = id[length];
					int id2 = objectId - 268435456;
					if (id2 < 0)
					{
						log.warning("objectId " + objectId + " in DB is less than minimum ID of " + 268435456 + ".");
					}
					else
					{
						this.freeIds.set(objectId - 268435456);
						this.freeIdCount.decrementAndGet();
					}
					++length;
				}
			}
			catch (Exception e)
			{
				log.warning(e);
			}
		}
		this.nextFreeId = new AtomicInteger(this.freeIds.nextClearBit(0));
		log.info(String.valueOf(this.freeIds.size()) + " id's available.");
		this.executor.scheduleAtFixedRate(this, 300000, 300000, TimeUnit.MILLISECONDS);
	}
	
	protected synchronized boolean reachingBitSetCapacity()
	{
		if (PrimeFinder.nextPrime((this.usedIds() * 11) / 10) > this.freeIds.size())
		{
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized void releaseId(int objectId)
	{
		if ((objectId - 268435456) < 0)
		{
			log.warning("release objectID " + objectId + " failed (< " + 268435456 + ")");
		}
		else
		{
			this.freeIds.clear(objectId - 268435456);
			this.freeIdCount.incrementAndGet();
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (this.reachingBitSetCapacity())
		{
			this.increaseBitSetCapacity();
		}
	}
	
	public synchronized int size()
	{
		return this.freeIdCount.get();
	}
	
	@Override
	public int usedIds()
	{
		return this.size() - 268435456;
	}
}

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
package rlib.util.table;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import rlib.concurrent.Locks;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.array.IntegerArray;
import rlib.util.array.LongArray;

public final class ConcurrentTable<K, V> implements Table<K, V>
{
	private final Lock readLock;
	private final Lock writeLock;
	private volatile Entry<V>[] table;
	private volatile int threshold = 12;
	private volatile int size;
	private volatile float loadFactor = 0.75f;
	
	public ConcurrentTable()
	{
		ReadWriteLock readWriteLock = Locks.newRWLock();
		this.readLock = readWriteLock.readLock();
		this.writeLock = readWriteLock.writeLock();
		this.table = new Entry[16];
	}
	
	private void addEntry(int hash, int key, V value, int index)
	{
		this.writeLock.lock();
		try
		{
			Entry<V> entry = this.table[index];
			this.table[index] = new Entry(key, value, entry, hash);
			if (this.size++ >= this.threshold)
			{
				this.resize(2 * this.table.length);
			}
		}
		finally
		{
			this.writeLock.unlock();
		}
	}
	
	@Override
	public void apply(FuncKeyValue<K, V> func)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void apply(FuncValue<V> func)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void clear()
	{
		this.writeLock.lock();
		try
		{
			Arrays.clear(this.table);
			this.size = 0;
		}
		finally
		{
			this.writeLock.unlock();
		}
	}
	
	@Override
	public boolean containsKey(int key)
	{
		if (this.getEntry(key) != null)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean containsKey(K key)
	{
		return this.containsKey(key == null ? 0 : key.hashCode());
	}
	
	@Override
	public boolean containsKey(long key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public boolean containsValue(V value)
	{
		this.readLock.lock();
		try
		{
			int i = 0;
			int length = this.table.length;
			while (i < length)
			{
				Entry element;
				Entry entry = element = this.table[i];
				while (entry != null)
				{
					if (value.equals(entry.value))
					{
						return true;
					}
					entry = entry.next;
				}
				++i;
			}
			return false;
		}
		finally
		{
			this.readLock.unlock();
		}
	}
	
	@Override
	public void finalyze()
	{
		if (this.size > 0)
		{
			this.clear();
		}
	}
	
	@Override
	public V get(int key)
	{
		int hash = Tables.hash(key);
		this.readLock.lock();
		try
		{
			Entry entry = this.table[Tables.indexFor(hash, this.table.length)];
			while (entry != null)
			{
				if ((entry.hash == hash) && (entry.key == key))
				{
					Object object = entry.value;
					return (V) object;
				}
				entry = entry.next;
			}
			return null;
		}
		finally
		{
			this.readLock.unlock();
		}
	}
	
	@Override
	public V get(K key)
	{
		return this.get((key == null ? 0 : key.hashCode()));
	}
	
	@Override
	public V get(long key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	private Entry<V> getEntry(int key)
	{
		int hash = Tables.hash(key);
		this.readLock.lock();
		try
		{
			Entry entry = this.table[Tables.indexFor(hash, this.table.length)];
			while (entry != null)
			{
				if (entry.key == key)
				{
					Entry entry2 = entry;
					return entry2;
				}
				entry = entry.next;
			}
			return null;
		}
		finally
		{
			this.readLock.unlock();
		}
	}
	
	@Override
	public TableType getType()
	{
		return TableType.DEPRECATED;
	}
	
	@Override
	public boolean isEmpty()
	{
		if (this.size == 0)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public Iterator<V> iterator()
	{
		return new TableIterator(this);
	}
	
	@Override
	public Array<K> keyArray(Array<K> container)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public IntegerArray keyIntegerArray(IntegerArray container)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public LongArray keyLongArray(LongArray container)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void moveTo(Table<K, V> table)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public V put(int key, V value)
	{
		int hash = Tables.hash(key);
		this.writeLock.lock();
		try
		{
			int i = Tables.indexFor(hash, this.table.length);
			Entry entry = this.table[i];
			while (entry != null)
			{
				if (entry.key == key)
				{
					Object oldValue = entry.value;
					Entry.access$5(entry, value);
					Object object = oldValue;
					return (V) object;
				}
				entry = entry.next;
			}
			this.addEntry(hash, key, value, i);
			return null;
		}
		finally
		{
			this.writeLock.unlock();
		}
	}
	
	@Override
	public V put(K key, V value)
	{
		return this.put((key == null ? 0 : key.hashCode()), value);
	}
	
	@Override
	public V put(long key, V value)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void put(Table<K, V> table)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void readLock()
	{
		this.readLock.lock();
	}
	
	@Override
	public void readUnlock()
	{
		this.readLock.unlock();
	}
	
	@Override
	public void reinit()
	{
	}
	
	@Override
	public V remove(int key)
	{
		this.writeLock.lock();
		try
		{
			Entry<V> e = this.removeEntryForKey(key);
			Object object = e == null ? null : e.value;
			return (V) object;
		}
		finally
		{
			this.writeLock.unlock();
		}
	}
	
	@Override
	public V remove(K key)
	{
		return this.remove((key == null ? 0 : key.hashCode()));
	}
	
	@Override
	public V remove(long key)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	private Entry<V> removeEntryForKey(int key)
	{
		int hash = Tables.hash(key);
		int i = Tables.indexFor(hash, this.table.length);
		this.writeLock.lock();
		try
		{
			Entry prev;
			Entry entry = prev = this.table[i];
			while (entry != null)
			{
				Entry next = entry.next;
				if ((entry.hash == hash) && (entry.key == key))
				{
					--this.size;
					if (prev == entry)
					{
						this.table[i] = next;
					}
					else
					{
						Entry.access$6(prev, next);
					}
					Entry entry2 = entry;
					return entry2;
				}
				prev = entry;
				entry = next;
			}
			Entry entry3 = entry;
			return entry3;
		}
		finally
		{
			this.writeLock.unlock();
		}
	}
	
	private void resize(int newLength)
	{
		this.writeLock.lock();
		try
		{
			Entry<V>[] oldTable = this.table;
			int oldLength = oldTable.length;
			if (oldLength == 1073741824)
			{
				this.threshold = Integer.MAX_VALUE;
				return;
			}
			Entry[] newTable = new Entry[newLength];
			this.transfer(newTable);
			this.table = newTable;
			this.threshold = (int) (newLength * this.loadFactor);
		}
		finally
		{
			this.writeLock.unlock();
		}
	}
	
	@Override
	public int size()
	{
		return this.size;
	}
	
	private void transfer(Entry<V>[] newTable)
	{
		Entry<V>[] original = this.table;
		int newCapacity = newTable.length;
		int j = 0;
		int length = original.length;
		while (j < length)
		{
			Entry entry = original[j];
			if (entry != null)
			{
				Entry next;
				original[j] = null;
				do
				{
					next = entry.next;
					int i = Tables.indexFor(entry.hash, newCapacity);
					Entry.access$6(entry, newTable[i]);
					newTable[i] = entry;
				}
				while ((entry = next) != null);
			}
			++j;
		}
	}
	
	@Override
	public Array<V> values(Array<V> container)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void writeLock()
	{
		this.writeLock.lock();
	}
	
	@Override
	public void writeUnlock()
	{
		this.writeLock.unlock();
	}
	
	private static final class Entry<V>
	{
		private volatile V value;
		private volatile Entry<V> next;
		private final int key;
		private final int hash;
		
		private Entry(int key, V value, Entry<V> next, int hash)
		{
			this.key = key;
			this.value = value;
			this.next = next;
			this.hash = hash;
		}
		
		@Override
		public boolean equals(Object object)
		{
			if (object.getClass() != Entry.class)
			{
				return false;
			}
			Entry entry = (Entry) object;
			if ((this.key == entry.key) && (this.value == entry.value))
			{
				return true;
			}
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return this.key ^ (this.value == null ? 0 : this.value.hashCode());
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(this.key) + " = " + this.value;
		}
		
		static /* synthetic */ void access$5(Entry entry, Object object)
		{
			entry.value = object;
		}
		
		static /* synthetic */ void access$6(Entry entry, Entry entry2)
		{
			entry.next = entry2;
		}
	}
	
	private final class TableIterator implements Iterator<V>
	{
		private Entry<V> next;
		private int index;
		private Entry<V> current;
		final /* synthetic */ ConcurrentTable this$0;
		
		private TableIterator(ConcurrentTable concurrentTable)
		{
			this.this$0 = concurrentTable;
			if (concurrentTable.size > 0)
			{
				concurrentTable.readLock.lock();
				try
				{
					while ((this.index < concurrentTable.table.length) && ((this.next = concurrentTable.table[this.index++]) == null))
					{
					}
				}
				finally
				{
					concurrentTable.readLock.unlock();
				}
			}
		}
		
		@Override
		public boolean hasNext()
		{
			if (this.next != null)
			{
				return true;
			}
			return false;
		}
		
		@Override
		public V next()
		{
			return this.nextEntry().value;
		}
		
		private Entry<V> nextEntry()
		{
			Entry<V> entry;
			entry = this.next;
			if (entry == null)
			{
				throw new NoSuchElementException();
			}
			this.this$0.readLock.lock();
			try
			{
				this.next = entry.next;
				if (this.next == null)
				{
					while ((this.index < this.this$0.table.length) && ((this.next = this.this$0.table[this.index++]) == null))
					{
					}
				}
			}
			finally
			{
				this.this$0.readLock.unlock();
			}
			this.current = entry;
			return entry;
		}
		
		@Override
		public void remove()
		{
			if (this.current == null)
			{
				throw new IllegalStateException();
			}
			int key = this.current.key;
			this.current = null;
			this.this$0.removeEntryForKey(key);
		}
	}
}

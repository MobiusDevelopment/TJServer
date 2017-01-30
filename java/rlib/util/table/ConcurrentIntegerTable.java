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

import rlib.concurrent.AsynReadSynWriteLock;
import rlib.concurrent.Locks;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.array.IntegerArray;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

public class ConcurrentIntegerTable<V> extends AbstractTable<IntKey, V>
{
	private final FoldablePool<Entry<V>> entryPool;
	private final AsynReadSynWriteLock locker;
	private volatile Entry<V>[] table;
	private volatile int threshold;
	private volatile int size;
	private volatile float loadFactor;
	
	protected ConcurrentIntegerTable()
	{
		this(0.75f, 16);
	}
	
	protected ConcurrentIntegerTable(float loadFactor)
	{
		this(loadFactor, 16);
	}
	
	protected ConcurrentIntegerTable(float loadFactor, int initCapacity)
	{
		this.loadFactor = loadFactor;
		this.threshold = (int) (initCapacity * loadFactor);
		this.size = 0;
		this.table = new Entry[16];
		this.entryPool = Pools.newFoldablePool(Entry.class);
		this.locker = Locks.newARSWLock();
	}
	
	protected ConcurrentIntegerTable(int initCapacity)
	{
		this(0.75f, initCapacity);
	}
	
	private final void addEntry(int hash, int key, V value, int index)
	{
		Entry<V>[] table = this.table();
		Entry<V> entry = table[index];
		Entry<V> newEntry = this.entryPool.take();
		if (newEntry == null)
		{
			newEntry = new Entry();
		}
		newEntry.set(hash, key, value, entry);
		table[index] = newEntry;
		if (this.size++ >= this.threshold)
		{
			this.resize(2 * table.length);
		}
	}
	
	@Override
	public void apply(FuncKeyValue<IntKey, V> func)
	{
		throw new IllegalArgumentException("not supported.");
	}
	
	@Override
	public void apply(FuncValue<V> func)
	{
		this.readLock();
		try
		{
			Entry<V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<V> entry = table[i];
				while (entry != null)
				{
					func.apply(entry.getValue());
					entry = entry.getNext();
				}
				++i;
			}
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final void clear()
	{
		this.writeLock();
		try
		{
			Object[] table = this.table();
			Entry next = null;
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry entry = (Entry) table[i];
				while (entry != null)
				{
					next = entry.getNext();
					this.entryPool.put(entry);
					entry = next;
				}
				++i;
			}
			Arrays.clear(table);
			this.size = 0;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public final boolean containsKey(int key)
	{
		this.readLock();
		try
		{
			boolean bl = this.getEntry(key) != null;
			return bl;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final boolean containsValue(V value)
	{
		this.readLock();
		try
		{
			if (value == null)
			{
				throw new NullPointerException("value is null.");
			}
			Entry<V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry element;
				Entry entry = element = table[i];
				while (entry != null)
				{
					if (value.equals(entry.getValue()))
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
			this.readUnlock();
		}
	}
	
	@Override
	public final void finalyze()
	{
		if (this.size() > 0)
		{
			this.clear();
		}
	}
	
	@Override
	public final V get(int key)
	{
		this.readLock();
		try
		{
			Entry<V> entry = this.getEntry(key);
			V v = entry == null ? null : (V) entry.getValue();
			return v;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	private final Entry<V> getEntry(int key)
	{
		int hash = AbstractTable.hash(key);
		Entry<V>[] table = this.table();
		Entry<V> entry = table[AbstractTable.indexFor(hash, table.length)];
		while (entry != null)
		{
			if ((entry.getHash() == hash) && (key == entry.getKey()))
			{
				return entry;
			}
			entry = entry.getNext();
		}
		return null;
	}
	
	@Override
	public TableType getType()
	{
		return TableType.INTEGER;
	}
	
	@Override
	public final Iterator<V> iterator()
	{
		return new TableIterator(this);
	}
	
	@Override
	public IntegerArray keyIntegerArray(IntegerArray container)
	{
		this.readLock();
		try
		{
			Entry<V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<V> entry = table[i];
				while (entry != null)
				{
					container.add(entry.getKey());
					entry = entry.getNext();
				}
				++i;
			}
			IntegerArray integerArray = container;
			return integerArray;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public void moveTo(Table<IntKey, V> table)
	{
		if (this.getType() != table.getType())
		{
			throw new IllegalArgumentException("incorrect table type.");
		}
		this.readLock();
		try
		{
			if (this.isEmpty())
			{
				return;
			}
			Entry<V>[] entryes = this.table();
			int i = 0;
			int length = entryes.length;
			while (i < length)
			{
				Entry<V> entry = entryes[i];
				while (entry != null)
				{
					table.put(entry.getKey(), entry.getValue());
					entry = entry.getNext();
				}
				++i;
			}
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public final V put(int key, V value)
	{
		this.writeLock();
		try
		{
			int hash = AbstractTable.hash(key);
			Entry<V>[] table = this.table();
			int i = AbstractTable.indexFor(hash, table.length);
			Entry<V> entry = table[i];
			while (entry != null)
			{
				if ((entry.getHash() == hash) && (key == entry.getKey()))
				{
					V v = entry.setValue(value);
					return v;
				}
				entry = entry.getNext();
			}
			this.addEntry(hash, key, value, i);
			return null;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	@Override
	public void readLock()
	{
		this.locker.readLock();
	}
	
	@Override
	public void readUnlock()
	{
		this.locker.readUnlock();
	}
	
	@Override
	public final V remove(int key)
	{
		this.writeLock();
		try
		{
			Entry<V> old = this.removeEntryForKey(key);
			V value = old == null ? null : (V) old.getValue();
			this.entryPool.put(old);
			V v = value;
			return v;
		}
		finally
		{
			this.writeUnlock();
		}
	}
	
	private final Entry<V> removeEntryForKey(int key)
	{
		Entry<V> prev;
		int hash = AbstractTable.hash(key);
		Entry<V>[] table = this.table();
		int i = AbstractTable.indexFor(hash, table.length);
		Entry<V> entry = prev = table[i];
		while (entry != null)
		{
			Entry<V> next = entry.getNext();
			if ((entry.getHash() == hash) && (key == entry.getKey()))
			{
				--this.size;
				if (prev == entry)
				{
					table[i] = next;
				}
				else
				{
					prev.setNext(next);
				}
				return entry;
			}
			prev = entry;
			entry = next;
		}
		return entry;
	}
	
	private final void resize(int newLength)
	{
		Entry<V>[] oldTable = this.table();
		int oldLength = oldTable.length;
		if (oldLength >= 1073741824)
		{
			this.threshold = Integer.MAX_VALUE;
			return;
		}
		Entry[] newTable = new Entry[newLength];
		this.transfer(newTable);
		this.table = newTable;
		this.threshold = (int) (newLength * this.loadFactor);
	}
	
	@Override
	public final int size()
	{
		return this.size;
	}
	
	private final Entry<V>[] table()
	{
		return this.table;
	}
	
	@Override
	public final String toString()
	{
		this.readLock();
		try
		{
			StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
			builder.append(" size = ").append(this.size).append(" : ");
			Entry<V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<V> entry = table[i];
				while (entry != null)
				{
					builder.append("[").append(entry.getKey()).append(" - ").append(entry.getValue()).append("]");
					builder.append(", ");
					entry = entry.getNext();
				}
				++i;
			}
			if (this.size > 0)
			{
				builder.replace(builder.length() - 2, builder.length(), ".");
			}
			String string = builder.toString();
			return string;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	private final void transfer(Entry<V>[] newTable)
	{
		Entry<V>[] original = this.table;
		int newCapacity = newTable.length;
		int j = 0;
		int length = original.length;
		while (j < length)
		{
			Entry<V> entry = original[j];
			if (entry != null)
			{
				Entry<V> next;
				do
				{
					next = entry.getNext();
					int i = AbstractTable.indexFor(entry.getHash(), newCapacity);
					entry.setNext(newTable[i]);
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
		this.readLock();
		try
		{
			Entry<V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<V> entry = table[i];
				while (entry != null)
				{
					container.add(entry.getValue());
					entry = entry.getNext();
				}
				++i;
			}
			Array<V> array = container;
			return array;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	@Override
	public void writeLock()
	{
		this.locker.writeLock();
	}
	
	@Override
	public void writeUnlock()
	{
		this.locker.writeUnlock();
	}
	
	private static final class Entry<V> implements Foldable
	{
		private Entry<V> next;
		private V value;
		private int hash;
		private int key;
		
		private Entry()
		{
		}
		
		@Override
		public boolean equals(Object object)
		{
			int secondKey;
			V secondValue;
			V firstValue;
			if ((object == null) || (object.getClass() != Entry.class))
			{
				return false;
			}
			Entry entry = (Entry) object;
			int firstKey = this.getKey();
			if ((firstKey == (secondKey = entry.getKey())) && (((firstValue = this.getValue()) == (secondValue = (V) entry.getValue())) || ((firstValue != null) && firstValue.equals(secondValue))))
			{
				return true;
			}
			return false;
		}
		
		@Override
		public void finalyze()
		{
			this.value = null;
			this.next = null;
			this.key = 0;
			this.hash = 0;
		}
		
		public int getHash()
		{
			return this.hash;
		}
		
		public int getKey()
		{
			return this.key;
		}
		
		public Entry<V> getNext()
		{
			return this.next;
		}
		
		public V getValue()
		{
			return this.value;
		}
		
		@Override
		public final int hashCode()
		{
			return this.key ^ (this.value == null ? 0 : this.value.hashCode());
		}
		
		@Override
		public void reinit()
		{
			this.hash = 0;
		}
		
		public void set(int hash, int key, V value, Entry<V> next)
		{
			this.value = value;
			this.next = next;
			this.key = key;
			this.hash = hash;
		}
		
		public void setNext(Entry<V> next)
		{
			this.next = next;
		}
		
		public V setValue(V value)
		{
			V old = this.getValue();
			this.value = value;
			return old;
		}
		
		@Override
		public final String toString()
		{
			return "Entry : " + this.key + " = " + this.value;
		}
	}
	
	private final class TableIterator implements Iterator<V>
	{
		private Entry<V> next;
		private Entry<V> current;
		private int index;
		final /* synthetic */ ConcurrentIntegerTable this$0;
		
		private TableIterator(ConcurrentIntegerTable concurrentIntegerTable)
		{
			this.this$0 = concurrentIntegerTable;
			Entry[] table = concurrentIntegerTable.table();
			if (concurrentIntegerTable.size > 0)
			{
				while ((this.index < table.length) && ((this.next = table[this.index++]) == null))
				{
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
			return this.nextEntry().getValue();
		}
		
		private Entry<V> nextEntry()
		{
			Entry[] table = this.this$0.table();
			Entry<V> entry = this.next;
			if (entry == null)
			{
				throw new NoSuchElementException();
			}
			this.next = entry.getNext();
			if (this.next == null)
			{
				while ((this.index < table.length) && ((this.next = table[this.index++]) == null))
				{
				}
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
			int key = this.current.getKey();
			this.current = null;
			this.this$0.removeEntryForKey(key);
		}
	}
}

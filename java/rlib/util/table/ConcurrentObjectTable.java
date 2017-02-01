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
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

public class ConcurrentObjectTable<K, V> extends AbstractTable<K, V>
{
	private final FoldablePool<Entry<K, V>> entryPool;
	private final AsynReadSynWriteLock locker;
	private volatile Entry<K, V>[] table;
	private volatile int threshold;
	volatile int size;
	private volatile float loadFactor;
	
	protected ConcurrentObjectTable()
	{
		this(0.75f, 16);
	}
	
	protected ConcurrentObjectTable(float loadFactor)
	{
		this(loadFactor, 16);
	}
	
	@SuppressWarnings("unchecked")
	protected ConcurrentObjectTable(float loadFactor, int initCapacity)
	{
		this.loadFactor = loadFactor;
		this.threshold = (int) (initCapacity * loadFactor);
		this.size = 0;
		this.table = new Entry[16];
		this.entryPool = Pools.newFoldablePool(Entry.class);
		this.locker = Locks.newARSWLock();
	}
	
	protected ConcurrentObjectTable(int initCapacity)
	{
		this(0.75f, initCapacity);
	}
	
	private final void addEntry(int hash, K key, V value, int index)
	{
		Entry<K, V>[] table = this.table();
		Entry<K, V> entry = table[index];
		Entry<K, V> newEntry = this.entryPool.take();
		if (newEntry == null)
		{
			newEntry = new Entry<>();
		}
		newEntry.set(hash, key, value, entry);
		table[index] = newEntry;
		if (this.size++ >= this.threshold)
		{
			this.resize(2 * table.length);
		}
	}
	
	@Override
	public void apply(FuncKeyValue<K, V> func)
	{
		this.readLock();
		try
		{
			Entry<K, V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<K, V> entry = table[i];
				while (entry != null)
				{
					func.apply(entry.getKey(), entry.getValue());
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
	public void apply(FuncValue<V> func)
	{
		this.readLock();
		try
		{
			Entry<K, V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<K, V> entry = table[i];
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
	
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
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
	public final boolean containsKey(K key)
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
			Entry<K, V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<?, ?> entry = table[i];
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
	public final V get(K key)
	{
		this.readLock();
		try
		{
			if (key == null)
			{
				throw new NullPointerException("key is null.");
			}
			Entry<K, V> entry = this.getEntry(key);
			V v = entry == null ? null : (V) entry.getValue();
			return v;
		}
		finally
		{
			this.readUnlock();
		}
	}
	
	private final Entry<K, V> getEntry(K key)
	{
		int hash = AbstractTable.hash(key.hashCode());
		Entry<K, V>[] table = this.table();
		Entry<K, V> entry = table[AbstractTable.indexFor(hash, table.length)];
		while (entry != null)
		{
			if ((entry.getHash() == hash) && key.equals(entry.getKey()))
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
		return TableType.OBJECT;
	}
	
	@Override
	public final Iterator<V> iterator()
	{
		return new TableIterator(this);
	}
	
	@Override
	public final Array<K> keyArray(Array<K> container)
	{
		this.readLock();
		try
		{
			Entry<K, V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<K, V> entry = table[i];
				while (entry != null)
				{
					container.add(entry.getKey());
					entry = entry.getNext();
				}
				++i;
			}
		}
		finally
		{
			this.readUnlock();
		}
		return container;
	}
	
	@Override
	public void moveTo(Table<K, V> table)
	{
		if (this.getType() != table.getType())
		{
			throw new IllegalArgumentException("incorrect table type.");
		}
		this.readLock();
		try
		{
			if (isEmpty())
			{
				return;
			}
			Entry<K, V>[] entryes = this.table();
			int i = 0;
			int length = entryes.length;
			while (i < length)
			{
				Entry<K, V> entry = entryes[i];
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
	public final V put(K key, V value)
	{
		if (key == null)
		{
			throw new NullPointerException("key is null.");
		}
		this.writeLock();
		try
		{
			int hash = AbstractTable.hash(key.hashCode());
			Entry<K, V>[] table = this.table();
			int i = AbstractTable.indexFor(hash, table.length);
			Entry<K, V> entry = table[i];
			while (entry != null)
			{
				if ((entry.getHash() == hash) && key.equals(entry.getKey()))
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
	public final void readLock()
	{
		this.locker.readLock();
	}
	
	@Override
	public final void readUnlock()
	{
		this.locker.readUnlock();
	}
	
	@Override
	public final V remove(K key)
	{
		if (key == null)
		{
			throw new NullPointerException("key is null.");
		}
		this.writeLock();
		try
		{
			Entry<K, V> old = this.removeEntryForKey(key);
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
	
	final Entry<K, V> removeEntryForKey(K key)
	{
		Entry<K, V> prev;
		int hash = AbstractTable.hash(key.hashCode());
		Entry<K, V>[] table = this.table();
		int i = AbstractTable.indexFor(hash, table.length);
		Entry<K, V> entry = prev = table[i];
		while (entry != null)
		{
			Entry<K, V> next = entry.getNext();
			if ((entry.getHash() == hash) && key.equals(entry.getKey()))
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
	
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	private final void resize(int newLength)
	{
		Entry<K, V>[] oldTable = this.table();
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
	
	final Entry<K, V>[] table()
	{
		return this.table;
	}
	
	@Override
	public final String toString()
	{
		this.readLock();
		try
		{
			StringBuilder builder = new StringBuilder(getClass().getSimpleName());
			builder.append(" size = ").append(this.size).append(" : ");
			Entry<K, V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<K, V> entry = table[i];
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
	
	private final void transfer(Entry<K, V>[] newTable)
	{
		Entry<K, V>[] original = this.table;
		int newCapacity = newTable.length;
		int j = 0;
		int length = original.length;
		while (j < length)
		{
			Entry<K, V> entry = original[j];
			if (entry != null)
			{
				Entry<K, V> next;
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
			Entry<K, V>[] table = this.table();
			int i = 0;
			int length = table.length;
			while (i < length)
			{
				Entry<K, V> entry = table[i];
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
	public final void writeLock()
	{
		this.locker.writeLock();
	}
	
	@Override
	public final void writeUnlock()
	{
		this.locker.writeUnlock();
	}
	
	private static final class Entry<K, V> implements Foldable
	{
		Entry<K, V> next;
		private K key;
		private V value;
		private int hash;
		
		Entry()
		{
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object object)
		{
			K secondKey;
			V secondValue;
			V firstValue;
			if ((object == null) || (object.getClass() != Entry.class))
			{
				return false;
			}
			Entry<?, ?> entry = (Entry<?, ?>) object;
			K firstKey = getKey();
			secondKey = (K) entry.getKey();
			if (((firstKey == secondKey) || ((firstKey != null) && firstKey.equals(secondKey))) && (((firstValue = getValue()) == (secondValue = (V) entry.getValue())) || ((firstValue != null) && firstValue.equals(secondValue))))
			{
				return true;
			}
			return false;
		}
		
		@Override
		public void finalyze()
		{
			this.key = null;
			this.value = null;
			this.next = null;
			this.hash = 0;
		}
		
		public int getHash()
		{
			return this.hash;
		}
		
		public K getKey()
		{
			return this.key;
		}
		
		public Entry<K, V> getNext()
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
			return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
		}
		
		@Override
		public void reinit()
		{
			this.hash = 0;
		}
		
		public void set(int hash, K key, V value, Entry<K, V> next)
		{
			this.value = value;
			this.next = next;
			this.key = key;
			this.hash = hash;
		}
		
		public void setNext(Entry<K, V> next)
		{
			this.next = next;
		}
		
		public V setValue(V value)
		{
			V old = getValue();
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
		private Entry<K, V> next;
		private Entry<K, V> current;
		private int index;
		@SuppressWarnings("rawtypes")
		final /* synthetic */ ConcurrentObjectTable this$0;
		
		@SuppressWarnings(
		{
			"rawtypes",
			"unchecked"
		})
		TableIterator(ConcurrentObjectTable concurrentObjectTable)
		{
			this.this$0 = concurrentObjectTable;
			Entry[] table = concurrentObjectTable.table();
			if (concurrentObjectTable.size > 0)
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
		
		@SuppressWarnings(
		{
			"rawtypes",
			"unchecked"
		})
		private Entry<K, V> nextEntry()
		{
			Entry[] table = this.this$0.table();
			Entry<K, V> entry = this.next;
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
		
		@SuppressWarnings("unchecked")
		@Override
		public void remove()
		{
			if (this.current == null)
			{
				throw new IllegalStateException();
			}
			K key = this.current.getKey();
			this.current = null;
			this.this$0.removeEntryForKey(key);
		}
	}
}

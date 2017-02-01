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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import rlib.util.array.Array;
import rlib.util.array.IntegerArray;
import rlib.util.array.LongArray;

public final class FastTable<K, V> implements Table<K, V>
{
	private int threshold = 12;
	private final float loadFactor = 0.75f;
	private int size = 0;
	private Entry<V>[] table = new Entry[16];
	
	private static int hash(int hash)
	{
		hash ^= (hash >>> 20) ^ (hash >>> 12);
		return hash ^ (hash >>> 7) ^ (hash >>> 4);
	}
	
	private static int indexFor(int hash, int length)
	{
		return hash & (length - 1);
	}
	
	protected FastTable()
	{
	}
	
	private void addEntry(int hash, int key, V value, int index)
	{
		Entry<V> entry = this.table[index];
		this.table[index] = new Entry(key, value, entry, hash);
		if (this.size++ >= this.threshold)
		{
			this.resize(2 * this.table.length);
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
		int i = 0;
		int length = this.table.length;
		while (i < length)
		{
			this.table[i] = null;
			++i;
		}
		this.size = 0;
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
		int hash = FastTable.hash(key);
		Entry entry = this.table[FastTable.indexFor(hash, this.table.length)];
		while (entry != null)
		{
			if ((entry.hash == hash) && (entry.key == key))
			{
				return (V) entry.value;
			}
			entry = entry.next;
		}
		return null;
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
		int hash = FastTable.hash(key);
		Entry entry = this.table[FastTable.indexFor(hash, this.table.length)];
		while (entry != null)
		{
			if (entry.key == key)
			{
				return entry;
			}
			entry = entry.next;
		}
		return null;
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
	public final Iterator<V> iterator()
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
		int hash = FastTable.hash(key);
		int i = FastTable.indexFor(hash, this.table.length);
		Entry entry = this.table[i];
		while (entry != null)
		{
			if (entry.key == key)
			{
				Object oldValue = entry.value;
				Entry.access$5(entry, value);
				return (V) oldValue;
			}
			entry = entry.next;
		}
		this.addEntry(hash, key, value, i);
		return null;
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
	}
	
	@Override
	public void readUnlock()
	{
	}
	
	@Override
	public void reinit()
	{
	}
	
	@Override
	public V remove(int key)
	{
		Entry<V> old = this.removeEntryForKey(key);
		return old == null ? null : old.value;
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
		Entry prev;
		int hash = FastTable.hash(key);
		int i = FastTable.indexFor(hash, this.table.length);
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
				return entry;
			}
			prev = entry;
			entry = next;
		}
		return entry;
	}
	
	private void resize(int newLength)
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
	
	@Override
	public int size()
	{
		return this.size;
	}
	
	@Override
	public String toString()
	{
		return "FastTable  size = " + this.size + ", " + (this.table != null ? new StringBuilder("table = ").append(Arrays.toString(this.table)).toString() : "");
	}
	
	private final void transfer(Entry<V>[] newTable)
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
					int i = FastTable.indexFor(entry.hash, newCapacity);
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
	}
	
	@Override
	public void writeUnlock()
	{
	}
	
	private static final class Entry<V>
	{
		private V value;
		private Entry<V> next;
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
		public final String toString()
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
		final /* synthetic */ FastTable this$0;
		
		private TableIterator(FastTable fastTable)
		{
			this.this$0 = fastTable;
			if (fastTable.size > 0)
			{
				while ((this.index < fastTable.table.length) && ((this.next = fastTable.table[this.index++]) == null))
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
			return this.nextEntry().value;
		}
		
		private Entry<V> nextEntry()
		{
			Entry<V> entry = this.next;
			if (entry == null)
			{
				throw new NoSuchElementException();
			}
			this.next = entry.next;
			if (this.next == null)
			{
				while ((this.index < this.this$0.table.length) && ((this.next = this.this$0.table[this.index++]) == null))
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
			int key = this.current.key;
			this.current = null;
			this.this$0.removeEntryForKey(key);
		}
	}
}

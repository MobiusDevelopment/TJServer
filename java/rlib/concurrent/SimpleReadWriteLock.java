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
package rlib.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class SimpleReadWriteLock implements AsynReadSynWriteLock
{
	private final Lock readLock;
	private final Lock writeLock;
	
	public SimpleReadWriteLock()
	{
		ReadWriteLock readWriteLock = Locks.newRWLock();
		readLock = readWriteLock.readLock();
		writeLock = readWriteLock.writeLock();
	}
	
	@Override
	public void readLock()
	{
		readLock.lock();
	}
	
	@Override
	public void readUnlock()
	{
		readLock.unlock();
	}
	
	@Override
	public void writeLock()
	{
		writeLock.lock();
	}
	
	@Override
	public void writeUnlock()
	{
		writeLock.unlock();
	}
}

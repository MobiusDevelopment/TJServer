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

import java.util.concurrent.locks.Lock;

import rlib.concurrent.Locks;

public final class Synchronizer implements Synchronized
{
	private final Lock lock = Locks.newLock();
	public volatile boolean locked;
	
	public final boolean isLocked()
	{
		return locked;
	}
	
	@Override
	public void lock()
	{
		lock.lock();
	}
	
	public final void setLocked(boolean locked)
	{
		this.locked = locked;
	}
	
	@Override
	public void unlock()
	{
		lock.unlock();
	}
}

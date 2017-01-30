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

public final class SimpleIdGenerator implements IdGenerator
{
	private final int start;
	private final int end;
	private volatile int nextId;
	
	public SimpleIdGenerator(int start, int end)
	{
		this.start = start;
		this.end = end;
		this.nextId = start;
	}
	
	@Override
	public synchronized int getNextId()
	{
		if (this.nextId == this.end)
		{
			this.nextId = this.start;
		}
		++this.nextId;
		return this.nextId;
	}
	
	@Override
	public void prepare()
	{
	}
	
	@Override
	public void releaseId(int id)
	{
	}
	
	@Override
	public int usedIds()
	{
		return this.nextId - this.start;
	}
}

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
package rlib.network.packets;

import java.nio.ByteBuffer;

public abstract class AbstractPacket<C> implements Packet<C>
{
	protected C owner;
	protected ByteBuffer buffer;
	protected String name;
	
	@Override
	public final ByteBuffer getBuffer()
	{
		return this.buffer;
	}
	
	@Override
	public final String getName()
	{
		if (this.name == null)
		{
			this.name = getClass().getSimpleName();
		}
		return this.name;
	}
	
	@Override
	public C getOwner()
	{
		return this.owner;
	}
	
	@Override
	public final void setBuffer(ByteBuffer buffer)
	{
		this.buffer = buffer;
	}
	
	@Override
	public void setOwner(C owner)
	{
		this.owner = owner;
	}
}

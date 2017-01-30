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

public interface SendablePacket<C> extends Packet<C>
{
	public void complete();
	
	public void decreaseSends();
	
	public void decreaseSends(int var1);
	
	public void increaseSends();
	
	public void increaseSends(int var1);
	
	public boolean isSynchronized();
	
	public void write(ByteBuffer var1);
	
	public void writeHeader(ByteBuffer var1, int var2);
	
	public void writeLocal();
	
	public void writePosition(ByteBuffer var1);
}

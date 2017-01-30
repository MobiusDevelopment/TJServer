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
package rlib.network.server.client;

import java.nio.ByteBuffer;

import rlib.network.AsynConnection;
import rlib.network.packets.ReadeablePacket;
import rlib.network.packets.SendablePacket;
import rlib.util.Synchronized;

public interface Client<A, P, C extends AsynConnection> extends Synchronized
{
	public void close();
	
	public void decrypt(ByteBuffer var1, int var2, int var3);
	
	public void encrypt(ByteBuffer var1, int var2, int var3);
	
	public A getAccount();
	
	public C getConnection();
	
	public String getHostAddress();
	
	public P getOwner();
	
	public boolean isConnected();
	
	public void readPacket(ReadeablePacket var1, ByteBuffer var2);
	
	public void sendPacket(SendablePacket var1);
	
	public void setAccount(A var1);
	
	public void setOwner(P var1);
	
	public void successfulConnection();
}

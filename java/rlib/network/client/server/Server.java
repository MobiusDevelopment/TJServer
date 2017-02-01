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
package rlib.network.client.server;

import java.nio.ByteBuffer;

import rlib.network.packets.ReadeablePacket;
import rlib.network.packets.SendablePacket;
import rlib.util.Synchronized;

@SuppressWarnings("rawtypes")
public interface Server<C extends ServerConnection> extends Synchronized
{
	void close();
	
	void decrypt(ByteBuffer var1, int var2, int var3);
	
	void encrypt(ByteBuffer var1, int var2, int var3);
	
	C getConnection();
	
	boolean isConnected();
	
	void readPacket(ReadeablePacket var1, ByteBuffer var2);
	
	void sendPacket(SendablePacket var1);
}

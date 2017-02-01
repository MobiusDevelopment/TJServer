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
package rlib.network;

public interface NetworkConfig
{
	String getGroupName();
	
	int getGroupSize();
	
	int getReadBufferSize();
	
	Class<? extends Thread> getThreadClass();
	
	int getThreadPriority();
	
	int getWriteBufferSize();
	
	boolean isVesibleReadException();
	
	boolean isVesibleWriteException();
}

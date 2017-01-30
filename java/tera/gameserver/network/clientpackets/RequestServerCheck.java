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
package tera.gameserver.network.clientpackets;

import tera.gameserver.network.serverpackets.CheckServerResult;

/**
 * @author Ronn
 */
public class RequestServerCheck extends ClientPacket
{
	private final int[] vals;
	
	public RequestServerCheck()
	{
		vals = new int[3];
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.ReadeablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	public void readImpl()
	{
		vals[0] = readInt();
		vals[1] = readInt();
		vals[2] = readInt();
	}
	
	@Override
	public void runImpl()
	{
		owner.sendPacket(CheckServerResult.getInstance(vals), true);
	}
}
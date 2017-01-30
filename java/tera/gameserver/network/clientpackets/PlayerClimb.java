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

import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class PlayerClimb extends ClientPacket
{
	
	private Player player;
	
	private float targetX;
	private float targetY;
	private float targetZ;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	@Override
	public void readImpl()
	{
		player = owner.getOwner();
		
		if (player == null)
		{
			return;
		}
		
		targetX = readFloat();
		targetY = readFloat();
		targetZ = readFloat();
	}
	
	@Override
	public void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		player.setXYZ(targetX, targetY, targetZ);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "PlayerClimb  targetX = " + targetX + ", targetY = " + targetY + ", targetZ = " + targetZ;
	}
}
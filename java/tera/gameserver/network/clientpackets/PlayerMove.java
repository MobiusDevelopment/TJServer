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

import tera.gameserver.manager.GeoManager;
import tera.gameserver.model.MoveType;
import tera.gameserver.model.playable.Player;

import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public class PlayerMove extends ClientPacket
{
	
	private Player player;
	
	private MoveType type;
	
	private int heading;
	
	private float targetX;
	private float targetY;
	private float targetZ;
	
	private float startX;
	private float startY;
	private float startZ;
	
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
		
		if ((player == null) || (buffer.remaining() < 27))
		{
			player = null;
			Loggers.warning(this, "incorrect packet");
			return;
		}
		
		startX = readFloat();
		startY = readFloat();
		startZ = readFloat();
		heading = readShort();
		targetX = readFloat();
		targetY = readFloat();
		targetZ = readFloat();
		type = MoveType.valueOf(readByte());
	}
	
	@Override
	public void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		if (type == MoveType.RUN)
		{
			GeoManager.write(player.getContinentId(), startX, startY, startZ);
			GeoManager.write(player.getContinentId(), targetX, targetY, targetZ);
		}
		
		player.getAI().startMove(startX, startY, startZ, heading, type, targetX, targetY, targetZ, true, false);
	}
}
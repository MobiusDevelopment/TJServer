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
import tera.gameserver.network.serverpackets.StateAllowed;

/**
 * @author Ronn
 */
public class RequestState extends ClientPacket
{
	private Player player;
	private int objectId;
	private int subId;
	private int stateId;
	
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
		objectId = readInt();
		subId = readInt();
		stateId = readInt();
	}
	
	@Override
	public void runImpl()
	{
		if ((player == null) || (player.getObjectId() != objectId) || (player.getSubId() != subId))
		{
			return;
		}
		
		player.sendPacket(StateAllowed.getInstance(player, stateId), true);
	}
}
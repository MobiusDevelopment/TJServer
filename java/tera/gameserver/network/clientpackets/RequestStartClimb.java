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
import tera.gameserver.model.territory.ClimbTerritory;
import tera.gameserver.network.serverpackets.CharClimb;

/**
 * @author Ronn
 */
public class RequestStartClimb extends ClientPacket
{
	
	private Player player;
	
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
	}
	
	@Override
	public void runImpl()
	{
		if ((player == null) || player.isBattleStanced())
		{
			return;
		}
		
		final ClimbTerritory territory = player.getTerritory(ClimbTerritory.class);
		
		if (territory == null)
		{
			return;
		}
		
		final int heading = player.calcHeading(territory.getTargetX(), territory.getTargetY());
		player.broadcastPacket(CharClimb.getInstance(player, heading, territory.getTargetX(), territory.getTargetY(), territory.getTargetZ()));
	}
}
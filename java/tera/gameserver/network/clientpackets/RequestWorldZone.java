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

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.gameserver.network.serverpackets.Tp1;
import tera.gameserver.network.serverpackets.WorldZone;

/**
 * @author Ronn
 */
public class RequestWorldZone extends ClientPacket
{
	
	private Player player;
	
	private int zoneId;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
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
	protected void readImpl()
	{
		player = owner.getOwner();
		readInt();
		zoneId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		if ((player == null) || player.isFlyingPegas() || (player.getZoneId() == zoneId))
		{
			return;
		}
		
		if (player.isOnMount())
		{
			player.getOffMount();
		}
		
		player.stopMove();
		player.decayMe(DeleteCharacter.DISAPPEARS);
		player.broadcastPacket(Tp1.getInstance(player));
		player.setZoneId(zoneId);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyChangedZoneId(player);
		player.sendPacket(WorldZone.getInstance(player), true);
	}
}

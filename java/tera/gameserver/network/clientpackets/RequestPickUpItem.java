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

import tera.gameserver.model.World;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class RequestPickUpItem extends ClientPacket
{
	private int itemId;
	private int itemSubId;
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
		player = owner.getOwner();
		itemId = readInt();
		itemSubId = readInt();
	}
	
	@Override
	public void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final ItemInstance item = World.getAroundById(ItemInstance.class, player, itemId, itemSubId);
		
		if ((item == null) || item.isInvisible())
		{
			return;
		}
		
		player.getAI().startItemPickUp(item);
	}
}
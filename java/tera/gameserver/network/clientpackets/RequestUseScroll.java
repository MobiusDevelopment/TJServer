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

import tera.gameserver.manager.ItemExecutorManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class RequestUseScroll extends ClientPacket
{
	private Player player;
	private int itemId;
	
	@Override
	public void readImpl()
	{
		player = owner.getOwner();
		itemId = readInt();
	}
	
	@Override
	public void runImpl()
	{
		if ((player == null) || player.isAllBlocking() || player.isOnMount())
		{
			return;
		}
		
		final Inventory inventory = player.getInventory();
		
		if (inventory == null)
		{
			return;
		}
		
		ItemInstance item = null;
		inventory.lock();
		
		try
		{
			item = inventory.getItemForItemId(itemId);
		}
		
		finally
		{
			inventory.unlock();
		}
		
		if ((item == null) || (item.getItemLevel() > player.getLevel()))
		{
			return;
		}
		
		final ItemExecutorManager executor = ItemExecutorManager.getInstance();
		
		if (!executor.execute(item, player))
		{
			player.getAI().startUseItem(item, player.getHeading(), false);
		}
	}
}
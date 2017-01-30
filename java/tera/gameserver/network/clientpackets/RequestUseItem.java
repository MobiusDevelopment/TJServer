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
public class RequestUseItem extends ClientPacket
{
	private Player player;
	private int itemId;
	private int objectId;
	private int heading;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
		objectId = 0;
		heading = -1;
	}
	
	@Override
	public void readImpl()
	{
		player = owner.getOwner();
		readInt();
		readInt();
		itemId = readInt();
		objectId = readInt();
		readLong();
		readLong();
		readInt();
		readInt();
		readFloat();
		readFloat();
		readFloat();
		heading = readShort();
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
			if (objectId != 0)
			{
				item = inventory.getItemForObjectId(objectId);
			}
			else
			{
				item = inventory.getItemForItemId(itemId);
			}
		}
		
		finally
		{
			inventory.unlock();
		}
		
		if ((item == null) || (item.getItemLevel() > player.getLevel()))
		{
			return;
		}
		
		if (heading == -1)
		{
			heading = player.getHeading();
		}
		
		final ItemExecutorManager executor = ItemExecutorManager.getInstance();
		
		if (!executor.execute(item, player))
		{
			player.getAI().startUseItem(item, heading, false);
		}
	}
}
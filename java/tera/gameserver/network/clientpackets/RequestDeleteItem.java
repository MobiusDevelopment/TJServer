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

import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Ronn
 */
public class RequestDeleteItem extends ClientPacket
{
	private Player player;
	private long count;
	private int index;
	
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
		readInt();
		readInt();
		index = readInt();
		count = readInt();
	}
	
	@Override
	public void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final Inventory inventory = player.getInventory();
		
		if (inventory == null)
		{
			log.warning(this, new Exception("not found inventory."));
			return;
		}
		
		inventory.lock();
		
		try
		{
			final Cell cell = inventory.getCell(index);
			
			if ((cell == null) || cell.isEmpty())
			{
				return;
			}
			
			final ItemInstance item = cell.getItem();
			
			if (!item.isDeletable())
			{
				final SystemMessage message = SystemMessage.getInstance(MessageType.YOU_CANT_DISCARD_ITEM_NAME);
				message.addItem(item.getItemId(), (int) item.getItemCount());
				player.sendPacket(message, true);
				return;
			}
			
			if (inventory.removeItemFromIndex(count, index))
			{
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(player);
				PacketManager.showDeleteItem(player, item);
				final GameLogManager gameLogger = GameLogManager.getInstance();
				gameLogger.writeItemLog(player.getName() + " delete item [id = " + item.getItemId() + ", count = " + count + ", name = " + item.getName() + "]");
			}
		}
		
		finally
		{
			inventory.unlock();
		}
	}
}
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
package tera.gameserver.scripts.items;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.MessageAddedItem;
import tera.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Ronn
 */
public class BarbecueItem extends AbstractItemExecutor
{
	public static final int RECEPT_ID = 5027;
	public static final int RESOURSE_ID = 5028;
	public static final int RESOURSE_2_ID = 5029;
	public static final int RESULT_ID = 5030;
	
	/**
	 * Constructor for BarbecueItem.
	 * @param itemIds int[]
	 * @param access int
	 */
	public BarbecueItem(int[] itemIds, int access)
	{
		super(itemIds, access);
	}
	
	/**
	 * Method execution.
	 * @param item ItemInstance
	 * @param player Player
	 * @see tera.gameserver.scripts.items.ItemExecutor#execution(ItemInstance, Player)
	 */
	@Override
	public void execution(ItemInstance item, Player player)
	{
		if (!player.isInBonfireTerritory())
		{
			player.sendMessage("You can only use a fixed fire.");
			return;
		}
		
		final Inventory inventory = player.getInventory();
		inventory.lock();
		
		try
		{
			if (!inventory.containsItems(RECEPT_ID, 1) || !inventory.containsItems(RESOURSE_ID, 1) || !inventory.containsItems(RESOURSE_2_ID, 1))
			{
				player.sendMessage("You are not components.");
				return;
			}
			
			inventory.removeItem(RECEPT_ID, 1L);
			inventory.removeItem(RESOURSE_ID, 1L);
			inventory.removeItem(RESOURSE_2_ID, 1L);
			inventory.forceAddItem(RESULT_ID, 1, "Bonfire");
		}
		
		finally
		{
			inventory.unlock();
		}
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyUseItem(item, player);
		eventManager.notifyInventoryChanged(player);
		player.sendPacket(SystemMessage.getInstance(MessageType.ITEM_USE).addItem(RECEPT_ID, 1), true);
		player.sendPacket(MessageAddedItem.getInstance(player.getName(), RESULT_ID, 1), true);
	}
}
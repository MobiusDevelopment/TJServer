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

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class RequestInventoryMovingItem extends ClientPacket
{
	private int oldcell;
	private int newcell;
	private Player player;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		oldcell = 0;
		newcell = 0;
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
		oldcell = readInt() - 20;
		newcell = readInt() - 20;
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
			return;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		inventory.lock();
		
		try
		{
			final Cell oldCell = inventory.getCell(oldcell);
			final Cell newCell = inventory.getCell(newcell);
			
			if ((oldCell == null) || (newCell == null))
			{
				return;
			}
			
			final ItemInstance oldItem = newCell.getItem();
			newCell.setItem(oldCell.getItem());
			oldCell.setItem(oldItem);
			dbManager.updateLocationItem(oldCell.getItem());
			dbManager.updateLocationItem(newCell.getItem());
		}
		
		finally
		{
			inventory.unlock();
		}
		eventManager.notifyInventoryChanged(player);
	}
}
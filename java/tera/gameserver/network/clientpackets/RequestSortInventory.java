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
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.interaction.dialogs.BankDialog;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class RequestSortInventory extends ClientPacket
{
	private static enum SortLocation
	{
		INVENTORY,
		BANK,
	}
	
	private Player player;
	
	private SortLocation location;
	
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
		location = SortLocation.values()[readInt()];
	}
	
	@Override
	protected void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		switch (location)
		{
			case INVENTORY:
			{
				final Inventory inventory = player.getInventory();
				
				if (inventory == null)
				{
					log.warning(this, new Exception("not found inventory"));
					return;
				}
				
				inventory.sort();
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(player);
				break;
			}
			
			case BANK:
			{
				final Dialog dialog = player.getLastDialog();
				
				if ((dialog == null) || !(dialog instanceof BankDialog))
				{
					return;
				}
				
				final BankDialog bank = (BankDialog) dialog;
				bank.sort();
			}
		}
	}
}

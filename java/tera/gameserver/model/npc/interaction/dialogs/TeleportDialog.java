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
package tera.gameserver.model.npc.interaction.dialogs;

import tera.gameserver.manager.GameLogManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.TeleportRegion;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.territory.LocalTerritory;
import tera.gameserver.network.serverpackets.TeleportPoints;

import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public class TeleportDialog extends AbstractDialog
{
	/**
	 * Method newInstance.
	 * @param npc Npc
	 * @param player Player
	 * @param regions TeleportRegion[]
	 * @param table Table<IntKey,TeleportRegion>
	 * @return TeleportDialog
	 */
	public static TeleportDialog newInstance(Npc npc, Player player, TeleportRegion[] regions, Table<IntKey, TeleportRegion> table)
	{
		final TeleportDialog dialog = (TeleportDialog) DialogType.TELEPORT.newInstance();
		dialog.npc = npc;
		dialog.player = player;
		dialog.regions = regions;
		dialog.table = table;
		return dialog;
	}
	
	private Table<IntKey, TeleportRegion> table;
	private TeleportRegion[] regions;
	
	/**
	 * Method getRegions.
	 * @return TeleportRegion[]
	 */
	public TeleportRegion[] getRegions()
	{
		return regions;
	}
	
	/**
	 * Method getType.
	 * @return DialogType
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#getType()
	 */
	@Override
	public DialogType getType()
	{
		return DialogType.TELEPORT;
	}
	
	/**
	 * Method init.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#init()
	 */
	@Override
	public synchronized boolean init()
	{
		if (!super.init())
		{
			return false;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		player.sendPacket(TeleportPoints.getInstance(npc, player, regions), true);
		return true;
	}
	
	/**
	 * Method teleport.
	 * @param index int
	 */
	public synchronized void teleport(int index)
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final TeleportRegion region = table.get(index);
		
		if (region == null)
		{
			log.warning(this, new Exception("not found region for index " + index));
			return;
		}
		
		final LocalTerritory territory = region.getRegion();
		
		if (!player.isWhetherIn(territory))
		{
			player.sendMessage(MessageType.NO_TERRAIN_FOUND_PLEASE_TELEPORT_TO_ANOTHER_AREA);
			return;
		}
		
		final Inventory inventory = player.getInventory();
		
		if (inventory == null)
		{
			log.warning(this, new Exception("not found inventory"));
			return;
		}
		
		final int price = region.getPrice();
		
		if (inventory.getMoney() < price)
		{
			player.sendMessage(MessageType.YOU_DONT_HAVE_ENOUGH_GOLD);
			return;
		}
		
		inventory.subMoney(price);
		final GameLogManager gameLogger = GameLogManager.getInstance();
		gameLogger.writeItemLog(player.getName() + " buy local teleport for " + price + " gold");
		player.teleToLocation(territory.getTeleportLoc());
		close();
	}
}
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

import tera.gameserver.model.Party;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.GuildInputName;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public final class CreateGuildDialog extends AbstractDialog
{
	/**
	 * Method newInstance.
	 * @param npc Npc
	 * @param player Player
	 * @param price int
	 * @param minLevel int
	 * @return CreateGuildDialog
	 */
	public static CreateGuildDialog newInstance(Npc npc, Player player, int price, int minLevel)
	{
		final CreateGuildDialog dialog = (CreateGuildDialog) DialogType.GUILD_CREATE.newInstance();
		dialog.player = player;
		dialog.npc = npc;
		dialog.price = price;
		dialog.minLevel = minLevel;
		return dialog;
	}
	
	private int price;
	private int minLevel;
	
	/**
	 * Method getType.
	 * @return DialogType
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#getType()
	 */
	@Override
	public DialogType getType()
	{
		return DialogType.GUILD_CREATE;
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
		
		if (player.getLevel() < minLevel)
		{
			player.sendMessage("Player level is too low.");
			return false;
		}
		
		if (player.hasGuild())
		{
			player.sendMessage("Player already has a guild.");
			return false;
		}
		
		final Party party = player.getParty();
		
		if (party == null)
		{
			player.sendMessage("Player is not in a party.");
			return false;
		}
		
		final Array<Player> members = party.getMembers();
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				if (array[i].hasGuild())
				{
					player.sendMessage("A member is already in a guild");
					return false;
				}
			}
		}
		
		finally
		{
			members.readUnlock();
		}
		final Inventory inventory = player.getInventory();
		
		if (inventory.getMoney() < price)
		{
			player.sendMessage("Not enough money.");
			return false;
		}
		
		player.sendPacket(GuildInputName.getInstance(), true);
		return true;
	}
}
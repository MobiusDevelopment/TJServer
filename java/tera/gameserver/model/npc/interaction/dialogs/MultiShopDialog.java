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

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.MultiShop;
import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 */
public final class MultiShopDialog extends AbstractDialog
{
	/**
	 * Method newInstance.
	 * @param npc Npc
	 * @param player Player
	 * @param items ItemTemplate[]
	 * @param price int[]
	 * @param priceId int
	 * @return MultiShopDialog
	 */
	public static MultiShopDialog newInstance(Npc npc, Player player, ItemTemplate[] items, int[] price, int priceId)
	{
		final MultiShopDialog dialog = (MultiShopDialog) DialogType.MULTI_SHOP.newInstance();
		dialog.items = items;
		dialog.npc = npc;
		dialog.player = player;
		dialog.price = price;
		dialog.priceId = priceId;
		return dialog;
	}
	
	private ItemTemplate[] items;
	private int[] price;
	private int priceId;
	
	protected MultiShopDialog()
	{
		super();
	}
	
	/**
	 * Method getType.
	 * @return DialogType
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#getType()
	 */
	@Override
	public DialogType getType()
	{
		return DialogType.MULTI_SHOP;
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
		
		player.sendPacket(MultiShop.getInstance(player, items, price, priceId), true);
		return true;
	}
}
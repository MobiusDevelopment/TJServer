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

import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.npc.interaction.dialogs.DialogType;
import tera.gameserver.model.npc.interaction.dialogs.ShopDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ShopTradePacket;

/**
 * @author Ronn
 */
public class RequestNpcAddSellShop extends ClientPacket
{
	private int itemId;
	private int itemCount;
	private int index;
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
	protected void readImpl()
	{
		player = owner.getOwner();
		readLong();
		readInt();
		itemId = readInt();
		itemCount = readInt();
		index = readInt() - 20;
	}
	
	@Override
	protected void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final Dialog dialog = player.getLastDialog();
		
		if ((dialog == null) || (dialog.getType() != DialogType.SHOP_WINDOW))
		{
			return;
		}
		
		final ShopDialog trade = (ShopDialog) dialog;
		
		if (trade.addSellItem(itemId, itemCount, index))
		{
			player.sendPacket(ShopTradePacket.getInstance(trade), true);
		}
	}
}
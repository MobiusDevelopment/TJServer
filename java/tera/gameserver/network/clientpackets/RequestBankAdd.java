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

import tera.gameserver.model.npc.interaction.dialogs.BankDialog;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class RequestBankAdd extends ClientPacket
{
	private Player player;
	private int gold;
	private int index;
	private int itemId;
	private int itemCount;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	@Override
	protected void readImpl()
	{
		player = owner.getOwner();
		readInt();
		readInt();
		readInt();
		readInt();
		gold = readInt();
		readInt();
		
		if (gold < 0)
		{
			return;
		}
		
		index = readInt();
		itemId = readInt();
		readInt();
		readInt();
		itemCount = readInt();
		readInt();
	}
	
	@Override
	protected void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final Dialog dialog = player.getLastDialog();
		
		if ((dialog == null) || !(dialog instanceof BankDialog))
		{
			return;
		}
		
		final BankDialog bank = (BankDialog) dialog;
		
		if (gold > 0)
		{
			bank.addMoney(gold);
		}
		else
		{
			bank.addItem(index - 20, itemId, itemCount);
		}
	}
}
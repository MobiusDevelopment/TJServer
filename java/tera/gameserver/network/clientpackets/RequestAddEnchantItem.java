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

import tera.gameserver.model.actions.dialogs.ActionDialog;
import tera.gameserver.model.actions.dialogs.ActionDialogType;
import tera.gameserver.model.actions.dialogs.EnchantItemDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.model.UserClient;

/**
 * @author Ronn
 */
public class RequestAddEnchantItem extends ClientPacket
{
	
	private int index;
	
	private int objectId;
	
	private int itemId;
	
	@Override
	protected void readImpl()
	{
		index = readInt();
		objectId = readInt();
		readInt();
		itemId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final UserClient client = getOwner();
		
		if (client == null)
		{
			return;
		}
		
		final Player actor = client.getOwner();
		
		if (actor == null)
		{
			return;
		}
		
		final ActionDialog dialog = actor.getLastActionDialog();
		
		if ((dialog == null) || (dialog.getType() != ActionDialogType.ENCHANT_ITEM_DIALOG))
		{
			return;
		}
		
		final EnchantItemDialog enchantDialog = (EnchantItemDialog) dialog;
		enchantDialog.addItem(index, objectId, itemId);
	}
}

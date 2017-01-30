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

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ItemTemplateInfo;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 */
public class RequestItemTemplateInfo extends ClientPacket
{
	
	private Player player;
	
	private int itemId;
	
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
		itemId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final ItemTable itemTable = ItemTable.getInstance();
		final ItemTemplate template = itemTable.getItem(itemId);
		
		if (template == null)
		{
			return;
		}
		
		player.sendPacket(ItemTemplateInfo.getInstance(template.getItemId()), true);
	}
}

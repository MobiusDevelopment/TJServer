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
package tera.gameserver.model.npc.interaction.replyes;

import org.w3c.dom.Node;

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.npc.interaction.dialogs.MultiShopDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;

import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class ReplyMultiShop extends AbstractReply
{
	private final ItemTemplate[] items;
	private final int[] price;
	private final int priceId;
	
	/**
	 * Constructor for ReplyMultiShop.
	 * @param node Node
	 */
	public ReplyMultiShop(Node node)
	{
		super(node);
		final VarTable vars = VarTable.newInstance(node);
		priceId = vars.getInteger("priceId");
		final Array<ItemTemplate> itemList = Arrays.toArray(ItemTemplate.class);
		final Array<Integer> priceList = Arrays.toArray(Integer.class);
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("item".equals(child.getNodeName()))
			{
				vars.parse(child);
				final int id = vars.getInteger("id");
				final ItemTable itemTable = ItemTable.getInstance();
				final ItemTemplate template = itemTable.getItem(id);
				
				if (template == null)
				{
					log.warning("not itemId " + id + " in item table.");
					continue;
				}
				
				final int price = vars.getInteger("price");
				itemList.add(template);
				priceList.add(price);
			}
		}
		
		items = new ItemTemplate[itemList.size()];
		price = new int[priceList.size()];
		
		for (int i = 0, length = itemList.size(); i < length; i++)
		{
			items[i] = itemList.get(i);
			price[i] = priceList.get(i);
		}
	}
	
	/**
	 * Method reply.
	 * @param npc Npc
	 * @param player Player
	 * @param link Link
	 * @see tera.gameserver.model.npc.interaction.replyes.Reply#reply(Npc, Player, Link)
	 */
	@Override
	public void reply(Npc npc, Player player, Link link)
	{
		final Dialog dialog = MultiShopDialog.newInstance(npc, player, items, price, priceId);
		
		if (!dialog.init())
		{
			dialog.close();
		}
	}
}
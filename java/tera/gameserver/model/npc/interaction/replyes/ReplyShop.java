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

import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.TaxationNpc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.npc.interaction.dialogs.ShopDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;

import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class ReplyShop extends AbstractReply
{
	private final ItemTemplate[][] sections;
	private final Table<IntKey, ItemTemplate> availableItems;
	private final int sectionId;
	
	/**
	 * Constructor for ReplyShop.
	 * @param node Node
	 */
	public ReplyShop(Node node)
	{
		super(node);
		final VarTable vars = VarTable.newInstance(node);
		sectionId = vars.getInteger("sectionId");
		final Array<Array<ItemTemplate>> sectionList = Arrays.toArray(Array.class);
		final ItemTable itemTable = ItemTable.getInstance();
		
		for (Node section = node.getFirstChild(); section != null; section = section.getNextSibling())
		{
			if (section.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("section".equals(section.getNodeName()))
			{
				final Array<ItemTemplate> items = Arrays.toArray(ItemTemplate.class);
				
				for (Node item = section.getFirstChild(); item != null; item = item.getNextSibling())
				{
					if (item.getNodeType() != Node.ELEMENT_NODE)
					{
						continue;
					}
					
					if ("item".equals(item.getNodeName()))
					{
						vars.parse(item);
						final int id = vars.getInteger("id");
						final ItemTemplate template = itemTable.getItem(id);
						
						if (template == null)
						{
							log.warning("not itemId " + id + " in item table.");
							continue;
						}
						
						items.add(template);
					}
				}
				
				sectionList.add(items);
			}
		}
		
		sections = new ItemTemplate[sectionList.size()][];
		final Array<ItemTemplate>[] array = sectionList.array();
		
		for (int i = 0, length = sectionList.size(); i < length; i++)
		{
			sections[i] = array[i].trimToSize().array();
		}
		
		int counter = 0;
		
		for (ItemTemplate[] items : sections)
		{
			counter += items.length;
		}
		
		if (counter < 1)
		{
			throw new IllegalArgumentException("no items");
		}
		
		availableItems = Tables.newIntegerTable();
		
		for (ItemTemplate[] items : sections)
		{
			for (ItemTemplate item : items)
			{
				availableItems.put(item.getItemId(), item);
			}
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
		Bank bank = null;
		float resultTax = 1;
		
		if (npc instanceof TaxationNpc)
		{
			final TaxationNpc taxation = (TaxationNpc) npc;
			bank = taxation.getTaxBank();
			resultTax = 1 + (taxation.getTax() / 100F);
		}
		
		final Dialog dialog = ShopDialog.newInstance(npc, sections, availableItems, player, bank, sectionId, resultTax);
		
		if (!dialog.init())
		{
			dialog.close();
		}
	}
}
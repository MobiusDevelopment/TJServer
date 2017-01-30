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

import tera.gameserver.events.EventConstant;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.MessageAddedItem;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;

/**
 * @author Ronn
 */
public class ReplyEventReward extends AbstractReply
{
	private static final int[] PRICE =
	{
		2,
		3,
		6,
		7,
	};
	private static final ItemTemplate[] ITEMS;
	static
	{
		final ItemTable itemTable = ItemTable.getInstance();
		ITEMS = new ItemTemplate[]
		{
			itemTable.getItem(408),
			itemTable.getItem(409),
			itemTable.getItem(410),
			itemTable.getItem(411),
		};
	}
	
	/**
	 * Constructor for ReplyEventReward.
	 * @param node Node
	 */
	public ReplyEventReward(Node node)
	{
		super(node);
	}
	
	/**
	 * Method levelToIndex.
	 * @param level int
	 * @return int
	 */
	private int levelToIndex(int level)
	{
		if (level > 57)
		{
			return 3;
		}
		else if (level > 47)
		{
			return 2;
		}
		else if (level > 34)
		{
			return 1;
		}
		else
		{
			return 0;
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
		final int index = levelToIndex(player.getLevel());
		final int price = PRICE[index];
		final ItemTemplate template = ITEMS[index];
		
		if (template == null)
		{
			log.warning("not found event reward.");
			return;
		}
		
		synchronized (player)
		{
			final int val = player.getVar(EventConstant.VAR_NANE_HERO_POINT, 0);
			
			if (val < price)
			{
				player.sendMessage("You do not have enough fame points.");
			}
			else
			{
				final Inventory inventory = player.getInventory();
				
				if (!inventory.addItem(template.getItemId(), 1, "EventReward"))
				{
					player.sendMessage("Free inventory space.");
				}
				else
				{
					player.setVar(EventConstant.VAR_NANE_HERO_POINT, Math.max(val - price, 0));
					player.sendMessage("You spent " + price + " points of glory.");
					player.sendPacket(MessageAddedItem.getInstance(player.getName(), template.getItemId(), 1), true);
					final ObjectEventManager eventManager = ObjectEventManager.getInstance();
					final DataBaseManager dbManager = DataBaseManager.getInstance();
					dbManager.updatePlayerVar(player.getObjectId(), EventConstant.VAR_NANE_HERO_POINT, String.valueOf(Math.max(val - price, 0)));
					eventManager.notifyInventoryChanged(player);
				}
			}
		}
	}
}
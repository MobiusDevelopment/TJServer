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
package tera.gameserver.scripts.commands;

import tera.Config;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ItemTemplateInfo;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.logging.Loggers;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 27.03.2012
 */
public class ItemCommand extends AbstractCommand
{
	/**
	 * Constructor for ItemCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public ItemCommand(int access, String[] commands)
	{
		super(access, commands);
	}
	
	/**
	 * Method execution.
	 * @param command String
	 * @param player Player
	 * @param values String
	 * @see tera.gameserver.scripts.commands.Command#execution(String, Player, String)
	 */
	@Override
	public void execution(String command, Player player, String values)
	{
		final ItemTable itemTable = ItemTable.getInstance();
		
		switch (command)
		{
			case "reload_items":
			{
				itemTable.reload();
				player.sayMessage("items reloaded.");
				break;
			}
			
			case "spawn_item":
			{
				final String[] vals = values.split(" ");
				
				if (vals.length != 2)
				{
					return;
				}
				
				final int id = Integer.parseInt(vals[0]);
				final int count = Integer.parseInt(vals[1]);
				
				if (Arrays.contains(Config.WORLD_DONATE_ITEMS, id))
				{
					Loggers.warning(this, new Exception("not create donate item for id " + id));
					return;
				}
				
				if (count < 1)
				{
					return;
				}
				
				final ItemTemplate template = itemTable.getItem(id);
				
				if (template == null)
				{
					return;
				}
				
				if (template.isStackable() || (count < 2))
				{
					final ItemInstance item = template.newInstance();
					
					if (item == null)
					{
						return;
					}
					
					item.setItemCount(count);
					item.setTempOwner(player);
					item.setContinentId(player.getContinentId());
					item.spawnMe(player.getX(), player.getY(), player.getZ(), 0);
				}
				else
				{
					final Location[] locs = Coords.circularCoords(Location.class, player.getX(), player.getY(), player.getZ(), 45, count);
					
					for (int i = 0; i < count; i++)
					{
						final ItemInstance item = template.newInstance();
						item.setItemCount(1);
						item.setTempOwner(player);
						item.setContinentId(player.getContinentId());
						item.spawnMe(locs[i]);
					}
				}
				
				break;
			}
			
			case "item_info":
			{
				player.sendPacket(ItemTemplateInfo.getInstance(Integer.parseInt(values)), true);
				break;
			}
			
			case "create_item":
			{
				final String[] vals = values.split(" ");
				
				if (vals.length < 1)
				{
					return;
				}
				
				final int id = Integer.parseInt(vals[0]);
				
				if (Arrays.contains(Config.WORLD_DONATE_ITEMS, id))
				{
					Loggers.warning(this, new Exception("not create donate item for id " + id));
					return;
				}
				
				final ItemTemplate template = itemTable.getItem(id);
				
				if (template == null)
				{
					return;
				}
				
				final ItemInstance newItem = template.newInstance();
				
				if (newItem == null)
				{
					return;
				}
				
				newItem.setAutor("GM_Create_Item");
				final Inventory inventory = player.getInventory();
				
				if ((vals.length > 1) && template.isStackable())
				{
					newItem.setItemCount(Integer.parseInt(vals[1]));
				}
				
				if (inventory.putItem(newItem))
				{
					final ObjectEventManager eventManager = ObjectEventManager.getInstance();
					eventManager.notifyInventoryChanged(player);
				}
				
				break;
			}
		}
	}
}

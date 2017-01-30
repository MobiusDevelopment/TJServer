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

import tera.Config;
import tera.gameserver.events.EventConstant;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class ReplyHeroPointToGold extends AbstractReply
{
	/**
	 * Constructor for ReplyHeroPointToGold.
	 * @param node Node
	 */
	public ReplyHeroPointToGold(Node node)
	{
		super(node);
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
		synchronized (player)
		{
			final int points = player.getVar(EventConstant.VAR_NANE_HERO_POINT, 0);
			
			if (points < 1)
			{
				player.sendMessage("You have no points of fame.");
				return;
			}
			
			player.setVar(EventConstant.VAR_NANE_HERO_POINT, points - 1);
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			dbManager.updatePlayerVar(player.getObjectId(), EventConstant.VAR_NANE_HERO_POINT, String.valueOf(points - 1));
		}
		final int reward = (int) (1 * Config.EVENT_HERO_POINT_TO_GOLD * Config.SERVER_RATE_MONEY);
		
		if (reward < 1)
		{
			return;
		}
		
		final Inventory inventory = player.getInventory();
		inventory.addMoney(reward);
		PacketManager.showAddGold(player, reward);
	}
}
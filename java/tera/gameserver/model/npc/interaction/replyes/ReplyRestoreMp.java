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

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class ReplyRestoreMp extends AbstractReply
{
	
	private final int price;
	
	/**
	 * Constructor for ReplyRestoreMp.
	 * @param node Node
	 */
	public ReplyRestoreMp(Node node)
	{
		super(node);
		price = VarTable.newInstance(node).getInteger("price");
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
		if (player.getCurrentMp() >= player.getMaxMp())
		{
			return;
		}
		
		final Inventory inventory = player.getInventory();
		
		if (inventory.getMoney() < price)
		{
			player.sendMessage(MessageType.YOU_DONT_HAVE_ENOUGH_GOLD);
			return;
		}
		
		inventory.subMoney(price);
		PacketManager.showPaidGold(player, price);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyInventoryChanged(player);
		player.effectHealMp(player.getMaxMp(), player);
	}
}

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
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class ReplyHeroPoint extends AbstractReply
{
	/**
	 * Constructor for ReplyHeroPoint.
	 * @param node Node
	 */
	public ReplyHeroPoint(Node node)
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
		final int val = player.getVar(EventConstant.VAR_NANE_HERO_POINT, 0);
		
		if (val == 0)
		{
			player.sendMessage("You have no points of fame.");
		}
		else if (val > 0)
		{
			player.sendMessage("Number of points fame: " + val);
		}
	}
}
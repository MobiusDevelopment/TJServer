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

import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildRank;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.npc.interaction.dialogs.LoadGuildIcon;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public final class ReplyLoagGuildIcon extends AbstractReply
{
	/**
	 * Constructor for ReplyLoagGuildIcon.
	 * @param node Node
	 */
	public ReplyLoagGuildIcon(Node node)
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
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			player.sendMessage(MessageType.YOU_NOT_IN_GUILD);
			return;
		}
		
		final GuildRank rank = player.getGuildRank();
		
		if (!rank.isGuildMaster())
		{
			player.sendMessage(MessageType.YOU_ARE_NOT_THE_GUILD_MASTER);
			return;
		}
		
		final Dialog dialog = LoadGuildIcon.newInstance(npc, player);
		
		if (!dialog.init())
		{
			dialog.close();
		}
	}
}
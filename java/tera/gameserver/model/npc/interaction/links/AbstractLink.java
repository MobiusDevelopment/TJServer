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
package tera.gameserver.model.npc.interaction.links;

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.npc.interaction.IconType;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.LinkType;
import tera.gameserver.model.npc.interaction.replyes.Reply;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public abstract class AbstractLink implements Link
{
	protected String name;
	protected LinkType type;
	protected IconType icon;
	protected Reply reply;
	protected Condition condition;
	
	/**
	 * Constructor for AbstractLink.
	 * @param name String
	 * @param type LinkType
	 * @param icon IconType
	 * @param reply Reply
	 * @param condition Condition
	 */
	public AbstractLink(String name, LinkType type, IconType icon, Reply reply, Condition condition)
	{
		this.name = name;
		this.type = type;
		this.reply = reply;
		this.icon = icon;
	}
	
	/**
	 * Method getIconId.
	 * @return int
	 * @see tera.gameserver.model.npc.interaction.Link#getIconId()
	 */
	@Override
	public int getIconId()
	{
		return icon.ordinal();
	}
	
	/**
	 * Method getId.
	 * @return int
	 * @see tera.gameserver.model.npc.interaction.Link#getId()
	 */
	@Override
	public int getId()
	{
		return 0;
	}
	
	/**
	 * Method getName.
	 * @return String
	 * @see tera.gameserver.model.npc.interaction.Link#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Method getReply.
	 * @return Reply
	 * @see tera.gameserver.model.npc.interaction.Link#getReply()
	 */
	@Override
	public Reply getReply()
	{
		return reply;
	}
	
	/**
	 * Method getType.
	 * @return LinkType
	 * @see tera.gameserver.model.npc.interaction.Link#getType()
	 */
	@Override
	public LinkType getType()
	{
		return type;
	}
	
	/**
	 * Method reply.
	 * @param npc Npc
	 * @param player Player
	 * @see tera.gameserver.model.npc.interaction.Link#reply(Npc, Player)
	 */
	@Override
	public void reply(Npc npc, Player player)
	{
		reply.reply(npc, player, this);
	}
	
	/**
	 * Method test.
	 * @param npc Npc
	 * @param player Player
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.Link#test(Npc, Player)
	 */
	@Override
	public boolean test(Npc npc, Player player)
	{
		return false;
	}
}
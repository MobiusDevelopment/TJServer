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
package tera.gameserver.model.npc.interaction;

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.replyes.Reply;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public interface Link
{
	/**
	 * Method getIconId.
	 * @return int
	 */
	public int getIconId();
	
	/**
	 * Method getId.
	 * @return int
	 */
	public int getId();
	
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName();
	
	/**
	 * Method getReply.
	 * @return Reply
	 */
	public Reply getReply();
	
	/**
	 * Method getType.
	 * @return LinkType
	 */
	public LinkType getType();
	
	/**
	 * Method reply.
	 * @param npc Npc
	 * @param player Player
	 */
	public void reply(Npc npc, Player player);
	
	/**
	 * Method test.
	 * @param npc Npc
	 * @param player Player
	 * @return boolean
	 */
	public boolean test(Npc npc, Player player);
}
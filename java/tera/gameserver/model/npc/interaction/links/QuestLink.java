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
import tera.gameserver.model.npc.interaction.LinkType;
import tera.gameserver.model.npc.interaction.replyes.Reply;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class QuestLink extends AbstractLink
{
	private final Condition condition;
	private final int id;
	
	/**
	 * Constructor for QuestLink.
	 * @param name String
	 * @param icon IconType
	 * @param id int
	 * @param reply Reply
	 * @param condition Condition
	 */
	public QuestLink(String name, IconType icon, int id, Reply reply, Condition condition)
	{
		super(name, LinkType.QUEST, icon, reply, condition);
		this.id = id;
		this.condition = condition;
	}
	
	/**
	 * Method getId.
	 * @return int
	 * @see tera.gameserver.model.npc.interaction.Link#getId()
	 */
	@Override
	public int getId()
	{
		return id;
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
		if ((condition == null) || condition.test(npc, player))
		{
			return true;
		}
		
		return false;
	}
}
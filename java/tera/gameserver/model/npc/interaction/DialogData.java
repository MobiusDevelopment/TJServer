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
import tera.gameserver.model.playable.Player;

import rlib.util.Strings;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public final class DialogData
{
	public static final String INDEX_LINK = Strings.EMPTY;
	private Link[] links;
	private int npcId;
	private final int type;
	
	/**
	 * Constructor for DialogData.
	 * @param links Link[]
	 * @param npcId int
	 * @param type int
	 */
	public DialogData(Link[] links, int npcId, int type)
	{
		this.links = links;
		this.npcId = npcId;
		this.type = type;
	}
	
	/**
	 * Method addLinks.
	 * @param container Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	public void addLinks(Array<Link> container, Npc npc, Player player)
	{
		final Link[] links = getLinks();
		
		if (links.length < 1)
		{
			return;
		}
		
		for (Link link2 : links)
		{
			final Link link = link2;
			
			if (!link.test(npc, player))
			{
				continue;
			}
			
			container.add(link);
		}
	}
	
	/**
	 * Method getLinks.
	 * @return Link[]
	 */
	public final Link[] getLinks()
	{
		return links;
	}
	
	/**
	 * Method getNpcId.
	 * @return int
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Method getType.
	 * @return int
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Method setLinks.
	 * @param links Link[]
	 */
	public final void setLinks(Link[] links)
	{
		this.links = links;
	}
	
	/**
	 * Method setNpcId.
	 * @param npcId int
	 */
	public void setNpcId(int npcId)
	{
		this.npcId = npcId;
	}
}
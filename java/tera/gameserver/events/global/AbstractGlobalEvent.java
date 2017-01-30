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
package tera.gameserver.events.global;

import tera.gameserver.events.Event;
import tera.gameserver.events.NpcInteractEvent;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public abstract class AbstractGlobalEvent implements Event, NpcInteractEvent
{
	/**
	 * Method addLinks.
	 * @param links Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	@Override
	public void addLinks(Array<Link> links, Npc npc, Player player)
	{
	}
	
	/**
	 * Method isAuto.
	 * @return boolean
	 * @see tera.gameserver.events.Event#isAuto()
	 */
	@Override
	public boolean isAuto()
	{
		return false;
	}
	
	/**
	 * Method onLoad.
	 * @return boolean
	 * @see tera.gameserver.events.Event#onLoad()
	 */
	@Override
	public boolean onLoad()
	{
		return true;
	}
	
	/**
	 * Method onReload.
	 * @return boolean
	 * @see tera.gameserver.events.Event#onReload()
	 */
	@Override
	public boolean onReload()
	{
		return false;
	}
	
	/**
	 * Method onSave.
	 * @return boolean
	 * @see tera.gameserver.events.Event#onSave()
	 */
	@Override
	public boolean onSave()
	{
		return false;
	}
	
	/**
	 * Method start.
	 * @return boolean
	 * @see tera.gameserver.events.Event#start()
	 */
	@Override
	public boolean start()
	{
		return false;
	}
	
	/**
	 * Method stop.
	 * @return boolean
	 * @see tera.gameserver.events.Event#stop()
	 */
	@Override
	public boolean stop()
	{
		return false;
	}
}
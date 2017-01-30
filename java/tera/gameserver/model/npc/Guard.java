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
package tera.gameserver.model.npc;

import tera.gameserver.model.Character;
import tera.gameserver.templates.NpcTemplate;

/**
 * @author Ronn
 */
public class Guard extends FriendNpc
{
	/**
	 * Constructor for Guard.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public Guard(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method checkTarget.
	 * @param target Character
	 * @return boolean
	 */
	@Override
	public boolean checkTarget(Character target)
	{
		if (!target.isNpc())
		{
			return false;
		}
		
		final Npc npc = target.getNpc();
		return !npc.isGuard() && !npc.isFriendNpc();
	}
	
	/**
	 * Method isGuard.
	 * @return boolean
	 */
	@Override
	public boolean isGuard()
	{
		return true;
	}
}

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
package tera.gameserver.model.skillengine.classes;

import tera.gameserver.model.Character;
import tera.gameserver.model.Party;
import tera.gameserver.model.playable.Player;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class PartySummon extends AbstractSkill
{
	/**
	 * Constructor for PartySummon.
	 * @param template SkillTemplate
	 */
	public PartySummon(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method useSkill.
	 * @param character Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#useSkill(Character, float, float, float)
	 */
	@Override
	public void useSkill(Character character, float targetX, float targetY, float targetZ)
	{
		if (!character.isPlayer())
		{
			return;
		}
		
		final Player player = character.getPlayer();
		
		if (player.isEvent())
		{
			return;
		}
		
		final Party party = character.getParty();
		
		if (party == null)
		{
			return;
		}
		
		final Array<Player> members = party.getMembers();
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (!member.isDead() && !member.isEvent() && (member != player))
				{
					member.teleToLocation(player.getContinentId(), player.getX(), player.getY(), player.getZ());
				}
			}
		}
		
		finally
		{
			members.readUnlock();
		}
	}
}
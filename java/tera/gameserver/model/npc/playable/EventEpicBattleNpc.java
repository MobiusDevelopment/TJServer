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
package tera.gameserver.model.npc.playable;

import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.templates.NpcTemplate;

/**
 * @author Ronn
 */
public class EventEpicBattleNpc extends PlayerKiller
{
	/**
	 * Constructor for EventEpicBattleNpc.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public EventEpicBattleNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method addAggro.
	 * @param aggressor Character
	 * @param aggro long
	 * @param damage boolean
	 */
	@Override
	public void addAggro(Character aggressor, long aggro, boolean damage)
	{
		if (!damage && aggressor.isPlayer())
		{
			final Player player = aggressor.getPlayer();
			
			switch (player.getPlayerClass())
			{
				case WARRIOR:
				case LANCER:
					aggro = 1;
					break;
				
				default:
					break;
			}
		}
		
		super.addAggro(aggressor, aggro, damage);
	}
	
	/**
	 * Method causingDamage.
	 * @param skill Skill
	 * @param info AttackInfo
	 * @param attacker Character
	 */
	@Override
	public void causingDamage(Skill skill, AttackInfo info, Character attacker)
	{
		final Player player = attacker.getPlayer();
		
		if ((player != null) && !player.isEvent())
		{
			return;
		}
		
		super.causingDamage(skill, info, attacker);
	}
}
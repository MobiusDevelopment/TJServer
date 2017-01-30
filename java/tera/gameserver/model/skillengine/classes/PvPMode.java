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

import tera.Config;
import tera.gameserver.model.Character;
import tera.gameserver.model.playable.Player;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class PvPMode extends AbstractSkill
{
	
	public PvPMode(SkillTemplate template)
	{
		super(template);
	}
	
	@Override
	public boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ)
	{
		if (!attacker.isPlayer())
		{
			return false;
		}
		
		final Player player = attacker.getPlayer();
		
		if (!Config.WORLD_PK_AVAILABLE)
		{
			player.sendMessage("PvP mode is temporarily unavailable.");
			return false;
		}
		
		if (player.getKarma() > 0)
		{
			player.sendMessage("Cannot not use when having karma (" + player.getKarma() + ").");
			return false;
		}
		
		if (player.isPvPMode() && player.isBattleStanced())
		{
			player.sendMessage("Do not use in a combat stance.");
			return false;
		}
		
		if (!player.isPvPMode() && (player.getDuel() != null))
		{
			player.sendMessage("It can not be used in a duel.");
			return false;
		}
		
		return super.checkCondition(attacker, targetX, targetY, targetZ);
	}
	
	@Override
	public void useSkill(Character character, float targetX, float targetY, float targetZ)
	{
		character.setPvPMode(!character.isPvPMode());
	}
}

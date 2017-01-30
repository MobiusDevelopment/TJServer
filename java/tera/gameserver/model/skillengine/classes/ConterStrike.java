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
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class ConterStrike extends Strike
{
	
	public ConterStrike(SkillTemplate template)
	{
		super(template);
	}
	
	@Override
	public boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ)
	{
		if (!attacker.isPlayer() || !attacker.isDefenseStance())
		{
			return false;
		}
		
		final long last = attacker.getPlayer().getLastBlock();
		
		if ((System.currentTimeMillis() - last) > 1000)
		{
			attacker.sendMessage("It can only be used after a successful block.");
			return false;
		}
		
		return super.checkCondition(attacker, targetX, targetY, targetZ);
	}
	
	@Override
	public boolean isWaitable()
	{
		return false;
	}
}

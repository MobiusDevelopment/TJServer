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
import tera.gameserver.network.serverpackets.RequestSkillStart;
import tera.gameserver.network.serverpackets.SkillEnd;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class PrepareNextSkill extends Strike
{
	/**
	 * Constructor for PrepareNextSkill.
	 * @param template SkillTemplate
	 */
	public PrepareNextSkill(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method endSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param force boolean
	 * @see tera.gameserver.model.skillengine.Skill#endSkill(Character, float, float, float, boolean)
	 */
	@Override
	public void endSkill(Character attacker, float targetX, float targetY, float targetZ, boolean force)
	{
		template.removeCastFuncs(attacker);
		
		if (force || attacker.isAttackBlocking() || attacker.isOwerturned())
		{
			attacker.broadcastPacket(SkillEnd.getInstance(attacker, castId, template.getId()));
			return;
		}
		
		attacker.setCastId(castId);
		attacker.sendPacket(RequestSkillStart.getInstance(template.getId() + template.getOffsetId()), true);
	}
}

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
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.MountOn;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class Mount extends AbstractSkill
{
	/**
	 * Constructor for Mount.
	 * @param template SkillTemplate
	 */
	public Mount(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method checkCondition.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Skill#checkCondition(Character, float, float, float)
	 */
	@Override
	public boolean checkCondition(Character attacker, float targetX, float targetY, float targetZ)
	{
		final Player player = attacker.getPlayer();
		
		if ((player != null) && player.isOnMount())
		{
			final Skill skill = player.getMountSkill();
			
			if (skill != this)
			{
				return false;
			}
		}
		
		return super.checkCondition(attacker, targetX, targetY, targetZ);
	}
	
	/**
	 * Method startSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#startSkill(Character, float, float, float)
	 */
	@Override
	public void startSkill(Character attacker, float targetX, float targetY, float targetZ)
	{
		final Player player = attacker.getPlayer();
		
		if (player == null)
		{
			return;
		}
		
		if (player.isOnMount())
		{
			final Skill skill = player.getMountSkill();
			
			if (skill != this)
			{
				return;
			}
			
			player.getOffMount();
			return;
		}
		
		super.startSkill(attacker, targetX, targetY, targetZ);
		
		if (!player.isOnMount())
		{
			template.addPassiveFuncs(player);
			player.setMountId(template.getMountId());
			player.setMountSkill(this);
			player.broadcastPacket(MountOn.getInstance(player, getIconId()));
			player.updateInfo();
		}
	}
}
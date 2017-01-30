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

import tera.gameserver.events.global.regionwars.Region;
import tera.gameserver.events.global.regionwars.RegionWarNpc;
import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.Guild;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.TargetHp;
import tera.gameserver.templates.NpcTemplate;

/**
 * @author Ronn
 */
public class RegionWarControl extends Monster implements RegionWarNpc
{
	
	private Guild guild;
	
	private Region region;
	
	/**
	 * Constructor for RegionWarControl.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public RegionWarControl(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method setGuildOwner.
	 * @param guild Guild
	 * @see tera.gameserver.events.global.regionwars.RegionWarNpc#setGuildOwner(Guild)
	 */
	@Override
	public void setGuildOwner(Guild guild)
	{
		this.guild = guild;
	}
	
	/**
	 * Method setRegion.
	 * @param region Region
	 * @see tera.gameserver.events.global.regionwars.RegionWarNpc#setRegion(Region)
	 */
	@Override
	public void setRegion(Region region)
	{
		this.region = region;
	}
	
	/**
	 * Method getRegion.
	 * @return Region
	 * @see tera.gameserver.events.global.regionwars.RegionWarNpc#getRegion()
	 */
	@Override
	public Region getRegion()
	{
		return region;
	}
	
	/**
	 * Method getGuildOwner.
	 * @return Guild
	 * @see tera.gameserver.events.global.regionwars.RegionWarNpc#getGuildOwner()
	 */
	@Override
	public Guild getGuildOwner()
	{
		return guild;
	}
	
	/**
	 * Method isOwerturnImmunity.
	 * @return boolean
	 */
	@Override
	public boolean isOwerturnImmunity()
	{
		return true;
	}
	
	/**
	 * Method isStunImmunity.
	 * @return boolean
	 */
	@Override
	public boolean isStunImmunity()
	{
		return true;
	}
	
	/**
	 * Method isSleepImmunity.
	 * @return boolean
	 */
	@Override
	public boolean isSleepImmunity()
	{
		return true;
	}
	
	/**
	 * Method isLeashImmunity.
	 * @return boolean
	 */
	@Override
	public boolean isLeashImmunity()
	{
		return true;
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
		if (attacker.isPlayer())
		{
			final Guild guild = attacker.getGuild();
			
			if (!region.isRegister(guild) || (getGuildOwner() == guild))
			{
				return;
			}
		}
		
		super.causingDamage(skill, info, attacker);
	}
	
	@Override
	public void updateHp()
	{
		super.updateHp();
		final Guild owner = getGuildOwner();
		
		if (owner != null)
		{
			owner.sendPacket(null, TargetHp.getInstance(this, TargetHp.BLUE));
		}
	}
}

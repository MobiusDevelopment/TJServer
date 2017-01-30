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
import tera.gameserver.model.npc.playable.PlayerKiller;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.templates.NpcTemplate;

/**
 * @author Ronn
 */
public class RegionWarDefense extends PlayerKiller implements RegionWarNpc
{
	private Region region;
	
	/**
	 * Constructor for RegionWarDefense.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public RegionWarDefense(int objectId, NpcTemplate template)
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
		if (target.getClass() == RegionWarControl.class)
		{
			final RegionWarControl control = (RegionWarControl) target;
			return control.getGuildOwner() != null;
		}
		
		if (target.isNpc() && !target.isSummon())
		{
			return false;
		}
		
		Player player = null;
		
		if (target.isSummon())
		{
			final Character owner = target.getOwner();
			
			if ((owner != null) && owner.isPlayer())
			{
				player = owner.getPlayer();
			}
		}
		else if (target.isPlayer())
		{
			player = target.getPlayer();
		}
		
		return player != null;
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
		Player player = null;
		
		if (attacker.isSummon())
		{
			final Character owner = attacker.getOwner();
			
			if ((owner != null) && owner.isPlayer())
			{
				player = owner.getPlayer();
			}
		}
		else if (attacker.isPlayer())
		{
			player = attacker.getPlayer();
		}
		
		if (player == null)
		{
			return;
		}
		
		final Guild guild = player.getGuild();
		
		if ((guild == null) || !getRegion().isRegister(guild))
		{
			return;
		}
		
		super.causingDamage(skill, info, attacker);
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
				
				case MYSTIC:
				case PRIEST:
					aggro *= 3;
					break;
				
				default:
					break;
			}
		}
		
		super.addAggro(aggressor, aggro, damage);
	}
	
	/**
	 * Method setGuildOwner.
	 * @param guild Guild
	 * @see tera.gameserver.events.global.regionwars.RegionWarNpc#setGuildOwner(Guild)
	 */
	@Override
	public void setGuildOwner(Guild guild)
	{
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
		return null;
	}
}
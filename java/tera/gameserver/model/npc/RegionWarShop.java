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
import tera.gameserver.model.Guild;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.templates.NpcTemplate;

/**
 * @author Ronn
 */
public class RegionWarShop extends FriendNpc implements TaxationNpc, RegionWarNpc
{
	private Region region;
	
	/**
	 * Constructor for RegionWarShop.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public RegionWarShop(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method getTax.
	 * @return int
	 * @see tera.gameserver.model.npc.TaxationNpc#getTax()
	 */
	@Override
	public int getTax()
	{
		final Region region = getRegion();
		
		if (region == null)
		{
			log.warning("not found region for NPC " + getTemplateId() + " - " + getTemplateType());
			return 0;
		}
		
		final Guild owner = region.getOwner();
		return owner == null ? 0 : region.getTax();
	}
	
	/**
	 * Method getTaxBank.
	 * @return Bank
	 * @see tera.gameserver.model.npc.TaxationNpc#getTaxBank()
	 */
	@Override
	public Bank getTaxBank()
	{
		final Region region = getRegion();
		
		if (region == null)
		{
			log.warning("not found region for NPC " + getTemplateId() + " - " + getTemplateType());
			return null;
		}
		
		final Guild owner = region.getOwner();
		return owner == null ? null : owner.getBank();
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
	 * Method setGuildOwner.
	 * @param guild Guild
	 * @see tera.gameserver.events.global.regionwars.RegionWarNpc#setGuildOwner(Guild)
	 */
	@Override
	public void setGuildOwner(Guild guild)
	{
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
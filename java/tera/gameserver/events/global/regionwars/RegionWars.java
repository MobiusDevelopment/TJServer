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
package tera.gameserver.events.global.regionwars;

import java.io.File;

import tera.Config;
import tera.gameserver.document.DocumentRegionWar;
import tera.gameserver.events.EventType;
import tera.gameserver.events.global.AbstractGlobalEvent;
import tera.gameserver.model.Guild;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class RegionWars extends AbstractGlobalEvent
{
	public static final String BATTLE_POINT = "region_war_battle_point";
	public static final int REWARD_INTERVAL = 300000;
	private static final String EVENT_NANE = "Region Wars";
	private final Array<Guild> registerGuilds;
	private final Array<Guild> ownerGuilds;
	private Region[] regions;
	
	public RegionWars()
	{
		registerGuilds = Arrays.toConcurrentArraySet(Guild.class);
		ownerGuilds = Arrays.toConcurrentArraySet(Guild.class);
	}
	
	/**
	 * Method addLinks.
	 * @param links Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	@Override
	public void addLinks(Array<Link> links, Npc npc, Player player)
	{
		final Region[] regions = getRegions();
		
		for (Region region : regions)
		{
			region.addLinks(links, npc, player);
		}
	}
	
	/**
	 * Method addOwnerGuild.
	 * @param guild Guild
	 */
	public void addOwnerGuild(Guild guild)
	{
		ownerGuilds.add(guild);
	}
	
	/**
	 * Method addRegisterGuild.
	 * @param guild Guild
	 */
	public void addRegisterGuild(Guild guild)
	{
		final Array<Guild> registerGuilds = getRegisterGuilds();
		
		if (!registerGuilds.contains(guild))
		{
			registerGuilds.add(guild);
		}
	}
	
	/**
	 * Method getRegisterGuilds.
	 * @return Array<Guild>
	 */
	public Array<Guild> getRegisterGuilds()
	{
		return registerGuilds;
	}
	
	/**
	 * Method getName.
	 * @return String
	 * @see tera.gameserver.events.Event#getName()
	 */
	@Override
	public String getName()
	{
		return EVENT_NANE;
	}
	
	/**
	 * Method getRegions.
	 * @return Region[]
	 */
	public Region[] getRegions()
	{
		return regions;
	}
	
	/**
	 * Method getType.
	 * @return EventType
	 * @see tera.gameserver.events.Event#getType()
	 */
	@Override
	public EventType getType()
	{
		return EventType.REGION_WARS;
	}
	
	/**
	 * Method isOwnerGuild.
	 * @param guild Guild
	 * @return boolean
	 */
	public boolean isOwnerGuild(Guild guild)
	{
		return ownerGuilds.contains(guild);
	}
	
	/**
	 * Method isRegisterGuild.
	 * @param guild Guild
	 * @return boolean
	 */
	public boolean isRegisterGuild(Guild guild)
	{
		return registerGuilds.contains(guild);
	}
	
	/**
	 * Method onLoad.
	 * @return boolean
	 * @see tera.gameserver.events.Event#onLoad()
	 */
	@Override
	public boolean onLoad()
	{
		final Array<Region> regions = Arrays.toArray(Region.class);
		regions.addAll(new DocumentRegionWar(new File(Config.SERVER_DIR + "/data/events/region_wars/region_wars.xml"), this).parse());
		regions.trimToSize();
		setRegions(regions.array());
		
		for (Region region : regions)
		{
			region.prepare();
		}
		
		return true;
	}
	
	/**
	 * Method removeOwnerGuild.
	 * @param guild Guild
	 */
	public void removeOwnerGuild(Guild guild)
	{
		ownerGuilds.fastRemove(guild);
	}
	
	/**
	 * Method removeRegisterGuild.
	 * @param guild Guild
	 */
	public void removeRegisterGuild(Guild guild)
	{
		registerGuilds.fastRemove(guild);
	}
	
	/**
	 * Method setRegions.
	 * @param region Region[]
	 */
	private void setRegions(Region[] region)
	{
		regions = region;
	}
}
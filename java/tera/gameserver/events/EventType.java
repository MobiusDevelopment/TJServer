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
package tera.gameserver.events;

import tera.gameserver.events.auto.EpicBattle;
import tera.gameserver.events.auto.LastHero;
import tera.gameserver.events.auto.TeamDeathMatch;
import tera.gameserver.events.auto.TeamVsTeam;
import tera.gameserver.events.global.regionwars.RegionWars;

/**
 * @author Ronn
 * @created 04.03.2012
 */
public enum EventType
{
	TEAM_VS_TEAM(new TeamVsTeam()),
	LAST_HERO(new LastHero()),
	
	TEAM_VS_MONSTERS(new EpicBattle()),
	REGION_WARS(new RegionWars()),
	TEAM_DEATH_MATCH(new TeamDeathMatch()),;
	
	private Event event;
	
	/**
	 * Constructor for EventType.
	 * @param evt Event
	 */
	private EventType(Event evt)
	{
		event = evt;
	}
	
	/**
	 * Method get.
	 * @return Event
	 */
	public Event get()
	{
		return event;
	}
}

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
package tera.gameserver.model.territory;

import org.w3c.dom.Node;

import tera.gameserver.events.global.regionwars.Region;
import tera.gameserver.events.global.regionwars.RegionState;
import tera.gameserver.model.Guild;
import tera.gameserver.model.TObject;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class RegionTerritory extends AbstractTerritory
{
	private Region region;
	
	/**
	 * Constructor for RegionTerritory.
	 * @param node Node
	 * @param type TerritoryType
	 */
	public RegionTerritory(Node node, TerritoryType type)
	{
		super(node, type);
	}
	
	/**
	 * Method getObjects.
	 * @param <T>
	 * @param container Array<T>
	 * @param type Class<T>
	 * @return Array<T>
	 */
	public <T extends TObject> Array<T> getObjects(Array<T> container, Class<T> type)
	{
		final WorldRegion[] regions = getRegions();
		
		for (WorldRegion region2 : regions)
		{
			region2.addObjects(container, type);
		}
		
		return container;
	}
	
	/**
	 * Method getRegion.
	 * @return Region
	 */
	public Region getRegion()
	{
		return region;
	}
	
	/**
	 * Method setRegion.
	 * @param region Region
	 */
	public void setRegion(Region region)
	{
		this.region = region;
	}
	
	/**
	 * Method onEnter.
	 * @param object TObject
	 * @see tera.gameserver.model.territory.Territory#onEnter(TObject)
	 */
	@Override
	public void onEnter(TObject object)
	{
		if (!object.isPlayer())
		{
			return;
		}
		
		final Player player = object.getPlayer();
		final Region region = getRegion();
		
		if ((region == null) || (region.getState() != RegionState.PREPARE_START_WAR))
		{
			return;
		}
		
		final Guild owner = region.getOwner();
		
		if (owner == null)
		{
			player.sendMessage("You entered a neutral region.");
		}
		else if (owner == player.getGuild())
		{
			player.sendMessage("You are logged in your region.");
			region.addFuncsTo(player);
		}
		else
		{
			player.sendMessage("You entered a region that belongs to \"" + owner.getName() + "\".");
			region.addFuncsTo(player);
		}
	}
	
	/**
	 * Method onExit.
	 * @param object TObject
	 * @see tera.gameserver.model.territory.Territory#onExit(TObject)
	 */
	@Override
	public void onExit(TObject object)
	{
		if (!object.isPlayer())
		{
			return;
		}
		
		final Player player = object.getPlayer();
		final Region region = getRegion();
		
		if ((region == null) || (region.getState() != RegionState.PREPARE_START_WAR))
		{
			return;
		}
		
		final Guild owner = region.getOwner();
		
		if (owner == null)
		{
			player.sendMessage("You came out of the neutral region.");
		}
		else if (owner == player.getGuild())
		{
			player.sendMessage("You went out of their region.");
			region.removeFuncsTo(player);
		}
		else
		{
			player.sendMessage("You came out of the region that belongs to \"" + owner.getName() + "\".");
			region.removeFuncsTo(player);
		}
	}
}
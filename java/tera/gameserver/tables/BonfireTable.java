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
package tera.gameserver.tables;

import tera.gameserver.model.TObject;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.territory.BonfireTerritory;
import tera.gameserver.model.territory.Territory;
import tera.gameserver.model.territory.TerritoryType;

import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class BonfireTable
{
	private static BonfireTerritory[] array;
	
	/**
	 * Method addBonfire.
	 * @param bonfire BonfireTerritory
	 */
	public static void addBonfire(BonfireTerritory bonfire)
	{
		array = Arrays.addToArray(array, bonfire, BonfireTerritory.class);
	}
	
	/**
	 * Method getNearBonfire.
	 * @param object TObject
	 * @return BonfireTerritory
	 */
	public static BonfireTerritory getNearBonfire(TObject object)
	{
		final WorldRegion region = object.getCurrentRegion();
		BonfireTerritory near = null;
		float dist = 0;
		
		if ((region != null) && region.hasTerritories())
		{
			final Territory[] terrs = region.getTerritories();
			
			for (Territory terr : terrs)
			{
				if (terr.getType() != TerritoryType.CAMP_TERRITORY)
				{
					continue;
				}
				
				final BonfireTerritory bonfire = (BonfireTerritory) terr;
				final float newDist = object.getDistance(bonfire.getCenterX(), bonfire.getCenterY(), bonfire.getCenterZ());
				
				if ((near == null) || (newDist < dist))
				{
					near = bonfire;
					dist = newDist;
				}
			}
			
			if (near != null)
			{
				return near;
			}
		}
		
		for (BonfireTerritory terr : array)
		{
			final float newDist = object.getDistance(terr.getCenterX(), terr.getCenterY(), terr.getCenterZ());
			
			if ((near == null) || (newDist < dist))
			{
				near = terr;
				dist = newDist;
			}
		}
		
		return near;
	}
}
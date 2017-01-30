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
package tera.gameserver.manager;

import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.util.Location;

import rlib.geom.Coords;

/**
 * @author Ronn
 */
public abstract class DebugManager
{
	/**
	 * Method showAreaDebug.
	 * @param continentId int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param radius float
	 */
	public static void showAreaDebug(int continentId, float targetX, float targetY, float targetZ, float radius)
	{
		final Location[] locs = Coords.circularCoords(Location.class, targetX, targetY, targetZ, (int) radius, 10);
		final ItemTable itemTable = ItemTable.getInstance();
		final ItemTemplate template = itemTable.getItem(8007);
		
		for (int i = 0; i < 10; i++)
		{
			final Location loc = locs[i];
			loc.setContinentId(continentId);
			template.newInstance().spawnMe(loc);
		}
		
		template.newInstance().spawnMe(new Location(targetX, targetY, targetZ));
	}
}
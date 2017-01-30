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

import tera.gameserver.model.MessageType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.util.Location;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public final class LocalTerritory extends AbstractTerritory
{
	private final Location teleportLoc;
	
	/**
	 * Constructor for LocalTerritory.
	 * @param node Node
	 * @param type TerritoryType
	 */
	public LocalTerritory(Node node, TerritoryType type)
	{
		super(node, type);
		final VarTable vars = VarTable.newInstance(node);
		teleportLoc = new Location(vars.getFloat("x"), vars.getFloat("y"), vars.getFloat("z"), 0, getContinentId());
	}
	
	/**
	 * Method getTeleportLoc.
	 * @return Location
	 */
	public Location getTeleportLoc()
	{
		return teleportLoc;
	}
	
	/**
	 * Method onEnter.
	 * @param object TObject
	 * @see tera.gameserver.model.territory.Territory#onEnter(TObject)
	 */
	@Override
	public void onEnter(TObject object)
	{
		super.onEnter(object);
		
		if (object.isPlayer())
		{
			final Player player = object.getPlayer();
			
			if (!player.isWhetherIn(this))
			{
				synchronized (this)
				{
					if (!player.isWhetherIn(this))
					{
						player.storeTerritory(this, true);
						player.sendPacket(SystemMessage.getInstance(MessageType.DISCOVERED_SECTION_NAME).add("sectionName", name), true);
					}
				}
			}
		}
	}
}
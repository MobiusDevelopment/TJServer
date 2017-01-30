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

import tera.gameserver.model.TObject;

/**
 * @author Ronn
 * @created 26.03.2012
 */
public class PeaceTerritory extends AbstractTerritory
{
	public PeaceTerritory(Node node, TerritoryType type)
	{
		super(node, type);
	}
	
	@Override
	public void onEnter(TObject object)
	{
		super.onEnter(object);
		
		if (object.isPlayer())
		{
			object.getPlayer().sendMessage("You have entered a peace zone.");
		}
	}
	
	@Override
	public void onExit(TObject object)
	{
		super.onExit(object);
		
		if (object.isPlayer())
		{
			object.getPlayer().sendMessage("You left the peace zone.");
		}
	}
}
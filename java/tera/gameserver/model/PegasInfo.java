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
package tera.gameserver.model;

import org.w3c.dom.Node;

import tera.util.Location;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public final class PegasInfo
{
	private final Location portal;
	private final Location landing;
	private final int toLanding;
	private final int toPortal;
	private final int local;
	
	/**
	 * Constructor for PegasInfo.
	 * @param node Node
	 * @param continentId int
	 */
	public PegasInfo(Node node, int continentId)
	{
		landing = new Location();
		portal = new Location();
		final VarTable vars = VarTable.newInstance(node);
		toLanding = vars.getInteger("landing");
		toPortal = vars.getInteger("portal");
		local = vars.getInteger("local");
		
		for (Node nd = node.getFirstChild(); nd != null; nd = nd.getNextSibling())
		{
			if (nd.getNodeType() == Node.ELEMENT_NODE)
			{
				vars.parse(nd);
				final float x = vars.getFloat("x");
				final float y = vars.getFloat("y");
				final float z = vars.getFloat("z");
				
				if ("portal".equals(nd.getNodeName()))
				{
					portal.setXYZ(x, y, z);
					portal.setContinentId(continentId);
				}
				else if ("landing".equals(nd.getNodeName()))
				{
					landing.setXYZ(x, y, z);
					landing.setContinentId(continentId);
				}
			}
		}
	}
	
	/**
	 * Method getLanding.
	 * @return Location
	 */
	public final Location getLanding()
	{
		return landing;
	}
	
	/**
	 * Method getLocal.
	 * @return int
	 */
	public final int getLocal()
	{
		return local;
	}
	
	/**
	 * Method getPortal.
	 * @return Location
	 */
	public final Location getPortal()
	{
		return portal;
	}
	
	/**
	 * Method getToLanding.
	 * @return int
	 */
	public final int getToLanding()
	{
		return toLanding;
	}
	
	/**
	 * Method getToPortal.
	 * @return int
	 */
	public final int getToPortal()
	{
		return toPortal;
	}
}
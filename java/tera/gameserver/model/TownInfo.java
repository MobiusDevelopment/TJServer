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
public final class TownInfo
{
	private final String name;
	private final PegasInfo pegasInfo;
	private final Location center;
	private final int id;
	private final int zone;
	
	/**
	 * Constructor for TownInfo.
	 * @param node Node
	 */
	public TownInfo(Node node)
	{
		final VarTable set = VarTable.newInstance(node);
		name = set.getString("name");
		id = set.getInteger("id");
		zone = set.getInteger("zone");
		final int continentId = set.getInteger("continentId", 0);
		PegasInfo pegasInfo = null;
		Location center = null;
		
		for (Node nd = node.getFirstChild(); nd != null; nd = nd.getNextSibling())
		{
			if ("pegas".equals(nd.getNodeName()))
			{
				pegasInfo = new PegasInfo(nd, continentId);
			}
			else if ("center".equals(nd.getNodeName()))
			{
				set.parse(nd);
				final float x = set.getFloat("x");
				final float y = set.getFloat("y");
				final float z = set.getFloat("z");
				center = new Location(x, y, z, 0, continentId);
			}
		}
		
		this.pegasInfo = pegasInfo;
		this.center = center;
	}
	
	/**
	 * Method getCenter.
	 * @return Location
	 */
	public Location getCenter()
	{
		return center;
	}
	
	/**
	 * Method getId.
	 * @return int
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Method getLanding.
	 * @return Location
	 */
	public final Location getLanding()
	{
		return pegasInfo.getLanding();
	}
	
	/**
	 * Method getLocal.
	 * @return int
	 */
	public final int getLocal()
	{
		return pegasInfo.getLocal();
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Method getPegasInfo.
	 * @return PegasInfo
	 */
	public PegasInfo getPegasInfo()
	{
		return pegasInfo;
	}
	
	/**
	 * Method getPortal.
	 * @return Location
	 */
	public final Location getPortal()
	{
		return pegasInfo.getPortal();
	}
	
	/**
	 * Method getToLanding.
	 * @return int
	 */
	public final int getToLanding()
	{
		return pegasInfo.getToLanding();
	}
	
	/**
	 * Method getToPortal.
	 * @return int
	 */
	public final int getToPortal()
	{
		return pegasInfo.getToPortal();
	}
	
	/**
	 * Method getZone.
	 * @return int
	 */
	public int getZone()
	{
		return zone;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "TownInfo  name = " + name + ", id = " + id + ", zone = " + zone;
	}
}
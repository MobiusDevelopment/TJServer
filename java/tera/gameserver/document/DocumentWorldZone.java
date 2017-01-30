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
package tera.gameserver.document;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import tera.gameserver.model.WorldZone;
import tera.util.Location;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 08.03.2012
 */
public final class DocumentWorldZone extends AbstractDocument<Array<WorldZone>>
{
	/**
	 * Constructor for DocumentWorldZone.
	 * @param file File
	 */
	public DocumentWorldZone(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<WorldZone>
	 */
	@Override
	protected Array<WorldZone> create()
	{
		return Arrays.toArray(WorldZone.class);
	}
	
	/**
	 * Method parse.
	 * @param arg0 Document
	 */
	@Override
	protected void parse(Document arg0)
	{
		for (Node lst = doc.getFirstChild(); lst != null; lst = lst.getNextSibling())
		{
			if ("list".equals(lst.getNodeName()))
			{
				for (Node npc = lst.getFirstChild(); npc != null; npc = npc.getNextSibling())
				{
					if ("zone".equals(npc.getNodeName()))
					{
						final WorldZone zone = parseZone(npc);
						
						if (zone == null)
						{
							log.warning(this, new Exception("not found zone"));
							continue;
						}
						
						result.add(zone);
					}
				}
			}
		}
	}
	
	/**
	 * Method parsePoints.
	 * @param node Node
	 * @param zone WorldZone
	 */
	private final void parsePoints(Node node, WorldZone zone)
	{
		final VarTable vars = VarTable.newInstance();
		
		for (Node point = node.getFirstChild(); point != null; point = point.getNextSibling())
		{
			if ((point.getNodeType() == Node.ELEMENT_NODE) && "point".equals(point.getNodeName()))
			{
				vars.parse(point);
				zone.addPoint(vars.getInteger("x"), vars.getInteger("y"));
			}
		}
	}
	
	/**
	 * Method parseRespawns.
	 * @param node Node
	 * @param zone WorldZone
	 */
	private final void parseRespawns(Node node, WorldZone zone)
	{
		final VarTable vars = VarTable.newInstance();
		
		for (Node respawn = node.getFirstChild(); respawn != null; respawn = respawn.getNextSibling())
		{
			if ((respawn.getNodeType() == Node.ELEMENT_NODE) && "respawn".equals(respawn.getNodeName()))
			{
				vars.parse(respawn);
				zone.addRespawnPoint(new Location(vars.getFloat("x"), vars.getFloat("y"), vars.getFloat("z"), 0, zone.getContinentId()));
			}
		}
	}
	
	/**
	 * Method parseZone.
	 * @param node Node
	 * @return WorldZone
	 */
	private final WorldZone parseZone(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final WorldZone zone = new WorldZone(vars.getInteger("maxZ"), vars.getInteger("minZ"), vars.getInteger("zoneId"), vars.getInteger("continentId", 0));
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			else if ("points".equals(child.getNodeName()))
			{
				parsePoints(child, zone);
			}
			else if ("respawns".equals(child.getNodeName()))
			{
				parseRespawns(child, zone);
			}
		}
		
		return zone;
	}
}
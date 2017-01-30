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

import tera.gameserver.model.resourse.ResourseSpawn;
import tera.gameserver.tables.ResourseTable;
import tera.gameserver.templates.ResourseTemplate;
import tera.util.Location;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 16.03.2012
 */
public final class DocumentResourseSpawn extends AbstractDocument<Array<ResourseSpawn>>
{
	/**
	 * Constructor for DocumentResourseSpawn.
	 * @param file File
	 */
	public DocumentResourseSpawn(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<ResourseSpawn>
	 */
	@Override
	protected Array<ResourseSpawn> create()
	{
		return result = Arrays.toArray(ResourseSpawn.class);
	}
	
	/**
	 * Method parse.
	 * @param doc Document
	 */
	@Override
	protected void parse(Document doc)
	{
		for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
		{
			if ("list".equals(list.getNodeName()))
			{
				for (Node spawns = list.getFirstChild(); spawns != null; spawns = spawns.getNextSibling())
				{
					if ("resourse".equals(spawns.getNodeName()))
					{
						parseSpawns(spawns);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseSpawns.
	 * @param node Node
	 */
	private final void parseSpawns(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final int templateId = vars.getInteger("templateId");
		final ResourseTable resourseTable = ResourseTable.getInstance();
		final ResourseTemplate template = resourseTable.getTemplate(templateId);
		
		if (template == null)
		{
			log.warning(this, new Exception("not found resourse template for templateId " + templateId));
			return;
		}
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("time".equals(child.getNodeName()))
			{
				parseTimeSpawns(child, template);
			}
		}
	}
	
	/**
	 * Method parseSpawns.
	 * @param node Node
	 * @param template ResourseTemplate
	 * @param continentId int
	 * @param respawn int
	 * @param randomRespawn int
	 */
	private final void parseSpawns(Node node, ResourseTemplate template, int continentId, int respawn, int randomRespawn)
	{
		final VarTable vars = VarTable.newInstance(node);
		final float x = vars.getFloat("x");
		final float y = vars.getFloat("y");
		final float z = vars.getFloat("z");
		final int min = vars.getInteger("min", 0);
		final int max = vars.getInteger("max", 0);
		result.add(new ResourseSpawn(template, new Location(x, y, z, 0, continentId), respawn, randomRespawn, min, max));
	}
	
	/**
	 * Method parseTimeSpawns.
	 * @param node Node
	 * @param template ResourseTemplate
	 */
	private final void parseTimeSpawns(Node node, ResourseTemplate template)
	{
		final VarTable vars = VarTable.newInstance(node);
		final int respawn = vars.getInteger("respawn", -1);
		final int random = vars.getInteger("random", -1);
		final int continentId = vars.getInteger("continentId", 0);
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("point".equals(child.getNodeName()))
			{
				parseSpawns(child, template, continentId, respawn, random);
			}
		}
	}
}
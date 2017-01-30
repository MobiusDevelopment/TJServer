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

import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.NpcType;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.tables.ConfigAITable;
import tera.gameserver.tables.NpcTable;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 16.03.2012
 */
public final class DocumentNpcSpawn extends AbstractDocument<Array<Spawn>>
{
	/**
	 * Constructor for DocumentNpcSpawn.
	 * @param file File
	 */
	public DocumentNpcSpawn(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<Spawn>
	 */
	@Override
	protected Array<Spawn> create()
	{
		return result = Arrays.toArray(Spawn.class);
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
				for (Node node = list.getFirstChild(); node != null; node = node.getNextSibling())
				{
					if ("npc".equals(node.getNodeName()))
					{
						parseSpawns(node);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseAiSpawns.
	 * @param node Node
	 * @param template NpcTemplate
	 * @param continentId int
	 */
	private final void parseAiSpawns(Node node, NpcTemplate template, int continentId)
	{
		final VarTable vars = VarTable.newInstance(node);
		final ConfigAITable configTable = ConfigAITable.getInstance();
		final NpcAIClass aiClass = vars.getEnum("class", NpcAIClass.class);
		final ConfigAI config = configTable.getConfig(vars.getString("config"));
		
		if (config == null)
		{
			log.warning(this, "not found config AI " + vars.getString("config") + " in file " + file);
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
				parseTimeSpawns(child, template, aiClass, config, continentId);
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
		final int npcId = vars.getInteger("id");
		final int type = vars.getInteger("type");
		final int continentId = vars.getInteger("continentId", 0);
		final NpcTable npcTable = NpcTable.getInstance();
		final NpcTemplate template = npcTable.getTemplate(npcId, type);
		
		if (template == null)
		{
			log.warning(this, new Exception("not found npc template for id " + npcId + ", type " + type));
			return;
		}
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ((child.getNodeType() == Node.ELEMENT_NODE) && "ai".equals(child.getNodeName()))
			{
				parseAiSpawns(child, template, continentId);
			}
		}
	}
	
	/**
	 * Method parseSpawns.
	 * @param node Node
	 * @param template NpcTemplate
	 * @param aiClass NpcAIClass
	 * @param config ConfigAI
	 * @param continentId int
	 * @param respawn int
	 * @param randomRespawn int
	 */
	private final void parseSpawns(Node node, NpcTemplate template, NpcAIClass aiClass, ConfigAI config, int continentId, int respawn, int randomRespawn)
	{
		final VarTable vars = VarTable.newInstance(node);
		final float x = vars.getFloat("x");
		final float y = vars.getFloat("y");
		final float z = vars.getFloat("z");
		final int heading = vars.getInteger("heading", -1);
		final int min = vars.getInteger("min", 0);
		final int max = vars.getInteger("max", 0);
		final NpcType type = template.getNpcType();
		result.add(type.newSpawn(node, vars, template, new Location(x, y, z, heading, continentId), respawn, randomRespawn, min, max, config, aiClass));
	}
	
	/**
	 * Method parseTimeSpawns.
	 * @param node Node
	 * @param template NpcTemplate
	 * @param aiClass NpcAIClass
	 * @param config ConfigAI
	 * @param continentId int
	 */
	private final void parseTimeSpawns(Node node, NpcTemplate template, NpcAIClass aiClass, ConfigAI config, int continentId)
	{
		final VarTable vars = VarTable.newInstance(node);
		final int respawn = vars.getInteger("respawn", -1);
		final int random = vars.getInteger("random", -1);
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("point".equals(child.getNodeName()))
			{
				parseSpawns(child, template, aiClass, config, continentId, respawn, random);
			}
		}
	}
}
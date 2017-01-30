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

import tera.gameserver.model.MinionData;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.spawn.MinionSpawn;
import tera.gameserver.tables.ConfigAITable;
import tera.gameserver.tables.NpcTable;
import tera.gameserver.templates.NpcTemplate;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 14.03.2012
 */
public final class DocumentMinion extends AbstractDocument<Array<MinionData>>
{
	/**
	 * Constructor for DocumentMinion.
	 * @param file File
	 */
	public DocumentMinion(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<MinionData>
	 */
	@Override
	protected Array<MinionData> create()
	{
		return Arrays.toArray(MinionData.class);
	}
	
	/**
	 * Method parse.
	 * @param doc Document
	 */
	@Override
	protected void parse(Document doc)
	{
		for (Node lst = doc.getFirstChild(); lst != null; lst = lst.getNextSibling())
		{
			if ("list".equals(lst.getNodeName()))
			{
				for (Node npc = lst.getFirstChild(); npc != null; npc = npc.getNextSibling())
				{
					if ("npc".equals(npc.getNodeName()))
					{
						final MinionData data = parseMinionData(npc);
						
						if (data == null)
						{
							continue;
						}
						
						result.add(data);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseMinionData.
	 * @param nodes Node
	 * @return MinionData
	 */
	private final MinionData parseMinionData(Node nodes)
	{
		final VarTable vals = VarTable.newInstance(nodes);
		final int leaderId = vals.getInteger("id");
		final int leaderType = vals.getInteger("type");
		final int respawn = vals.getInteger("respawn");
		final NpcTable npcTable = NpcTable.getInstance();
		final ConfigAITable configTable = ConfigAITable.getInstance();
		final NpcTemplate leaderTemplate = npcTable.getTemplate(leaderId, leaderType);
		
		if (leaderTemplate == null)
		{
			log.warning(this, "not found leader id " + leaderId + ", type " + leaderType);
			return null;
		}
		
		final Array<MinionSpawn> spawns = Arrays.toArray(MinionSpawn.class);
		final VarTable vars = VarTable.newInstance();
		
		for (Node node = nodes.getFirstChild(); node != null; node = node.getNextSibling())
		{
			if ((node.getNodeType() == Node.ELEMENT_NODE) && "minion".equals(node.getNodeName()))
			{
				vars.parse(node);
				final NpcTemplate template = npcTable.getTemplate(vars.getInteger("id"), vars.getInteger("type"));
				
				if (template == null)
				{
					continue;
				}
				
				final ConfigAI config = configTable.getConfig(vars.getString("aiConfig"));
				
				if (config == null)
				{
					log.warning(this, "not found ai config for " + vars.getString("aiConfig"));
					continue;
				}
				
				final NpcAIClass aiClass = vars.getEnum("aiClass", NpcAIClass.class);
				final MinionSpawn spawn = new MinionSpawn(template);
				spawn.setCount(vars.getInteger("count", 1));
				spawn.setRadius(vars.getInteger("radius", 60));
				spawn.setConfig(config);
				spawn.setAiClass(aiClass);
				spawns.add(spawn);
			}
		}
		
		if (spawns.isEmpty())
		{
			return null;
		}
		
		spawns.trimToSize();
		return new MinionData(spawns.array(), leaderId, leaderType, respawn);
	}
}
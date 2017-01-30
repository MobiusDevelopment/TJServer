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
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import tera.gameserver.model.drop.Drop;
import tera.gameserver.model.drop.DropGroup;
import tera.gameserver.model.drop.DropInfo;
import tera.gameserver.model.drop.NpcDrop;
import tera.gameserver.model.drop.ResourseDrop;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 17.03.2012
 */
public final class DocumentDrop extends AbstractDocument<Array<Drop>>
{
	public static final Set<Integer> filter = new HashSet<>();
	
	/**
	 * Constructor for DocumentDrop.
	 * @param file File
	 */
	public DocumentDrop(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<Drop>
	 */
	@Override
	protected Array<Drop> create()
	{
		return Arrays.toArray(Drop.class);
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
				for (Node dp = lst.getFirstChild(); dp != null; dp = dp.getNextSibling())
				{
					if ("drop".equals(dp.getNodeName()))
					{
						final Drop drop = parseDrop(dp);
						
						if (drop == null)
						{
							continue;
						}
						
						result.add(drop);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseDrop.
	 * @param nodes Node
	 * @return Drop
	 */
	private final Drop parseDrop(Node nodes)
	{
		final VarTable vars = VarTable.newInstance(nodes);
		final int templateId = vars.getInteger("templateId");
		final int templateType = vars.getInteger("templateType", -1);
		final Array<DropGroup> groups = Arrays.toArray(DropGroup.class);
		
		for (Node child = nodes.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("group".equals(child.getNodeName()))
			{
				final DropGroup group = parseGroup(child);
				
				if (group != null)
				{
					groups.add(group);
				}
			}
		}
		
		if (groups.isEmpty())
		{
			return null;
		}
		
		groups.trimToSize();
		return templateType == -1 ? new ResourseDrop(templateId, groups.array()) : new NpcDrop(templateId, templateType, groups.array());
	}
	
	/**
	 * Method parseGroup.
	 * @param node Node
	 * @return DropGroup
	 */
	private final DropGroup parseGroup(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final int id = vars.getInteger("id");
		final int chance = vars.getInteger("chance");
		final int count = vars.getInteger("count", 1);
		final Array<DropInfo> items = Arrays.toArray(DropInfo.class);
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("item".equals(child.getNodeName()))
			{
				final DropInfo item = parseItem(child);
				
				if (item != null)
				{
					items.add(item);
				}
			}
		}
		
		if (items.isEmpty())
		{
			return null;
		}
		
		items.trimToSize();
		return new DropGroup(id, chance, count, items.array());
	}
	
	/**
	 * Method parseItem.
	 * @param node Node
	 * @return DropInfo
	 */
	private final DropInfo parseItem(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final int id = vars.getInteger("templateId");
		final int min = vars.getInteger("min");
		final int max = vars.getInteger("max");
		final int chance = vars.getInteger("chance");
		final ItemTable itemTable = ItemTable.getInstance();
		final ItemTemplate template = itemTable.getItem(id);
		
		if (template == null)
		{
			filter.add(id);
			return null;
		}
		
		return new DropInfo(template, min, max, chance);
	}
}
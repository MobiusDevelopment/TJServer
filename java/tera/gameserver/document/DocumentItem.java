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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tera.gameserver.model.items.ItemClass;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.parser.FuncParser;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.data.AbstractDocument;
import rlib.util.Strings;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 17.03.2012
 */
public final class DocumentItem extends AbstractDocument<Array<ItemTemplate>>
{
	/**
	 * Constructor for DocumentItem.
	 * @param file File
	 */
	public DocumentItem(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<ItemTemplate>
	 */
	@Override
	protected Array<ItemTemplate> create()
	{
		return Arrays.toArray(ItemTemplate.class);
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
				for (Node temp = lst.getFirstChild(); temp != null; temp = temp.getNextSibling())
				{
					final ItemClass itemClass = ItemClass.valueOfXml(temp.getNodeName());
					
					if (itemClass == null)
					{
						continue;
					}
					
					final Array<SkillTemplate> skills = Arrays.toArray(SkillTemplate.class);
					final Array<Func> funcs = Arrays.toArray(Func.class);
					final VarTable vars = VarTable.newInstance(temp);
					vars.set("class", itemClass);
					final ItemTemplate template = itemClass.newTemplate(vars);
					
					if (template == null)
					{
						continue;
					}
					
					skills.addAll(SkillTable.parseSkills(vars.getString("skills", Strings.EMPTY), template.getClassIdItemSkill()));
					
					for (Node child = temp.getFirstChild(); child != null; child = child.getNextSibling())
					{
						switch (child.getNodeName())
						{
							case "skills":
								parseSkills(child, skills);
								break;
							
							case "funcs":
								parseFuncs(child, funcs);
								break;
						}
					}
					
					skills.trimToSize();
					funcs.trimToSize();
					template.setSkills(skills.array());
					template.setFuncs(funcs.array());
					result.add(template);
				}
			}
		}
	}
	
	/**
	 * Method parseFuncs.
	 * @param node Node
	 * @param funcs Array<Func>
	 */
	private void parseFuncs(Node node, Array<Func> funcs)
	{
		final FuncParser parser = FuncParser.getInstance();
		parser.parse(node, funcs, file);
	}
	
	/**
	 * Method parseSkills.
	 * @param node Node
	 * @param skills Array<SkillTemplate>
	 */
	private void parseSkills(Node node, Array<SkillTemplate> skills)
	{
		final SkillTable skillTable = SkillTable.getInstance();
		
		for (Node temp = node.getFirstChild(); temp != null; temp = temp.getNextSibling())
		{
			if ("skill".equals(temp.getNodeName()))
			{
				final NamedNodeMap attrs = temp.getAttributes();
				final int templateId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
				final int classId = Integer.parseInt(attrs.getNamedItem("class").getNodeValue());
				final SkillTemplate skill = skillTable.getSkill(templateId, classId);
				
				if (skill != null)
				{
					skills.add(skill);
				}
			}
		}
	}
}
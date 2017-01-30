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

import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.base.Race;
import tera.gameserver.model.base.Sex;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.parser.FuncParser;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.templates.PlayerTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 16.03.2012
 */
public final class DocumentPlayer extends AbstractDocument<Array<PlayerTemplate>>
{
	/**
	 * Constructor for DocumentPlayer.
	 * @param file File
	 */
	public DocumentPlayer(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<PlayerTemplate>
	 */
	@Override
	protected Array<PlayerTemplate> create()
	{
		return Arrays.toArray(PlayerTemplate.class);
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
				for (Node template = list.getFirstChild(); template != null; template = template.getNextSibling())
				{
					if ("template".equals(template.getNodeName()))
					{
						parseTemplate(template);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseFuncs.
	 * @param node Node
	 * @return Func[]
	 */
	private final Func[] parseFuncs(Node node)
	{
		final Array<Func> array = Arrays.toArray(Func.class);
		final FuncParser parser = FuncParser.getInstance();
		parser.parse(node, array, file);
		array.trimToSize();
		return array.array();
	}
	
	/**
	 * Method parseItems.
	 * @param node Node
	 * @return int[][]
	 */
	private final int[][] parseItems(Node node)
	{
		final Array<int[]> items = Arrays.toArray(int[].class);
		final ItemTable itemTable = ItemTable.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ("item".equals(child.getNodeName()))
			{
				final VarTable vars = VarTable.newInstance(child);
				final int id = vars.getInteger("id");
				final int count = vars.getInteger("count");
				final ItemTemplate template = itemTable.getItem(id);
				
				if (template == null)
				{
					continue;
				}
				
				items.add(Arrays.toIntegerArray(id, count));
			}
		}
		
		items.trimToSize();
		return items.array();
	}
	
	/**
	 * Method parseSkills.
	 * @param node Node
	 * @return Array<SkillTemplate[]>
	 */
	private final Array<SkillTemplate[]> parseSkills(Node node)
	{
		final Array<SkillTemplate[]> skills = Arrays.toArray(SkillTemplate[].class, 2);
		final SkillTable skillTable = SkillTable.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ("skill".equals(child.getNodeName()))
			{
				final VarTable vars = VarTable.newInstance(child);
				final int id = vars.getInteger("id");
				final int classId = vars.getInteger("class");
				final SkillTemplate[] template = skillTable.getSkills(classId, id);
				
				if (template != null)
				{
					skills.add(template);
				}
			}
		}
		
		return skills;
	}
	
	/**
	 * Method parseSkills.
	 * @param node Node
	 * @param playerClass PlayerClass
	 * @return SkillTemplate[][]
	 */
	private final SkillTemplate[][] parseSkills(Node node, PlayerClass playerClass)
	{
		final Array<SkillTemplate[]> skills = Arrays.toArray(SkillTemplate[].class, 2);
		final SkillTable skillTable = SkillTable.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ("skill".equals(child.getNodeName()))
			{
				final VarTable vars = VarTable.newInstance(child);
				final int id = vars.getInteger("id");
				final SkillTemplate[] template = skillTable.getSkills(playerClass.getId(), id);
				
				if (template != null)
				{
					skills.add(template);
				}
			}
		}
		
		skills.trimToSize();
		return skills.array();
	}
	
	/**
	 * Method parseTemplate.
	 * @param node Node
	 */
	private final void parseTemplate(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final PlayerClass pclass = vars.getEnum("class", PlayerClass.class);
		VarTable set = null;
		int[][] items = null;
		SkillTemplate[][] skills = null;
		Func[] funcs = new Func[0];
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			else if ("stats".equals(child.getNodeName()))
			{
				set = VarTable.newInstance(child, "stat", "name", "val");
			}
			else if ("funcs".equals(child.getNodeName()))
			{
				funcs = parseFuncs(child);
			}
			else if ("items".equals(child.getNodeName()))
			{
				items = parseItems(child);
			}
			else if ("skills".equals(child.getNodeName()))
			{
				skills = parseSkills(child, pclass);
			}
			else if ("races".equals(child.getNodeName()))
			{
				parseTemplate(child, set, funcs, pclass, items, skills);
			}
		}
	}
	
	/**
	 * Method parseTemplate.
	 * @param node Node
	 * @param stats VarTable
	 * @param funcs Func[]
	 * @param playerClass PlayerClass
	 * @param items int[][]
	 * @param skills SkillTemplate[][]
	 */
	private final void parseTemplate(Node node, VarTable stats, Func[] funcs, PlayerClass playerClass, int[][] items, SkillTemplate[][] skills)
	{
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ("race".equals(child.getNodeName()))
			{
				final VarTable vars = VarTable.newInstance(child);
				final Race race = vars.getEnum("type", Race.class);
				Array<SkillTemplate[]> skillList = null;
				
				for (Node temp = child.getFirstChild(); temp != null; temp = temp.getNextSibling())
				{
					if ("skills".equals(temp.getNodeName()))
					{
						skillList = parseSkills(temp);
					}
				}
				
				// TODO: Check this.
				if (skillList == null)
				{
					return;
				}
				
				for (SkillTemplate[] skill : skills)
				{
					skillList.add(skill);
				}
				
				skillList.trimToSize();
				int modelId = vars.getInteger("male", -1);
				
				if (modelId != -1)
				{
					result.add(new PlayerTemplate(stats, funcs, playerClass, race, Sex.MALE, modelId, items, skillList.array()));
				}
				
				modelId = vars.getInteger("female", -1);
				
				if (modelId != -1)
				{
					result.add(new PlayerTemplate(stats, funcs, playerClass, race, Sex.FEMALE, modelId, items, skillList.array()));
				}
			}
		}
	}
}
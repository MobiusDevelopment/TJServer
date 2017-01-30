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

import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.parser.ConditionParser;
import tera.gameserver.parser.EffectParser;
import tera.gameserver.parser.FuncParser;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class DocumentSkill extends AbstractDocument<Array<SkillTemplate[]>>
{
	/**
	 * Constructor for DocumentSkill.
	 * @param file File
	 */
	public DocumentSkill(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<SkillTemplate[]>
	 */
	@Override
	protected Array<SkillTemplate[]> create()
	{
		return result = Arrays.toArray(SkillTemplate[].class);
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
				for (Node skill = list.getFirstChild(); skill != null; skill = skill.getNextSibling())
				{
					if ("skill".equals(skill.getNodeName()))
					{
						try
						{
							result.add(parseSkill(skill));
						}
						catch (Exception e)
						{
							log.warning(this, "incorrect file " + file + ", and skill " + skill.getAttributes().getNamedItem("id"));
							log.warning(this, e);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Method parseSkill.
	 * @param node Node
	 * @return SkillTemplate[]
	 */
	private SkillTemplate[] parseSkill(Node node)
	{
		SkillTemplate[] skills;
		final Table<String, String[]> table = Tables.newObjectTable();
		final Array<EffectTemplate> effects = Arrays.toArray(EffectTemplate.class);
		final Array<Func> passiveFuncs = Arrays.toArray(Func.class);
		final Array<Func> castFuncs = Arrays.toArray(Func.class);
		Condition condition = null;
		final VarTable attrs = VarTable.newInstance(node);
		final int id = attrs.getInteger("id");
		final int levels = attrs.getInteger("levels");
		final int classId = attrs.getInteger("class");
		final String skillName = attrs.getString("name");
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("table".equals(child.getNodeName()))
			{
				final Node item = child.getAttributes().getNamedItem("name");
				
				if (item == null)
				{
					continue;
				}
				
				final String name = item.getNodeValue();
				
				if (name == null)
				{
					continue;
				}
				
				final Node values = child.getFirstChild();
				
				if (values == null)
				{
					continue;
				}
				
				final String value = values.getNodeValue();
				
				if (value == null)
				{
					continue;
				}
				
				table.put(name, value.split(" "));
			}
		}
		
		skills = new SkillTemplate[levels];
		final VarTable vars = VarTable.newInstance();
		final ConditionParser condParser = ConditionParser.getInstance();
		final EffectParser effectParser = EffectParser.getInstance();
		final FuncParser funcParser = FuncParser.getInstance();
		
		for (int order = 0; order < levels; order++)
		{
			final VarTable stats = VarTable.newInstance();
			stats.set("id", (id + order));
			stats.set("level", 1 + order);
			stats.set("classId", classId);
			stats.set("name", skillName);
			passiveFuncs.clear();
			castFuncs.clear();
			effects.clear();
			condition = null;
			
			for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
			{
				if (child.getNodeType() != Node.ELEMENT_NODE)
				{
					continue;
				}
				
				if ("set".equals(child.getNodeName()))
				{
					vars.parse(child);
					final String name = vars.getString("name");
					String value = vars.getString("value");
					
					if (value.startsWith("#"))
					{
						final String[] array = table.get(value);
						value = array[Math.min(array.length - 1, order)];
					}
					
					stats.set(name, value);
				}
				else if ("cond".equals(child.getNodeName()))
				{
					for (Node condNode = child.getFirstChild(); condNode != null; condNode = condNode.getNextSibling())
					{
						if (condNode.getNodeType() != Node.ELEMENT_NODE)
						{
							continue;
						}
						
						final Condition cond = condParser.parseCondition(condNode, id + order, file);
						
						if (cond == null)
						{
							log.warning(this, new Exception("not found condition"));
							continue;
						}
						
						condition = condParser.joinAnd(condition, cond);
					}
				}
				else if ("cast".equals(child.getNodeName()))
				{
					funcParser.parse(child, castFuncs, table, order, id + order, file);
				}
				else if ("for".equals(child.getNodeName()))
				{
					funcParser.parse(child, passiveFuncs, table, order, id + order, file);
					
					for (Node added = child.getFirstChild(); added != null; added = added.getNextSibling())
					{
						if (added.getNodeType() != Node.ELEMENT_NODE)
						{
							continue;
						}
						
						if ("effect".equals(added.getNodeName()))
						{
							final EffectTemplate effect = effectParser.paraseEffects(order, added, table, id + order, file);
							
							if (effect == null)
							{
								log.warning(this, "not found effect to name " + added.getNodeName() + " on file" + file + ".");
								continue;
							}
							
							effects.add(effect);
						}
					}
				}
			}
			
			effects.trimToSize();
			passiveFuncs.trimToSize();
			castFuncs.trimToSize();
			skills[order] = new SkillTemplate(stats, Arrays.copyOf(effects.array(), 0), condition, Arrays.copyOf(passiveFuncs.array(), 0), Arrays.copyOf(castFuncs.array(), 0));
		}
		
		return skills;
	}
}

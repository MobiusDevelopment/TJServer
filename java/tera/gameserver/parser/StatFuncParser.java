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
package tera.gameserver.parser;

import java.io.File;

import org.w3c.dom.Node;

import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.skillengine.funcs.stat.FuncFactory;

import rlib.util.VarTable;
import rlib.util.array.Arrays;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public final class StatFuncParser
{
	private static final String[] STAT_FUNC_NAMES =
	{
		"add",
		"sub",
		"mul",
		"set",
		"div",
	};
	
	private static StatFuncParser instance;
	
	/**
	 * Method getInstance.
	 * @return StatFuncParser
	 */
	public static StatFuncParser getInstance()
	{
		if (instance == null)
		{
			instance = new StatFuncParser();
		}
		
		return instance;
	}
	
	/**
	 * Method isStatFunc.
	 * @param name String
	 * @return boolean
	 */
	public static boolean isStatFunc(String name)
	{
		return Arrays.contains(STAT_FUNC_NAMES, name);
	}
	
	/**
	 * Method getValue.
	 * @param order int
	 * @param table Table<String,String[]>
	 * @param value String
	 * @param skill int
	 * @param file File
	 * @return String
	 */
	private String getValue(int order, Table<String, String[]> table, String value, int skill, File file)
	{
		String val = null;
		
		if (!value.startsWith("#"))
		{
			val = value;
		}
		else
		{
			final String[] array = table.get(value);
			value = array[Math.min(array.length - 1, order)];
		}
		
		return val;
	}
	
	/**
	 * Method parseFunc.
	 * @param order int
	 * @param table Table<String,String[]>
	 * @param node Node
	 * @param skill int
	 * @param file File
	 * @return Func
	 */
	public Func parseFunc(int order, Table<String, String[]> table, Node node, int skill, File file)
	{
		final VarTable vars = VarTable.newInstance(node);
		final StatType stat = StatType.valueOfXml(vars.getString("stat"));
		final int ordinal = Integer.decode(vars.getString("order"));
		final String value = getValue(order, table, vars.getString("val"), skill, file);
		Condition cond = null;
		final ConditionParser parser = ConditionParser.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			cond = parser.parseCondition(child, skill, file);
			
			if (cond != null)
			{
				break;
			}
		}
		
		final FuncFactory funcFactory = FuncFactory.getInstance();
		return funcFactory.createFunc(node.getNodeName(), stat, ordinal, cond, value);
	}
	
	/**
	 * Method parseFunc.
	 * @param node Node
	 * @param file File
	 * @return Func
	 */
	public Func parseFunc(Node node, File file)
	{
		final VarTable vars = VarTable.newInstance(node);
		final StatType stat = StatType.valueOfXml(vars.getString("stat"));
		final int ordinal = Integer.decode(vars.getString("order"));
		Condition cond = null;
		final ConditionParser parser = ConditionParser.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			cond = parser.parseCondition(child, 0, file);
			
			if (cond != null)
			{
				break;
			}
		}
		
		final FuncFactory funcFactory = FuncFactory.getInstance();
		return funcFactory.createFunc(node.getNodeName(), stat, ordinal, cond, vars.getString("val"));
	}
}

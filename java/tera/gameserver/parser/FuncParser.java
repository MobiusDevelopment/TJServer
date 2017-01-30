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
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import tera.gameserver.model.skillengine.Condition;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.skillengine.funcs.chance.ChanceFunc;
import tera.gameserver.model.skillengine.funcs.task.TaskFunc;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public class FuncParser
{
	private static final Logger log = Loggers.getLogger(FuncParser.class);
	private static String[] FUNC_NAMES =
	{
		"task",
		"chance",
	};
	private static FuncParser instance;
	
	/**
	 * Method getInstance.
	 * @return FuncParser
	 */
	public static FuncParser getInstance()
	{
		if (instance == null)
		{
			instance = new FuncParser();
		}
		
		return instance;
	}
	
	/**
	 * Method isFunc.
	 * @param name String
	 * @return boolean
	 */
	public static boolean isFunc(String name)
	{
		return Arrays.contains(FUNC_NAMES, name) || StatFuncParser.isStatFunc(name);
	}
	
	private final Array<ChanceFunc> funcs;
	
	private FuncParser()
	{
		funcs = Arrays.toArray(ChanceFunc.class);
	}
	
	/**
	 * Method addChanceFunc.
	 * @param func ChanceFunc
	 */
	public void addChanceFunc(ChanceFunc func)
	{
		funcs.add(func);
	}
	
	/**
	 * Method parse.
	 * @param node Node
	 * @param container Array<Func>
	 * @param file File
	 */
	public void parse(Node node, Array<Func> container, File file)
	{
		final VarTable vars = VarTable.newInstance();
		final ConditionParser condParser = ConditionParser.getInstance();
		final StatFuncParser statFuncParser = StatFuncParser.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			final String name = child.getNodeName();
			
			if (StatFuncParser.isStatFunc(name))
			{
				final Func func = statFuncParser.parseFunc(child, file);
				
				if (func == null)
				{
					log.warning("not found func stat to name " + name + " on file" + file + ".");
					continue;
				}
				
				container.add(func);
			}
			else if ("task".equals(name))
			{
				vars.parse(child);
				
				try
				{
					final Func func = (Func) Class.forName(TaskFunc.class.getPackage().getName() + "." + vars.getString("name")).getConstructor(VarTable.class).newInstance(vars);
					container.add(func);
				}
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | DOMException e)
				{
					log.warning(e);
				}
			}
			else if ("chance".equals(name))
			{
				vars.parse(child);
				Condition cond = null;
				
				for (Node condNode = child.getFirstChild(); condNode != null; condNode = condNode.getNextSibling())
				{
					if (condNode.getNodeType() != Node.ELEMENT_NODE)
					{
						continue;
					}
					
					final Condition cnd = condParser.parseCondition(condNode, 0, file);
					
					if (cnd == null)
					{
						log.warning(new Exception("not found condition for " + condNode));
						continue;
					}
					
					cond = condParser.joinAnd(cond, cnd);
				}
				
				try
				{
					final Func func = (Func) Class.forName(ChanceFunc.class.getPackage().getName() + "." + vars.getString("name")).getConstructor(VarTable.class, Condition.class).newInstance(vars, cond);
					container.add(func);
				}
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | DOMException e)
				{
					log.warning(e);
				}
			}
		}
	}
	
	/**
	 * Method parse.
	 * @param node Node
	 * @param container Array<Func>
	 * @param table Table<String,String[]>
	 * @param order int
	 * @param skillId int
	 * @param file File
	 */
	public void parse(Node node, Array<Func> container, Table<String, String[]> table, int order, int skillId, File file)
	{
		final VarTable vars = VarTable.newInstance();
		final ConditionParser condParser = ConditionParser.getInstance();
		final StatFuncParser statFuncParser = StatFuncParser.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			final String name = child.getNodeName();
			
			if (StatFuncParser.isStatFunc(name))
			{
				final Func func = statFuncParser.parseFunc(order, table, child, skillId, file);
				
				if (func == null)
				{
					log.warning("not found func stat to name " + name + " on file" + file + ".");
					continue;
				}
				
				container.add(func);
			}
			else if ("task".equals(name))
			{
				vars.parse(child);
				
				try
				{
					final Func func = (Func) Class.forName(TaskFunc.class.getPackage().getName() + "." + vars.getString("name")).getConstructor(VarTable.class).newInstance(vars);
					container.add(func);
				}
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | DOMException e)
				{
					log.warning(e);
				}
			}
			else if ("chance".equals(name))
			{
				vars.parse(child);
				Condition cond = null;
				
				for (Node condNode = child.getFirstChild(); condNode != null; condNode = condNode.getNextSibling())
				{
					if (condNode.getNodeType() != Node.ELEMENT_NODE)
					{
						continue;
					}
					
					final Condition cnd = condParser.parseCondition(condNode, 0, file);
					
					if (cnd == null)
					{
						log.warning(new Exception("not found condition for " + condNode));
						continue;
					}
					
					cond = condParser.joinAnd(cond, cnd);
				}
				
				try
				{
					final Func func = (Func) Class.forName(ChanceFunc.class.getPackage().getName() + "." + vars.getString("name")).getConstructor(VarTable.class, Condition.class).newInstance(vars, cond);
					container.add(func);
				}
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | DOMException e)
				{
					log.warning(e);
				}
			}
		}
	}
	
	public void prepareChanceFunc()
	{
		for (ChanceFunc func : funcs)
		{
			func.prepare();
		}
	}
}
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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.templates.EffectTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public final class EffectParser
{
	private static final Logger log = Loggers.getLogger(EffectParser.class);
	
	private static EffectParser instance;
	
	/**
	 * Method getInstance.
	 * @return EffectParser
	 */
	public static EffectParser getInstance()
	{
		if (instance == null)
		{
			instance = new EffectParser();
		}
		
		return instance;
	}
	
	private EffectParser()
	{
		log.info("initialized.");
	}
	
	/**
	 * Method paraseEffects.
	 * @param order int
	 * @param node Node
	 * @param table Table<String,String[]>
	 * @param skillId int
	 * @param file File
	 * @return EffectTemplate
	 */
	public EffectTemplate paraseEffects(int order, Node node, Table<String, String[]> table, int skillId, File file)
	{
		final VarTable vars = VarTable.newInstance();
		final Array<Func> funcs = Arrays.toArray(Func.class);
		final NamedNodeMap vals = node.getAttributes();
		
		for (int i = 0; i < vals.getLength(); i++)
		{
			final Node item = vals.item(i);
			final String name = item.getNodeName();
			String value = item.getNodeValue();
			
			if (value.startsWith("#"))
			{
				final String[] array = table.get(value);
				value = array[Math.min(array.length - 1, order)];
			}
			
			vars.set(name, value);
		}
		
		final FuncParser funcManager = FuncParser.getInstance();
		funcManager.parse(node, funcs, table, order, skillId, file);
		funcs.trimToSize();
		return new EffectTemplate(vars, funcs.array());
	}
}

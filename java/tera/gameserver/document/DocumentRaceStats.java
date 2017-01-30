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
import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import tera.gameserver.model.base.Race;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.parser.FuncParser;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 16.03.2012
 */
public final class DocumentRaceStats extends AbstractDocument<Void>
{
	/**
	 * Constructor for DocumentRaceStats.
	 * @param file File
	 */
	public DocumentRaceStats(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Void
	 */
	@Override
	protected Void create()
	{
		return null;
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
				for (Node child = list.getFirstChild(); child != null; child = child.getNextSibling())
				{
					if ((child.getNodeType() == Node.ELEMENT_NODE) && "race".equals(child.getNodeName()))
					{
						parseRace(child);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseRace.
	 * @param node Node
	 */
	private final void parseRace(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final Race race = vars.getEnum("type", Race.class);
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("stats".equals(child.getNodeName()))
			{
				parseStats(child, race);
			}
			else if ("funcs".equals(child.getNodeName()))
			{
				parseFuncs(child, race);
			}
		}
	}
	
	/**
	 * Method parseStats.
	 * @param node Node
	 * @param race Race
	 */
	private void parseStats(Node node, Race race)
	{
		final VarTable vars = VarTable.newInstance(node, "stat", "name", "val");
		final Field[] fields = race.getClass().getDeclaredFields();
		
		try
		{
			for (Field field : fields)
			{
				if (field.getType() != float.class)
				{
					continue;
				}
				
				final boolean old = field.isAccessible();
				field.setAccessible(true);
				field.setFloat(race, vars.getFloat(field.getName(), 1F));
				field.setAccessible(old);
			}
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			log.warning(this, e);
		}
	}
	
	/**
	 * Method parseFuncs.
	 * @param node Node
	 * @param race Race
	 */
	private void parseFuncs(Node node, Race race)
	{
		final Array<Func> array = Arrays.toArray(Func.class);
		final FuncParser parser = FuncParser.getInstance();
		parser.parse(node, array, file);
		race.setFuncs(array);
	}
}
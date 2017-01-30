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

import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.parser.FuncParser;
import tera.gameserver.templates.NpcTemplate;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 16.03.2012
 */
public final class DocumentNpc extends AbstractDocument<Array<NpcTemplate>>
{
	/**
	 * Constructor for DocumentNpc.
	 * @param file File
	 */
	public DocumentNpc(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<NpcTemplate>
	 */
	@Override
	protected Array<NpcTemplate> create()
	{
		return Arrays.toArray(NpcTemplate.class);
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
				for (Node sp = lst.getFirstChild(); sp != null; sp = sp.getNextSibling())
				{
					if ("template".equals(sp.getNodeName()))
					{
						final NpcTemplate template = parseTemplate(sp);
						
						if (template == null)
						{
							continue;
						}
						
						result.add(template);
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
		final Array<Func> funcs = Arrays.toArray(Func.class);
		final FuncParser parser = FuncParser.getInstance();
		parser.parse(node, funcs, file);
		funcs.trimToSize();
		return funcs.array();
	}
	
	/**
	 * Method parseTemplate.
	 * @param nodes Node
	 * @return NpcTemplate
	 */
	private final NpcTemplate parseTemplate(Node nodes)
	{
		return new NpcTemplate(VarTable.newInstance(nodes), parseFuncs(nodes));
	}
}
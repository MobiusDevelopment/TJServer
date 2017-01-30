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

import tera.gameserver.model.territory.Territory;
import tera.gameserver.model.territory.TerritoryType;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 09.03.2012
 */
public final class DocumentTerritory extends AbstractDocument<Array<Territory>>
{
	/**
	 * Constructor for DocumentTerritory.
	 * @param file File
	 */
	public DocumentTerritory(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<Territory>
	 */
	@Override
	protected Array<Territory> create()
	{
		return Arrays.toArray(Territory.class);
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
				for (Node node = list.getFirstChild(); node != null; node = node.getNextSibling())
				{
					if ("territory".equals(node.getNodeName()))
					{
						final VarTable vars = VarTable.newInstance(node);
						result.add(vars.getEnum("type", TerritoryType.class).newInstance(node));
					}
				}
			}
		}
	}
}
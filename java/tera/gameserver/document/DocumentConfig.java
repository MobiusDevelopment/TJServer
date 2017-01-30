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

import rlib.data.AbstractDocument;
import rlib.util.VarTable;

/**
 * @author Ronn
 * @created 12.03.2012
 */
public final class DocumentConfig extends AbstractDocument<VarTable>
{
	/**
	 * Constructor for DocumentConfig.
	 * @param file File
	 */
	public DocumentConfig(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return VarTable
	 */
	@Override
	protected VarTable create()
	{
		return VarTable.newInstance();
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
				for (Node set = list.getFirstChild(); set != null; set = set.getNextSibling())
				{
					if ("set".equals(set.getNodeName()))
					{
						final NamedNodeMap attrs = set.getAttributes();
						final String name = attrs.getNamedItem("name").getNodeValue();
						final String value = attrs.getNamedItem("value").getNodeValue();
						
						if ((name == null) || (value == null))
						{
							log.warning(this, "error loading file " + file + ", set name " + name + " value " + value + ".");
							System.exit(0);
						}
						
						result.set(name, value);
					}
				}
			}
		}
	}
}
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
public final class DocumentSnifferOpcode extends AbstractDocument<VarTable>
{
	/**
	 * Constructor for DocumentSnifferOpcode.
	 * @param file File
	 */
	public DocumentSnifferOpcode(File file)
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
				for (Node protocol = list.getFirstChild(); protocol != null; protocol = protocol.getNextSibling())
				{
					if ("protocol".equals(protocol.getNodeName()))
					{
						for (Node packet = protocol.getFirstChild(); packet != null; packet = packet.getNextSibling())
						{
							if ("packet".equals(packet.getNodeName()))
							{
								final NamedNodeMap attrs = packet.getAttributes();
								final Integer id = Integer.decode(attrs.getNamedItem("id").getNodeValue());
								final String type = attrs.getNamedItem("class").getNodeValue();
								final String name = attrs.getNamedItem("name").getNodeValue();
								result.set(type + "_" + name, id);
							}
						}
					}
				}
			}
		}
	}
}
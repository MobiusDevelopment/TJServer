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

import tera.gameserver.model.ai.npc.MessagePackage;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class DocumentMessagePackage extends AbstractDocument<Array<MessagePackage>>
{
	/**
	 * Constructor for DocumentMessagePackage.
	 * @param file File
	 */
	public DocumentMessagePackage(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<MessagePackage>
	 */
	@Override
	protected Array<MessagePackage> create()
	{
		return Arrays.toArray(MessagePackage.class);
	}
	
	/**
	 * Method parse.
	 * @param doc Document
	 */
	@Override
	protected void parse(Document doc)
	{
		final Array<String> messages = Arrays.toArray(String.class);
		final VarTable vars = VarTable.newInstance();
		
		for (Node lst = doc.getFirstChild(); lst != null; lst = lst.getNextSibling())
		{
			if (lst.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("list".equals(lst.getNodeName()))
			{
				for (Node pckg = lst.getFirstChild(); pckg != null; pckg = pckg.getNextSibling())
				{
					if (pckg.getNodeType() != Node.ELEMENT_NODE)
					{
						continue;
					}
					
					if (!"package".equals(pckg.getNodeName()))
					{
						continue;
					}
					
					vars.parse(pckg);
					final String name = vars.getString("name");
					messages.clear();
					
					for (Node msg = pckg.getFirstChild(); msg != null; msg = msg.getNextSibling())
					{
						if ((msg.getNodeType() != Node.ELEMENT_NODE) || !"msg".equals(msg.getNodeName()))
						{
							continue;
						}
						
						vars.parse(msg);
						messages.add(vars.getString("text"));
					}
					
					result.add(new MessagePackage(name, messages.toArray(new String[messages.size()])));
				}
			}
		}
	}
}

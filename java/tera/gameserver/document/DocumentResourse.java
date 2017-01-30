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

import tera.gameserver.templates.ResourseTemplate;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 16.03.2012
 */
public final class DocumentResourse extends AbstractDocument<Array<ResourseTemplate>>
{
	/**
	 * Constructor for DocumentResourse.
	 * @param file File
	 */
	public DocumentResourse(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<ResourseTemplate>
	 */
	@Override
	protected Array<ResourseTemplate> create()
	{
		return Arrays.toArray(ResourseTemplate.class);
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
				for (Node temp = list.getFirstChild(); temp != null; temp = temp.getNextSibling())
				{
					if ((temp.getNodeType() == Node.ELEMENT_NODE) && "template".equals(temp.getNodeName()))
					{
						result.add(new ResourseTemplate(VarTable.newInstance(temp)));
					}
				}
			}
		}
	}
}
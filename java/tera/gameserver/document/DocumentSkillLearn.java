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

import tera.gameserver.model.SkillLearn;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class DocumentSkillLearn extends AbstractDocument<Array<SkillLearn>>
{
	/**
	 * Constructor for DocumentSkillLearn.
	 * @param file File
	 */
	public DocumentSkillLearn(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<SkillLearn>
	 */
	@Override
	protected Array<SkillLearn> create()
	{
		return Arrays.toArray(SkillLearn.class);
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
				for (Node playerClass = list.getFirstChild(); playerClass != null; playerClass = playerClass.getNextSibling())
				{
					if ("class".equals(playerClass.getNodeName()))
					{
						parseClass(playerClass);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseClass.
	 * @param node Node
	 */
	private void parseClass(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final int classId = vars.getInteger("id");
		
		for (Node skills = node.getFirstChild(); skills != null; skills = skills.getNextSibling())
		{
			if ("skill".equals(skills.getNodeName()))
			{
				vars.parse(skills);
				int id = vars.getInteger("id");
				int minLevel = vars.getInteger("minLevel");
				int price = vars.getInteger("price");
				final boolean passive = vars.getBoolean("passive", false);
				SkillLearn current = new SkillLearn(id, price, 0, minLevel, classId, passive);
				result.add(current);
				
				for (Node next = skills.getFirstChild(); next != null; next = next.getNextSibling())
				{
					if ("next".equals(next.getNodeName()))
					{
						vars.parse(next);
						id = vars.getInteger("id");
						minLevel = vars.getInteger("minLevel");
						price = vars.getInteger("price");
						current = new SkillLearn(id, price, current.getId(), minLevel, classId, passive);
						result.add(current);
					}
				}
			}
		}
	}
}

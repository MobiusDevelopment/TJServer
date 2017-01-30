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

import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestType;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class DocumentQuest extends AbstractDocument<Array<Quest>>
{
	/**
	 * Constructor for DocumentQuest.
	 * @param file File
	 */
	public DocumentQuest(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Array<Quest>
	 */
	@Override
	protected Array<Quest> create()
	{
		return Arrays.toArray(Quest.class);
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
				for (Node questNode = lst.getFirstChild(); questNode != null; questNode = questNode.getNextSibling())
				{
					if ("quest".equals(questNode.getNodeName()))
					{
						final VarTable vars = VarTable.newInstance(questNode);
						final Quest quest = vars.getEnum("type", QuestType.class).newInstance(questNode);
						
						if (quest != null)
						{
							result.add(quest);
						}
					}
				}
			}
		}
	}
}

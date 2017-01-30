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

import tera.gameserver.model.npc.playable.NpcAppearance;
import tera.gameserver.model.playable.PlayerAppearance;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public final class DocumentNpcAppearance extends AbstractDocument<Void>
{
	private final Table<String, NpcAppearance> table;
	
	/**
	 * Constructor for DocumentNpcAppearance.
	 * @param file File
	 * @param appTable Table<String,NpcAppearance>
	 */
	public DocumentNpcAppearance(File file, Table<String, NpcAppearance> appTable)
	{
		super(file);
		table = appTable;
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
		final VarTable vars = VarTable.newInstance();
		final Table<String, NpcAppearance> table = getTable();
		
		for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
		{
			if ("list".equals(list.getNodeName()))
			{
				for (Node child = list.getFirstChild(); child != null; child = child.getNextSibling())
				{
					if ((child.getNodeType() == Node.ELEMENT_NODE) && "appearance".equals(child.getNodeName()))
					{
						vars.parse(child);
						final String id = vars.getString("id");
						vars.parse(child, "set", "name", "value");
						final NpcAppearance appearance = PlayerAppearance.fromXML(new NpcAppearance(), vars);
						table.put(id, appearance);
					}
				}
			}
		}
	}
	
	/**
	 * Method getTable.
	 * @return Table<String,NpcAppearance>
	 */
	public Table<String, NpcAppearance> getTable()
	{
		return table;
	}
}
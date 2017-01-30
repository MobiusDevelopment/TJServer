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
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import tera.gameserver.model.npc.interaction.DialogData;
import tera.gameserver.model.npc.interaction.IconType;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.LinkType;
import tera.gameserver.model.npc.interaction.links.NpcLink;
import tera.gameserver.model.npc.interaction.replyes.Reply;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class DocumentDialog extends AbstractDocument<Table<IntKey, Table<IntKey, DialogData>>>
{
	/**
	 * Method parseLink.
	 * @param node Node
	 * @return Link
	 */
	public static Link parseLink(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final String name = vars.getString("name");
		final IconType icon = vars.getEnum("icon", IconType.class, IconType.NONE);
		Reply reply = null;
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ("reply".equals(child.getNodeName()))
			{
				final String replyName = child.getAttributes().getNamedItem("name").getNodeValue();
				
				if (replyName == null)
				{
					continue;
				}
				
				try
				{
					reply = (Reply) Class.forName("tera.gameserver.model.npc.interaction.replyes." + replyName).getConstructor(Node.class).newInstance(child);
				}
				catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					log.warning(DocumentDialog.class, e);
				}
			}
		}
		
		return new NpcLink(name, LinkType.DIALOG, icon, reply);
	}
	
	/**
	 * Constructor for DocumentDialog.
	 * @param file File
	 */
	public DocumentDialog(File file)
	{
		super(file);
	}
	
	/**
	 * Method create.
	 * @return Table<IntKey,Table<IntKey,DialogData>>
	 */
	@Override
	protected Table<IntKey, Table<IntKey, DialogData>> create()
	{
		return Tables.newIntegerTable();
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
				for (Node npc = lst.getFirstChild(); npc != null; npc = npc.getNextSibling())
				{
					if ("npc".equals(npc.getNodeName()))
					{
						final DialogData dialog = parseNpc(npc);
						Table<IntKey, DialogData> table = result.get(dialog.getNpcId());
						
						if (table == null)
						{
							table = Tables.newIntegerTable();
							result.put(dialog.getNpcId(), table);
						}
						
						table.put(dialog.getType(), dialog);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseNpc.
	 * @param node Node
	 * @return DialogData
	 */
	private DialogData parseNpc(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final int id = vars.getInteger("id");
		final int type = vars.getInteger("type");
		final Array<Link> links = Arrays.toArray(Link.class, 2);
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ("link".equals(child.getNodeName()))
			{
				links.add(parseLink(child));
			}
		}
		
		links.trimToSize();
		return new DialogData(links.array(), id, type);
	}
}
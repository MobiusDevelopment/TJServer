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
package tera.gameserver.model.npc.interaction.replyes;

import org.w3c.dom.Node;

import tera.gameserver.model.TeleportRegion;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.dialogs.TeleportDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.territory.LocalTerritory;
import tera.gameserver.tables.TerritoryTable;

import rlib.util.VarTable;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public class ReplyTeleport extends AbstractReply
{
	private final Table<IntKey, TeleportRegion> table;
	private TeleportRegion[] regions;
	
	/**
	 * Constructor for ReplyTeleport.
	 * @param node Node
	 */
	public ReplyTeleport(Node node)
	{
		super(node);
		final TerritoryTable territoryTable = TerritoryTable.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ((child.getNodeType() != Node.ELEMENT_NODE) || !"loc".equals(child.getNodeName()))
			{
				continue;
			}
			
			final VarTable vars = VarTable.newInstance(child);
			final LocalTerritory region = (LocalTerritory) territoryTable.getTerritory(vars.getString("name"));
			
			if (region == null)
			{
				log.warning(this, "not found territory for name " + vars.getString("name"));
				continue;
			}
			
			final int price = vars.getInteger("price");
			final int index = vars.getInteger("index");
			regions = Arrays.addToArray(regions, new TeleportRegion(region, price, index), TeleportRegion.class);
		}
		
		table = Tables.newIntegerTable();
		
		for (TeleportRegion region : regions)
		{
			if (table.containsKey(region.getIndex()))
			{
				log.warning(this, new Exception("found duplicate teleport region for index " + region.getIndex()));
			}
			
			table.put(region.getIndex(), region);
		}
	}
	
	/**
	 * Method reply.
	 * @param npc Npc
	 * @param player Player
	 * @param link Link
	 * @see tera.gameserver.model.npc.interaction.replyes.Reply#reply(Npc, Player, Link)
	 */
	@Override
	public void reply(Npc npc, Player player, Link link)
	{
		if ((regions == null) || (regions.length < 1))
		{
			return;
		}
		
		final TeleportDialog dialog = TeleportDialog.newInstance(npc, player, regions, table);
		
		if (!dialog.init())
		{
			dialog.close();
		}
	}
}
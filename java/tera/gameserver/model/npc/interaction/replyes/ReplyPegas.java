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

import tera.gameserver.model.Route;
import tera.gameserver.model.TownInfo;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.dialogs.PegasDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.tables.TownTable;

import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class ReplyPegas extends AbstractReply
{
	private final Table<IntKey, Route> routes;
	private final TownInfo town;
	
	/**
	 * Constructor for ReplyPegas.
	 * @param node Node
	 */
	public ReplyPegas(Node node)
	{
		super(node);
		routes = Tables.newIntegerTable();
		final TownTable townTable = TownTable.getInstance();
		town = townTable.getTown(node.getAttributes().getNamedItem("town").getNodeValue());
		final Array<Route> list = Arrays.toArray(Route.class);
		
		for (Node route = node.getFirstChild(); route != null; route = route.getNextSibling())
		{
			if ("route".equals(route.getNodeName()))
			{
				final VarTable vars = VarTable.newInstance(route);
				final int index = vars.getInteger("index");
				final int price = vars.getInteger("price");
				final TownInfo target = townTable.getTown(vars.getString("target"));
				list.add(new Route(index, price, target, vars.getBoolean("short", false)));
			}
		}
		
		for (Route route : list)
		{
			routes.put(route.getIndex(), route);
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
		final PegasDialog window = PegasDialog.newInstance(npc, player, routes, town);
		
		if (!window.init())
		{
			window.close();
		}
	}
}
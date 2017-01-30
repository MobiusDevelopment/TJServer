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

import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.TaxationNpc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.npc.interaction.dialogs.SkillShopDialog;
import tera.gameserver.model.playable.Player;

import rlib.util.VarTable;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class ReplySkillShop extends AbstractReply
{
	private final PlayerClass[] available;
	
	/**
	 * Constructor for ReplySkillShop.
	 * @param node Node
	 */
	public ReplySkillShop(Node node)
	{
		super(node);
		final VarTable vars = VarTable.newInstance(node);
		available = vars.getEnumArray("classes", PlayerClass.class, ",");
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
		if (!Arrays.contains(available, player.getPlayerClass()))
		{
			player.sendMessage("You do not have the appropriate class.");
			return;
		}
		
		Bank bank = null;
		float resultTax = 1;
		
		if (npc instanceof TaxationNpc)
		{
			final TaxationNpc taxation = (TaxationNpc) npc;
			bank = taxation.getTaxBank();
			resultTax = 1 + (taxation.getTax() / 100F);
		}
		
		final Dialog dialog = SkillShopDialog.newInstance(npc, player, bank, resultTax);
		
		if (!dialog.init())
		{
			dialog.close();
		}
	}
}
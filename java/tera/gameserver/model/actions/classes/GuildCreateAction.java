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
package tera.gameserver.model.actions.classes;

import tera.Config;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Party;
import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ActionDoned;
import tera.gameserver.network.serverpackets.ActionInvite;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class GuildCreateAction extends AbstractAction<String>
{
	@Override
	public synchronized void assent(Player player)
	{
		final Player actor = getActor();
		final String guildName = getTarget();
		final ActionType type = getType();
		super.assent(player);
		
		if (!test(actor, guildName))
		{
			return;
		}
		
		actor.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), actor.getObjectId(), actor.getSubId(), type.ordinal(), objectId), true);
		final Party party = player.getParty();
		
		if (party == null)
		{
			player.sendMessage("You must be in a party.");
			return;
		}
		
		final Array<Player> members = party.getMembers();
		final Dialog dialog = actor.getLastDialog();
		
		if (dialog != null)
		{
			dialog.close();
		}
		
		JoinGuildAction.newInstance(actor, guildName, members, 3000).invite();
	}
	
	@Override
	public synchronized void cancel(Player player)
	{
		final Player actor = getActor();
		final ActionType type = getType();
		
		if (actor != null)
		{
			actor.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), actor.getObjectId(), actor.getSubId(), type.ordinal(), objectId), true);
		}
		
		super.cancel(player);
	}
	
	@Override
	public ActionType getType()
	{
		return ActionType.CREATE_GUILD;
	}
	
	@Override
	public void init(Player actor, String name)
	{
		this.actor = actor;
		target = name;
	}
	
	@Override
	public synchronized void invite()
	{
		final Player actor = getActor();
		final String target = getTarget();
		
		if ((actor == null) || (target == null))
		{
			return;
		}
		
		final ActionType type = getType();
		actor.setLastAction(this);
		actor.sendPacket(ActionInvite.getInstance(actor.getName(), target, type.ordinal(), objectId), true);
		final ExecutorManager executor = ExecutorManager.getInstance();
		setSchedule(executor.scheduleGeneral(this, 30000));
	}
	
	@Override
	public boolean test(Player actor, String target)
	{
		if ((target == null) || (actor == null))
		{
			return false;
		}
		
		if (!Config.checkName(target))
		{
			actor.sendMessage("Invalid guild name.");
			return false;
		}
		
		if (actor.getLevel() < 8)
		{
			actor.sendMessage("Your level is too low.");
			return false;
		}
		
		if (actor.hasGuild())
		{
			actor.sendMessage("You are already a member of a guild.");
			return false;
		}
		
		final Party party = actor.getParty();
		
		if (party == null)
		{
			actor.sendMessage("You must have a party.");
			return false;
		}
		
		final Array<Player> members = party.getMembers();
		members.readLock();
		
		try
		{
			final Player[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				if (array[i].hasGuild())
				{
					actor.sendMessage("Our group already has a man in a guild.");
					return false;
				}
			}
		}
		
		finally
		{
			members.readUnlock();
		}
		final Inventory inventory = actor.getInventory();
		
		if (inventory.getMoney() < 3000)
		{
			actor.sendMessage("You do not have enough money.");
			return false;
		}
		
		return true;
	}
}

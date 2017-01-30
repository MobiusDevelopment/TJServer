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

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildRank;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.World;
import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ActionDoned;
import tera.gameserver.network.serverpackets.ActionInvite;

/**
 * @author Ronn
 */
public class GuildInviteAction extends PlayerAction
{
	@Override
	public synchronized void assent(Player player)
	{
		final Player actor = getActor();
		final Player target = getTarget();
		super.assent(player);
		
		if (!test(actor, target))
		{
			return;
		}
		
		final ActionType type = getType();
		final Guild guild = actor.getGuild();
		actor.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), player.getObjectId(), player.getSubId(), type.ordinal(), objectId), true);
		player.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), player.getObjectId(), player.getSubId(), type.ordinal(), objectId), true);
		guild.joinMember(player);
		actor.updateGuild();
		player.sendMessage("You joined \"" + guild.getName() + "\"!");
	}
	
	@Override
	public synchronized void cancel(Player player)
	{
		final Player actor = getActor();
		final Player target = getTarget();
		
		if ((actor != null) && (target != null))
		{
			final ActionType type = getType();
			actor.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), target.getObjectId(), target.getSubId(), type.ordinal(), objectId), true);
			target.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), target.getObjectId(), target.getSubId(), type.ordinal(), objectId), true);
		}
		
		super.cancel(player);
	}
	
	@Override
	public ActionType getType()
	{
		return ActionType.INVITE_GUILD;
	}
	
	@Override
	public void init(Player actor, String name)
	{
		Player target = World.getAroundByName(Player.class, actor, name);
		
		if (target == null)
		{
			target = World.getPlayer(name);
		}
		
		this.actor = actor;
		this.target = target;
	}
	
	@Override
	public void invite()
	{
		final Player actor = getActor();
		final Player target = getTarget();
		
		if ((actor == null) || (target == null))
		{
			return;
		}
		
		final Guild guild = actor.getGuild();
		
		if (guild == null)
		{
			actor.sendMessage("You are not in a guild.");
			return;
		}
		
		actor.setLastAction(this);
		target.setLastAction(this);
		final ActionType type = getType();
		target.sendPacket(ActionInvite.getInstance(actor.getName(), target.getName(), type.ordinal(), objectId), true);
		final ExecutorManager executor = ExecutorManager.getInstance();
		setSchedule(executor.scheduleGeneral(this, 20000));
	}
	
	@Override
	protected boolean test(Player actor, Player target)
	{
		if ((target == null) || (actor == null))
		{
			return false;
		}
		
		final GuildRank actorRank = actor.getGuildRank();
		
		if ((actorRank == null) || !actorRank.isChangeLineUp())
		{
			actor.sendMessage("You can not invite to the guild.");
			return false;
		}
		
		final Guild guild = target.getGuild();
		
		if (guild != null)
		{
			actor.sendMessage("The player is already in another guild.");
			return false;
		}
		
		if (target.getLastAction() != null)
		{
			actor.sendMessage(MessageType.TARGET_IS_BUSY);
			return false;
		}
		
		return true;
	}
}

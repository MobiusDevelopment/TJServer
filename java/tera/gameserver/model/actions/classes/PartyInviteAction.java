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
import tera.gameserver.model.MessageType;
import tera.gameserver.model.Party;
import tera.gameserver.model.World;
import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ActionDoned;
import tera.gameserver.network.serverpackets.ActionInvite;
import tera.gameserver.network.serverpackets.AppledAction;
import tera.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Ronn
 * @created 06.03.2012
 */
public class PartyInviteAction extends PlayerAction
{
	/**
	 * Method assent.
	 * @param player Player
	 * @see tera.gameserver.model.actions.Action#assent(Player)
	 */
	@Override
	public void assent(Player player)
	{
		final Player actor = getActor();
		final Player target = getTarget();
		super.assent(player);
		
		if (!test(actor, target))
		{
			return;
		}
		
		final ActionType type = getType();
		actor.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), target.getObjectId(), target.getSubId(), type.ordinal(), objectId), true);
		target.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), target.getObjectId(), target.getSubId(), type.ordinal(), objectId), true);
		Party party = actor.getParty();
		
		if (party == null)
		{
			party = Party.newInstance(actor, objectId);
		}
		
		party.addPlayer(target);
	}
	
	/**
	 * Method cancel.
	 * @param player Player
	 * @see tera.gameserver.model.actions.Action#cancel(Player)
	 */
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
	
	/**
	 * Method getType.
	 * @return ActionType
	 * @see tera.gameserver.model.actions.Action#getType()
	 */
	@Override
	public ActionType getType()
	{
		return ActionType.PARTY;
	}
	
	/**
	 * Method init.
	 * @param actor Player
	 * @param name String
	 * @see tera.gameserver.model.actions.Action#init(Player, String)
	 */
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
	
	/**
	 * Method invite.
	 * @see tera.gameserver.model.actions.Action#invite()
	 */
	@Override
	public synchronized void invite()
	{
		final Player actor = getActor();
		final Player target = getTarget();
		
		if ((actor == null) || (target == null))
		{
			return;
		}
		
		actor.setLastAction(this);
		target.setLastAction(this);
		final ActionType type = getType();
		actor.sendPacket(AppledAction.newInstance(actor, target, type.ordinal(), objectId), true);
		target.sendPacket(ActionInvite.getInstance(actor.getName(), target.getName(), type.ordinal(), objectId), true);
		final ExecutorManager executor = ExecutorManager.getInstance();
		setSchedule(executor.scheduleGeneral(this, 20000));
	}
	
	/**
	 * Method test.
	 * @param actor Player
	 * @param target Player
	 * @return boolean
	 */
	@Override
	public boolean test(Player actor, Player target)
	{
		if (actor == null)
		{
			return false;
		}
		
		final Party party = actor.getParty();
		
		if ((party != null) && (party.size() > 4))
		{
			actor.sendMessage(MessageType.THE_PARTY_IS_FULL);
			return false;
		}
		
		if (target == null)
		{
			actor.sendMessage(MessageType.THAT_CHARACTER_ISNT_ONLINE);
			return false;
		}
		
		if (target.getParty() != null)
		{
			actor.sendMessage(MessageType.TARGET_IS_ALREADY_IN_THE_SAME_GROUP);
			return false;
		}
		
		if (target.getLastAction() != null)
		{
			actor.sendMessage(MessageType.TARGET_IS_BUSY);
			return false;
		}
		
		if (target.isPvPMode())
		{
			actor.sendMessage(MessageType.YOU_CANT_INVITE_PVP_PLAYER_TO_A_PARTY);
			return false;
		}
		
		if (target.isDead())
		{
			actor.sendPacket(SystemMessage.getInstance(MessageType.USER_NAME_IS_DEAD).addUserName(target.getName()), true);
			return false;
		}
		
		return true;
	}
}
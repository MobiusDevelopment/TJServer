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
import tera.gameserver.model.Duel;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.World;
import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ActionDoned;
import tera.gameserver.network.serverpackets.AppledAction;
import tera.gameserver.network.serverpackets.DuelStart;
import tera.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Ronn
 */
public class DuelStartAction extends PlayerAction
{
	/**
	 * Method assent.
	 * @param player Player
	 * @see tera.gameserver.model.actions.Action#assent(Player)
	 */
	@Override
	public synchronized void assent(Player player)
	{
		final Player target = getTarget();
		final Player actor = getActor();
		super.assent(player);
		
		if (!test(actor, target))
		{
			cancel(null);
			return;
		}
		
		final ActionType type = getType();
		actor.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), target.getObjectId(), target.getSubId(), type.ordinal(), objectId), true);
		target.sendPacket(ActionDoned.getInstance(actor.getObjectId(), actor.getSubId(), target.getObjectId(), target.getSubId(), type.ordinal(), objectId), true);
		final SystemMessage packet = SystemMessage.getInstance(MessageType.TARGET_ACCEPTED_THE_DUEL);
		packet.addTarget(target.getName());
		actor.sendPacket(packet, true);
		actor.sendPacket(DuelStart.getInstance(), true);
		target.sendPacket(DuelStart.getInstance(), true);
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleGeneral(Duel.newInstance(actor, target), 5500);
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
			SystemMessage packet;
			
			if (player == target)
			{
				packet = SystemMessage.getInstance(MessageType.TARGET_REJERECT_THE_DUEL);
				packet.addTarget(target.getName());
				actor.sendPacket(packet, true);
			}
			
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
		return ActionType.DUEL;
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
		this.actor = actor;
		target = World.getAroundByName(Player.class, actor, name);
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
			log.warning(this, new Exception("not found actor or target"));
			return;
		}
		
		actor.setLastAction(this);
		target.setLastAction(this);
		final ActionType type = getType();
		actor.sendPacket(AppledAction.newInstance(actor, target, type.ordinal(), objectId), true);
		target.sendPacket(AppledAction.newInstance(actor, target, type.ordinal(), objectId), true);
		final ExecutorManager executor = ExecutorManager.getInstance();
		setSchedule(executor.scheduleGeneral(this, 30000));
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
		
		if (actor.getDuel() != null)
		{
			actor.sendMessage(MessageType.YOU_ARE_IN_A_DUEL_NOW);
			return false;
		}
		
		if (target == null)
		{
			actor.sendMessage(MessageType.THAT_CHARACTER_ISNT_ONLINE);
			return false;
		}
		
		if (!actor.isInRange(target, Config.WORLD_DUEL_MAX_RANGE))
		{
			actor.sendMessage(MessageType.TOO_FAR_AWAY);
			return false;
		}
		
		if (target.getLastAction() != null)
		{
			actor.sendMessage(MessageType.TARGET_IS_BUSY);
			return false;
		}
		
		if (actor.isPvPMode() || actor.isBattleStanced())
		{
			actor.sendMessage(MessageType.YOU_CANT_DUEL_WITH_SOMEONE_IN_PVP);
			return false;
		}
		
		if (actor.isEvent() || target.isEvent())
		{
			actor.sendMessage("Player already in a event.");
			return false;
		}
		
		if (target.isBattleStanced() || target.isPvPMode())
		{
			actor.sendMessage(MessageType.TARGET_IS_IN_COMBAT);
			return false;
		}
		
		if (target.hasDuel())
		{
			actor.sendMessage("Player is in a duel.");
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

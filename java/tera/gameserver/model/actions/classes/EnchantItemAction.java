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

import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.actions.dialogs.EnchantItemDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.AppledAction;

/**
 * @author Ronn
 */
public final class EnchantItemAction extends AbstractAction<Void>
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
		super.assent(player);
		
		if (!test(actor, target))
		{
			return;
		}
		
		final EnchantItemDialog dialog = EnchantItemDialog.newInstance(actor);
		
		if (!dialog.init())
		{
			dialog.cancel(actor);
		}
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
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		actor.setLastAction(null);
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
		return ActionType.ENCHANT_ITEM;
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
	}
	
	/**
	 * Method invite.
	 * @see tera.gameserver.model.actions.Action#invite()
	 */
	@Override
	public synchronized void invite()
	{
		final Player actor = getActor();
		
		if ((actor == null) || actor.isOnMount() || actor.isFlyingPegas() || actor.hasLastActionDialog())
		{
			return;
		}
		
		actor.setLastAction(this);
		final ActionType type = getType();
		actor.sendPacket(AppledAction.newInstance(actor, null, type.ordinal(), objectId), true);
		assent(actor);
	}
	
	/**
	 * Method test.
	 * @param actor Player
	 * @param target Void
	 * @return boolean
	 */
	@Override
	public boolean test(Player actor, Void target)
	{
		if ((actor == null) || actor.isOnMount() || actor.isFlyingPegas() || actor.hasLastActionDialog())
		{
			return false;
		}
		
		return true;
	}
}
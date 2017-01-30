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
package tera.gameserver.model.actions.dialogs;

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ActionDialogCancel;

import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public abstract class AbstractActionDialog implements ActionDialog
{
	protected static final Logger log = Loggers.getLogger(ActionDialog.class);
	
	protected Player actor;
	
	protected Player enemy;
	
	protected ActionDialogType type;
	
	protected int objectId;
	
	public AbstractActionDialog()
	{
		type = getType();
	}
	
	/**
	 * Method cancel.
	 * @param player Player
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#cancel(Player)
	 */
	@Override
	public synchronized void cancel(Player player)
	{
		final Player actor = getActor();
		final Player enemy = getEnemy();
		
		if ((actor == null) || (enemy == null))
		{
			return;
		}
		
		clear();
		actor.sendPacket(ActionDialogCancel.getInstance(actor, enemy, type.ordinal(), objectId), true);
		enemy.sendPacket(ActionDialogCancel.getInstance(enemy, actor, type.ordinal(), objectId), true);
		getType().getPool().put(this);
	}
	
	protected void clear()
	{
		actor.setLastActionDialog(null);
		enemy.setLastActionDialog(null);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		actor = null;
		enemy = null;
	}
	
	/**
	 * Method getActor.
	 * @return Player
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#getActor()
	 */
	@Override
	public Player getActor()
	{
		return actor;
	}
	
	/**
	 * Method getEnemy.
	 * @return Player
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#getEnemy()
	 */
	@Override
	public Player getEnemy()
	{
		return enemy;
	}
	
	/**
	 * Method getEnemy.
	 * @param player Player
	 * @return Player
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#getEnemy(Player)
	 */
	@Override
	public Player getEnemy(Player player)
	{
		if (player == actor)
		{
			return enemy;
		}
		return actor;
	}
	
	/**
	 * Method init.
	 * @return boolean
	 * @see tera.gameserver.model.actions.dialogs.ActionDialog#init()
	 */
	@Override
	public synchronized boolean init()
	{
		actor.setLastActionDialog(this);
		enemy.setLastActionDialog(this);
		return true;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
}

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
package tera.gameserver.model.npc.interaction.dialogs;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.pools.FoldablePool;

/**
 * @author Ronn
 * @created 24.02.2012
 */
public abstract class AbstractDialog implements Dialog
{
	protected static final Logger log = Loggers.getLogger(AbstractDialog.class);
	protected DialogType type;
	protected Npc npc;
	protected Player player;
	
	public AbstractDialog()
	{
		type = getType();
	}
	
	/**
	 * Method apply.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#apply()
	 */
	@Override
	public boolean apply()
	{
		return false;
	}
	
	/**
	 * Method close.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#close()
	 */
	@Override
	public synchronized boolean close()
	{
		final Player player = getPlayer();
		final Npc npc = getNpc();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		if (npc == null)
		{
			log.warning(this, new Exception("not found npc"));
		}
		else
		{
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyStopDialog(npc, player);
		}
		
		player.setLastDialog(null);
		final FoldablePool<Dialog> pool = type.getPool();
		pool.put(this);
		return true;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		npc = null;
		player = null;
	}
	
	/**
	 * Method getNpc.
	 * @return Npc
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#getNpc()
	 */
	@Override
	public final Npc getNpc()
	{
		return npc;
	}
	
	/**
	 * Method getPlayer.
	 * @return Player
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#getPlayer()
	 */
	@Override
	public final Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Method getType.
	 * @return DialogType
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#getType()
	 */
	@Override
	public abstract DialogType getType();
	
	/**
	 * Method init.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#init()
	 */
	@Override
	public boolean init()
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		final Dialog old = player.getLastDialog();
		
		if (old != null)
		{
			old.close();
		}
		
		final Npc npc = getNpc();
		
		if (npc == null)
		{
			log.warning(this, new Exception("not found npc"));
		}
		else
		{
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyStartDialog(npc, player);
		}
		
		player.setLastDialog(this);
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
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
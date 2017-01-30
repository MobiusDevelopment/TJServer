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

import java.util.concurrent.ScheduledFuture;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Route;
import tera.gameserver.model.TownInfo;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.DeparturePortal;
import tera.gameserver.network.serverpackets.GetOffPegas;
import tera.gameserver.network.serverpackets.PegasFly;
import tera.gameserver.network.serverpackets.PegasReplyPacket;
import tera.gameserver.network.serverpackets.PegasRouts;
import tera.gameserver.network.serverpackets.PutAnPegas;
import tera.gameserver.network.serverpackets.StateAllowed;
import tera.gameserver.network.serverpackets.WorldZone;

import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 * @created 25.02.2012
 */
public class PegasDialog extends AbstractDialog implements Runnable
{
	/**
	 */
	public static enum State
	{
		STARTING,
		FLY,
		LANDING,
	}
	
	public static final int DEPARTURE_CONTINUE_STATE = 999800;
	
	/**
	 * Method newInstance.
	 * @param npc Npc
	 * @param player Player
	 * @param routes Table<IntKey,Route>
	 * @param town TownInfo
	 * @return PegasDialog
	 */
	public static final PegasDialog newInstance(Npc npc, Player player, Table<IntKey, Route> routes, TownInfo town)
	{
		final PegasDialog dialog = (PegasDialog) DialogType.PEGAS.newInstance();
		dialog.npc = npc;
		dialog.player = player;
		dialog.routes = routes;
		dialog.town = town;
		return dialog;
	}
	
	private Table<IntKey, Route> routes;
	private State state;
	private TownInfo town;
	private ScheduledFuture<PegasDialog> schedule;
	
	public PegasDialog()
	{
		setState(State.STARTING);
	}
	
	/**
	 * Method apply.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#apply()
	 */
	@Override
	public synchronized boolean apply()
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		
		switch (getState())
		{
			case STARTING:
			{
				final Route route = player.getRoute();
				
				if (route == null)
				{
					log.warning(this, new Exception("not found route"));
					return false;
				}
				
				if (player.isOnMount())
				{
					player.getOffMount();
				}
				
				player.setLoc(town.getLanding());
				player.setFlyingPegas(true);
				player.setInvul(true);
				player.broadcastPacket(PutAnPegas.getInstance(player));
				player.broadcastPacket(PegasFly.getInstance(player, route, 0));
				player.broadcastPacket(PegasFly.getInstance(player, route, 3000));
				
				if (route.isLocal())
				{
					setState(State.LANDING);
					schedule = executor.scheduleGeneral(this, route.getTarget().getLocal());
				}
				else
				{
					setState(State.FLY);
					schedule = executor.scheduleGeneral(this, town.getToPortal());
				}
				
				break;
			}
			
			case FLY:
			{
				final Route route = player.getRoute();
				
				if (route == null)
				{
					log.warning(this, new Exception("not found route"));
					return false;
				}
				
				final TownInfo target = route.getTarget();
				player.setLoc(target.getPortal());
				final DataBaseManager dbManager = DataBaseManager.getInstance();
				dbManager.updatePlayerContinentId(player);
				player.broadcastPacket(DeparturePortal.getInstance(player));
				player.broadcastPacket(StateAllowed.getInstance(player, DEPARTURE_CONTINUE_STATE));
				player.setZoneId(target.getZone());
				player.sendPacket(WorldZone.getInstance(player), true);
				setState(State.LANDING);
				schedule = executor.scheduleGeneral(this, target.getToLanding());
				break;
			}
			
			case LANDING:
			{
				final Route route = player.getRoute();
				
				if (route == null)
				{
					log.warning(this, new Exception("not found route"));
					return false;
				}
				
				final TownInfo target = route.getTarget();
				player.teleToLocation(target.getLanding());
				player.broadcastPacket(GetOffPegas.getInstance(player));
				player.setFlyingPegas(false);
				player.setInvul(false);
				player.setRoute(null);
				close();
			}
		}
		
		return true;
	}
	
	/**
	 * Method close.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#close()
	 */
	@Override
	public synchronized boolean close()
	{
		if (schedule != null)
		{
			schedule.cancel(false);
			schedule = null;
		}
		
		return super.close();
	}
	
	/**
	 * Method fly.
	 * @param index int
	 * @return boolean
	 */
	public synchronized boolean fly(int index)
	{
		if (state != State.STARTING)
		{
			return false;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		final Route route = routes.get(index);
		
		if (route == null)
		{
			log.warning(this, new Exception("not found route"));
			return false;
		}
		
		final Inventory inventory = player.getInventory();
		
		if (inventory.getMoney() < route.getPrice())
		{
			return false;
		}
		
		inventory.subMoney(route.getPrice());
		final GameLogManager gameLogger = GameLogManager.getInstance();
		gameLogger.writeItemLog(player.getName() + " buy fly pegas for " + route.getPrice() + " gold");
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyInventoryChanged(player);
		player.setRoute(route);
		return true;
	}
	
	/**
	 * Method getState.
	 * @return State
	 */
	public State getState()
	{
		return state;
	}
	
	/**
	 * Method getType.
	 * @return DialogType
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#getType()
	 */
	@Override
	public DialogType getType()
	{
		return DialogType.PEGAS;
	}
	
	/**
	 * Method init.
	 * @return boolean
	 * @see tera.gameserver.model.npc.interaction.dialogs.Dialog#init()
	 */
	@Override
	public synchronized boolean init()
	{
		if (routes.isEmpty())
		{
			return false;
		}
		
		if (!super.init())
		{
			return false;
		}
		
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		player.sendPacket(PegasRouts.getInstance(routes, town.getId()), true);
		player.sendPacket(PegasReplyPacket.getInstance(player), true);
		return true;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		state = State.STARTING;
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		if (!apply())
		{
			close();
		}
	}
	
	/**
	 * Method setState.
	 * @param state State
	 */
	public void setState(State state)
	{
		this.state = state;
	}
}
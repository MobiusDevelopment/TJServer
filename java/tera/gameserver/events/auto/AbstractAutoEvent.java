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
package tera.gameserver.events.auto;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import tera.gameserver.events.Event;
import tera.gameserver.events.EventPlayer;
import tera.gameserver.events.EventState;
import tera.gameserver.events.NpcInteractEvent;
import tera.gameserver.events.Registered;
import tera.gameserver.manager.EventManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.listeners.DeleteListener;
import tera.gameserver.model.listeners.DieListener;
import tera.gameserver.model.listeners.TerritoryListener;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.skillengine.funcs.stat.MathFunc;
import tera.gameserver.model.territory.Territory;
import tera.gameserver.tables.TerritoryTable;
import tera.gameserver.tables.WorldZoneTable;

import rlib.concurrent.Locks;
import rlib.util.SafeTask;
import rlib.util.Synchronized;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public abstract class AbstractAutoEvent extends SafeTask implements Event, Synchronized, NpcInteractEvent, Registered, TerritoryListener, DieListener, DeleteListener
{
	private static final Func RUN_LOCKER = new MathFunc(StatType.RUN_SPEED, 0x50, null, null)
	{
		@Override
		public float calc(Character attacker, Character attacked, Skill skill, float val)
		{
			return 0;
		}
	};
	
	private final Lock lock;
	private final Table<IntKey, EventPlayer> players;
	private final Array<Player> prepare;
	private final Array<Player> activePlayers;
	private final Territory eventTerritory;
	protected ScheduledFuture<? extends AbstractAutoEvent> schedule;
	protected EventState state;
	protected int time;
	protected boolean started;
	
	protected AbstractAutoEvent()
	{
		lock = Locks.newLock();
		prepare = Arrays.toArray(Player.class);
		activePlayers = Arrays.toArray(Player.class);
		final TerritoryTable territoryTable = TerritoryTable.getInstance();
		eventTerritory = territoryTable.getTerritory(getTerritoryId());
		players = Tables.newIntegerTable();
	}
	
	/**
	 * Method addActivePlayer.
	 * @param player Player
	 */
	public final void addActivePlayer(Player player)
	{
		activePlayers.add(player);
	}
	
	/**
	 * Method addLinks.
	 * @param links Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	@Override
	public void addLinks(Array<Link> links, Npc npc, Player player)
	{
	}
	
	protected final void clearTerritory()
	{
		if (eventTerritory == null)
		{
			return;
		}
		
		final WorldZoneTable worldZoneTable = WorldZoneTable.getInstance();
		final Array<TObject> objects = eventTerritory.getObjects();
		final TObject[] objs = objects.array();
		objects.writeLock();
		
		try
		{
			for (int i = 0, length = objects.size(); i < length; i++)
			{
				final TObject object = objs[i];
				
				if (!object.isPlayer())
				{
					continue;
				}
				
				final Player player = object.getPlayer();
				
				if (!players.containsKey(player.getObjectId()))
				{
					player.teleToLocation(worldZoneTable.getRespawn(player));
					i--;
					length--;
				}
			}
		}
		
		finally
		{
			objects.writeUnlock();
		}
	}
	
	protected void finishedState()
	{
	}
	
	protected void finishingState()
	{
	}
	
	/**
	 * Method getActivePlayers.
	 * @return Array<Player>
	 */
	public Array<Player> getActivePlayers()
	{
		return activePlayers;
	}
	
	/**
	 * Method getEventTerritory.
	 * @return Territory
	 */
	public final Territory getEventTerritory()
	{
		return eventTerritory;
	}
	
	/**
	 * Method getMaxLevel.
	 * @return int
	 */
	protected int getMaxLevel()
	{
		return 0;
	}
	
	/**
	 * Method getMinLevel.
	 * @return int
	 */
	protected int getMinLevel()
	{
		return 0;
	}
	
	/**
	 * Method getPlayers.
	 * @return Table<IntKey,EventPlayer>
	 */
	public final Table<IntKey, EventPlayer> getPlayers()
	{
		return players;
	}
	
	/**
	 * Method getPrepare.
	 * @return Array<Player>
	 */
	public final Array<Player> getPrepare()
	{
		return prepare;
	}
	
	/**
	 * Method getRegisterTime.
	 * @return int
	 */
	protected int getRegisterTime()
	{
		return 0;
	}
	
	/**
	 * Method getState.
	 * @return EventState
	 */
	protected final EventState getState()
	{
		return state;
	}
	
	/**
	 * Method getTerritoryId.
	 * @return int
	 */
	protected int getTerritoryId()
	{
		return 0;
	}
	
	/**
	 * Method isAuto.
	 * @return boolean
	 * @see tera.gameserver.events.Event#isAuto()
	 */
	@Override
	public boolean isAuto()
	{
		return true;
	}
	
	/**
	 * Method isCheckDieState.
	 * @return boolean
	 */
	protected boolean isCheckDieState()
	{
		return false;
	}
	
	/**
	 * Method isCheckTerritoryState.
	 * @return boolean
	 */
	protected boolean isCheckTerritoryState()
	{
		return false;
	}
	
	/**
	 * Method isStarted.
	 * @return boolean
	 */
	public final boolean isStarted()
	{
		return started;
	}
	
	/**
	 * Method lock.
	 * @see rlib.util.Synchronized#lock()
	 */
	@Override
	public final void lock()
	{
		lock.lock();
	}
	
	/**
	 * Method lockMove.
	 * @param player Player
	 */
	protected void lockMove(Player player)
	{
		RUN_LOCKER.addFuncTo(player);
	}
	
	/**
	 * Method onDelete.
	 * @param player Player
	 */
	protected void onDelete(Player player)
	{
	}
	
	/**
	 * Method onDelete.
	 * @param object TObject
	 * @see tera.gameserver.model.listeners.DeleteListener#onDelete(TObject)
	 */
	@Override
	public void onDelete(TObject object)
	{
		if (!object.isPlayer())
		{
			return;
		}
		
		final Player player = object.getPlayer();
		
		if (!player.isEvent())
		{
			return;
		}
		
		onDelete(player);
	}
	
	/**
	 * Method onDie.
	 * @param killer Character
	 * @param killed Character
	 * @see tera.gameserver.model.listeners.DieListener#onDie(Character, Character)
	 */
	@Override
	public void onDie(Character killer, Character killed)
	{
		if (!isCheckDieState() || !killed.isPlayer())
		{
			return;
		}
		
		final Player player = killed.getPlayer();
		
		if (!player.isEvent() || !players.containsKey(killed.getObjectId()))
		{
			return;
		}
		
		onDie(player, killer);
	}
	
	/**
	 * Method onDie.
	 * @param killed Player
	 * @param killer Character
	 */
	protected void onDie(Player killed, Character killer)
	{
	}
	
	/**
	 * Method onEnter.
	 * @param player Player
	 */
	protected void onEnter(Player player)
	{
	}
	
	/**
	 * Method onEnter.
	 * @param territory Territory
	 * @param object TObject
	 * @see tera.gameserver.model.listeners.TerritoryListener#onEnter(Territory, TObject)
	 */
	@Override
	public void onEnter(Territory territory, TObject object)
	{
		if ((territory != eventTerritory) || !isCheckTerritoryState() || !object.isPlayer() || players.containsKey(object.getObjectId()))
		{
			return;
		}
		
		onEnter(object.getPlayer());
	}
	
	/**
	 * Method onExit.
	 * @param player Player
	 */
	protected void onExit(Player player)
	{
	}
	
	/**
	 * Method onExit.
	 * @param territory Territory
	 * @param object TObject
	 * @see tera.gameserver.model.listeners.TerritoryListener#onExit(Territory, TObject)
	 */
	@Override
	public void onExit(Territory territory, TObject object)
	{
		if ((territory != eventTerritory) || !isCheckTerritoryState() || !object.isPlayer() || !players.containsKey(object.getObjectId()))
		{
			return;
		}
		
		onExit(object.getPlayer());
	}
	
	/**
	 * Method onLoad.
	 * @return boolean
	 * @see tera.gameserver.events.Event#onLoad()
	 */
	@Override
	public boolean onLoad()
	{
		return true;
	}
	
	/**
	 * Method onReload.
	 * @return boolean
	 * @see tera.gameserver.events.Event#onReload()
	 */
	@Override
	public boolean onReload()
	{
		return true;
	}
	
	/**
	 * Method onSave.
	 * @return boolean
	 * @see tera.gameserver.events.Event#onSave()
	 */
	@Override
	public boolean onSave()
	{
		return true;
	}
	
	protected void prepareBattleState()
	{
	}
	
	protected void prepareEndState()
	{
	}
	
	protected void prepareStartState()
	{
	}
	
	/**
	 * Method registerPlayer.
	 * @param player Player
	 * @return boolean
	 * @see tera.gameserver.events.Registered#registerPlayer(Player)
	 */
	@Override
	public boolean registerPlayer(Player player)
	{
		lock();
		
		try
		{
			if (!isStarted())
			{
				player.sendMessage("Event is not running.");
				return false;
			}
			
			if (getState() != EventState.REGISTER)
			{
				player.sendMessage("It is not registration time.");
				return false;
			}
			
			if ((player.getLevel() > getMaxLevel()) || (player.getLevel() < getMinLevel()))
			{
				player.sendMessage("Your level is not adequate.");
				return false;
			}
			
			final Array<Player> prepare = getPrepare();
			
			if (prepare.contains(player))
			{
				player.sendMessage("You have already registered.");
				return false;
			}
			
			if (player.isDead())
			{
				player.sendMessage("You are dead.");
				return false;
			}
			
			if (player.hasDuel())
			{
				player.sendMessage("You are in a duel.");
				return false;
			}
			
			prepare.add(player);
			player.setEvent(true);
			player.sendMessage("You have registered.");
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method isNeedShowPlayerCount.
	 * @return boolean
	 */
	protected boolean isNeedShowPlayerCount()
	{
		return true;
	}
	
	protected void registerState()
	{
		World.sendAnnounce("There are " + time + " minute(s) left to register for the event.");
		
		if (isNeedShowPlayerCount())
		{
			World.sendAnnounce("The have been " + prepare.size() + " registered participants.");
		}
		
		World.sendAnnounce("Register at NPC [Mystel] or via the command --event_reg " + getName() + ".");
		time--;
		
		if (time == 0)
		{
			setState(EventState.PREPARE_START);
		}
	}
	
	/**
	 * Method removeActivePlayer.
	 * @param player Object
	 */
	public final void removeActivePlayer(Object player)
	{
		activePlayers.fastRemove(player);
	}
	
	/**
	 * Method removeEventPlayer.
	 * @param objectId int
	 * @return EventPlayer
	 */
	public final EventPlayer removeEventPlayer(int objectId)
	{
		return players.remove(objectId);
	}
	
	@Override
	protected void runImpl()
	{
		lock();
		
		try
		{
			switch (getState())
			{
				case REGISTER:
				{
					registerState();
					break;
				}
				
				case PREPARE_START:
				{
					prepareStartState();
					break;
				}
				
				case PREPARE_BATLE:
				{
					prepareBattleState();
					break;
				}
				
				case RUNNING:
				{
					runningState();
					break;
				}
				
				case PREPARE_END:
				{
					prepareEndState();
					break;
				}
				
				case FINISHING:
				{
					finishingState();
					break;
				}
				
				case FINISHED:
				{
					finishedState();
				}
			}
		}
		
		finally
		{
			unlock();
		}
	}
	
	protected void runningState()
	{
	}
	
	/**
	 * Method setStarted.
	 * @param var boolean
	 */
	public final void setStarted(boolean var)
	{
		started = var;
	}
	
	/**
	 * Method setState.
	 * @param var EventState
	 */
	protected final void setState(EventState var)
	{
		state = var;
	}
	
	/**
	 * Method start.
	 * @return boolean
	 * @see tera.gameserver.events.Event#start()
	 */
	@Override
	public boolean start()
	{
		lock();
		
		try
		{
			if (isStarted())
			{
				return false;
			}
			
			if (eventTerritory != null)
			{
				eventTerritory.addListener(this);
			}
			
			final ObjectEventManager objectEventManager = ObjectEventManager.getInstance();
			objectEventManager.addDeleteListener(this);
			objectEventManager.addDieListener(this);
			time = getRegisterTime();
			final EventManager eventManager = EventManager.getInstance();
			eventManager.start(this);
			World.sendAnnounce("Started automatic event \"" + getName() + "\"");
			setStarted(true);
			setState(EventState.REGISTER);
			final ExecutorManager executor = ExecutorManager.getInstance();
			schedule = executor.scheduleGeneralAtFixedRate(this, 60000, 60000);
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method stop.
	 * @return boolean
	 * @see tera.gameserver.events.Event#stop()
	 */
	@Override
	public boolean stop()
	{
		lock();
		
		try
		{
			if (!isStarted())
			{
				return false;
			}
			
			players.clear();
			activePlayers.clear();
			
			if (eventTerritory != null)
			{
				eventTerritory.removeListener(this);
			}
			
			final ObjectEventManager objectEventManager = ObjectEventManager.getInstance();
			objectEventManager.removeDeleteListener(this);
			objectEventManager.removeDieListener(this);
			World.sendAnnounce("Event \"" + getName() + "\" finished.");
			setStarted(false);
			setState(EventState.FINISHED);
			return true;
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method unlock.
	 * @see rlib.util.Synchronized#unlock()
	 */
	@Override
	public final void unlock()
	{
		lock.unlock();
	}
	
	/**
	 * Method unlockMove.
	 * @param player Player
	 */
	protected void unlockMove(Player player)
	{
		RUN_LOCKER.removeFuncTo(player);
	}
	
	/**
	 * Method unregisterPlayer.
	 * @param player Player
	 * @return boolean
	 * @see tera.gameserver.events.Registered#unregisterPlayer(Player)
	 */
	@Override
	public boolean unregisterPlayer(Player player)
	{
		lock();
		
		try
		{
			if (!isStarted())
			{
				player.sendMessage("Event is not running.");
				return false;
			}
			
			if (getState() != EventState.REGISTER)
			{
				player.sendMessage("Registration time is over.");
				return false;
			}
			
			final Array<Player> prepare = getPrepare();
			
			if (!prepare.contains(player))
			{
				player.sendMessage("You are not registred.");
				return false;
			}
			
			prepare.fastRemove(player);
			player.setEvent(false);
			player.sendMessage("You have registered.");
			return false;
		}
		
		finally
		{
			unlock();
		}
	}
}

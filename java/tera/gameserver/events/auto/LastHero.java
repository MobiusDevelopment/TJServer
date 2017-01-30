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

import tera.Config;
import tera.gameserver.events.EventConstant;
import tera.gameserver.events.EventPlayer;
import tera.gameserver.events.EventState;
import tera.gameserver.events.EventType;
import tera.gameserver.events.EventUtils;
import tera.gameserver.manager.EventManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.IconType;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.LinkType;
import tera.gameserver.model.npc.interaction.links.NpcLink;
import tera.gameserver.model.npc.interaction.replyes.Reply;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.AppledEffect;
import tera.gameserver.network.serverpackets.CancelEffect;
import tera.gameserver.tables.WorldZoneTable;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.util.Rnd;
import rlib.util.array.Array;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 * @created 11.04.2012
 */
public final class LastHero extends AbstractAutoEvent
{
	public static final String EVENT_NAME = "LH";
	public static final int TERRITORY_ID = 54;
	private static final Location CENTER = new Location(11995, 7736, 974, 0, 0);
	
	private final Reply REPLY_REGISTER = (npc, player, link) -> registerPlayer(player);
	private final Reply REPLY_UNREGISTER = (npc, player, link) -> unregisterPlayer(player);
	
	private final Link LINK_REGISTER = new NpcLink("Reg. LastHero", LinkType.DIALOG, IconType.GREEN, REPLY_REGISTER);
	private final Link LINK_UNREGISTER = new NpcLink("Unreg. LastHero", LinkType.DIALOG, IconType.GREEN, REPLY_UNREGISTER);
	
	/**
	 * Method addLinks.
	 * @param links Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	@Override
	public void addLinks(Array<Link> links, Npc npc, Player player)
	{
		if (!isStarted())
		{
			return;
		}
		
		if (npc.getTemplate() != EventConstant.MYSTEL)
		{
			return;
		}
		
		if (getState() != EventState.REGISTER)
		{
			return;
		}
		
		if ((player.getLevel() > getMaxLevel()) || (player.getLevel() < getMinLevel()))
		{
			return;
		}
		
		if (player.isDead())
		{
			return;
		}
		
		if (player.hasDuel())
		{
			return;
		}
		
		final Array<Player> prepare = getPrepare();
		
		if (prepare.contains(player))
		{
			links.add(LINK_UNREGISTER);
		}
		else
		{
			links.add(LINK_REGISTER);
		}
	}
	
	@Override
	protected void finishingState()
	{
		final Table<IntKey, EventPlayer> players = getPlayers();
		
		for (EventPlayer eventPlayer : players)
		{
			final Player player = eventPlayer.getPlayer();
			player.setFractionId(0);
			player.setResurrected(true);
			player.setEvent(false);
			eventPlayer.restoreState();
			player.updateInfo();
			eventPlayer.restoreLoc();
			eventPlayer.fold();
		}
		
		stop();
		final EventManager eventManager = EventManager.getInstance();
		eventManager.finish(this);
	}
	
	/**
	 * Method getMaxLevel.
	 * @return int
	 */
	@Override
	protected int getMaxLevel()
	{
		return Config.EVENT_LH_MAX_LEVEL;
	}
	
	/**
	 * Method getMinLevel.
	 * @return int
	 */
	@Override
	protected int getMinLevel()
	{
		return Config.EVENT_LH_MIN_LEVEL;
	}
	
	/**
	 * Method getName.
	 * @return String
	 * @see tera.gameserver.events.Event#getName()
	 */
	@Override
	public String getName()
	{
		return EVENT_NAME;
	}
	
	/**
	 * Method getRegisterTime.
	 * @return int
	 */
	@Override
	protected int getRegisterTime()
	{
		return Config.EVENT_LH_REGISTER_TIME;
	}
	
	/**
	 * Method getTerritoryId.
	 * @return int
	 */
	@Override
	protected int getTerritoryId()
	{
		return TERRITORY_ID;
	}
	
	/**
	 * Method getType.
	 * @return EventType
	 * @see tera.gameserver.events.Event#getType()
	 */
	@Override
	public EventType getType()
	{
		return EventType.LAST_HERO;
	}
	
	/**
	 * Method isCheckDieState.
	 * @return boolean
	 */
	@Override
	protected boolean isCheckDieState()
	{
		return (state == EventState.RUNNING) || (state == EventState.PREPARE_END);
	}
	
	/**
	 * Method isCheckTerritoryState.
	 * @return boolean
	 */
	@Override
	protected boolean isCheckTerritoryState()
	{
		return (state == EventState.RUNNING) || (state == EventState.PREPARE_END);
	}
	
	/**
	 * Method onDelete.
	 * @param player Player
	 */
	@Override
	@SuppressWarnings("incomplete-switch")
	protected void onDelete(Player player)
	{
		lock();
		
		try
		{
			switch (state)
			{
				case REGISTER:
				case PREPARE_START:
					getPrepare().fastRemove(player);
					break;
				
				case PREPARE_END:
				case RUNNING:
				{
					final EventPlayer eventPlayer = removeEventPlayer(player.getObjectId());
					
					if (eventPlayer != null)
					{
						eventPlayer.fold();
					}
					
					removeActivePlayer(player);
					updateResult();
				}
			}
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method onDie.
	 * @param killed Player
	 * @param killer Character
	 */
	@Override
	protected void onDie(Player killed, Character killer)
	{
		lock();
		
		try
		{
			removeActivePlayer(killed);
			updateResult();
		}
		
		finally
		{
			unlock();
		}
	}
	
	/**
	 * Method onEnter.
	 * @param player Player
	 */
	@Override
	protected void onEnter(Player player)
	{
		if (!player.isDead())
		{
			final WorldZoneTable zoneTable = WorldZoneTable.getInstance();
			player.teleToLocation(zoneTable.getRespawn(player));
		}
	}
	
	/**
	 * Method onExit.
	 * @param player Player
	 */
	@Override
	protected void onExit(Player player)
	{
		if (!player.isDead())
		{
			player.setCurrentHp(0);
			player.doDie(player);
			player.sendMessage("You are out of the event-zone.");
		}
	}
	
	@Override
	protected void prepareEndState()
	{
		World.sendAnnounce("The fight is over.");
		final Spawn[] guards = EventUtils.guards;
		
		for (Spawn guard : guards)
		{
			guard.stop();
		}
		
		final Array<Player> activePlayers = getActivePlayers();
		final Player player = activePlayers.first();
		final Table<IntKey, EventPlayer> players = getPlayers();
		
		if ((activePlayers.size() > 1) || (player == null))
		{
			World.sendAnnounce("Draw...");
		}
		else
		{
			World.sendAnnounce("Player " + player.getName() + " won.");
			int level = 0;
			
			for (EventPlayer eventPlayer : players)
			{
				final Player target = eventPlayer.getPlayer();
				
				if (target == null)
				{
					continue;
				}
				
				level += target.getLevel();
			}
			
			level /= players.size();
			final int reward = (int) Math.max((Math.sqrt(players.size()) * level) / 2, 1);
			synchronized (player)
			{
				player.setVar(EventConstant.VAR_NANE_HERO_POINT, player.getVar(EventConstant.VAR_NANE_HERO_POINT, 0) + reward);
			}
			player.sendMessage("You received" + reward + " points of glory.");
		}
		
		setState(EventState.FINISHING);
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleGeneral(this, 5000);
	}
	
	@Override
	protected void prepareStartState()
	{
		final Array<Player> prepare = getPrepare();
		final Array<Player> activePlayers = getActivePlayers();
		final Table<IntKey, EventPlayer> players = getPlayers();
		Player[] array = prepare.array();
		
		for (int i = 0, length = prepare.size(); i < length; i++)
		{
			if (i >= Config.EVENT_LH_MAX_PLAYERS)
			{
				break;
			}
			
			final Player player = array[i];
			
			if (player.isDead())
			{
				player.sendMessage("You are dead.");
				continue;
			}
			
			if (player.hasDuel())
			{
				player.sendMessage(MessageType.YOU_ARE_IN_A_DUEL_NOW);
				continue;
			}
			
			if (players.containsKey(player.getObjectId()))
			{
				continue;
			}
			
			player.setResurrected(false);
			final EventPlayer eventPlayer = EventPlayer.newInstance(player);
			players.put(player.getObjectId(), eventPlayer);
			player.setFractionId(i + 1);
			activePlayers.add(player);
			eventPlayer.saveLoc();
			eventPlayer.saveState();
		}
		
		prepare.clear();
		
		if (players.size() < Config.EVENT_LH_MIN_PLAYERS)
		{
			World.sendAnnounce("Insufficient number of participants.");
			state = EventState.FINISHING;
			
			if (schedule != null)
			{
				schedule.cancel(true);
			}
			
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.execute(this);
			return;
		}
		
		World.sendAnnounce("The event will involve " + players.size() + " players.");
		World.sendAnnounce("The battle will start after 1 minute.");
		array = activePlayers.array();
		final Location loc = EventUtils.takeLocation();
		
		for (int i = 0, length = activePlayers.size(); i < length; i++)
		{
			final Player player = array[i];
			player.teleToLocation(Coords.randomCoords(loc, CENTER.getX(), CENTER.getY(), CENTER.getZ(), Rnd.nextInt(65000), 40, 300));
			lockMove(player);
			player.setStuned(true);
			player.broadcastPacket(AppledEffect.getInstance(player, player, 701100, 60000));
			player.updateInfo();
		}
		
		EventUtils.putLocation(loc);
		clearTerritory();
		setState(EventState.RUNNING);
	}
	
	@Override
	protected void runningState()
	{
		final Spawn[] guards = EventUtils.guards;
		
		for (Spawn guard : guards)
		{
			guard.start();
		}
		
		final Array<Player> activePlayers = getActivePlayers();
		final Player[] array = activePlayers.array();
		
		for (int i = 0, length = activePlayers.size(); i < length; i++)
		{
			final Player player = array[i];
			unlockMove(player);
			player.setStamina(player.getMaxStamina());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			player.setStuned(false);
			player.broadcastPacket(CancelEffect.getInstance(player, 701100));
			player.updateInfo();
			player.updateInfo();
		}
		
		World.sendAnnounce("TO BATTLE!!!");
		setState(EventState.PREPARE_END);
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, Config.EVENT_LH_BATTLE_TIME * 60 * 1000);
		updateResult();
	}
	
	private void updateResult()
	{
		lock();
		
		try
		{
			final Array<TObject> objects = getEventTerritory().getObjects();
			final Array<Player> activePlayers = getActivePlayers();
			final Player[] array = activePlayers.array();
			
			for (int i = 0, length = activePlayers.size(); i < length; i++)
			{
				final Player player = array[i];
				
				if (player == null)
				{
					continue;
				}
				
				if (!objects.contains(player))
				{
					final EventPlayer eventPlayer = removeEventPlayer(player.getObjectId());
					
					if (eventPlayer != null)
					{
						eventPlayer.fold();
					}
					
					activePlayers.fastRemove(player);
				}
			}
			
			if (activePlayers.size() < 2)
			{
				if (getState() == EventState.PREPARE_END)
				{
					if (schedule != null)
					{
						schedule.cancel(false);
					}
					
					final ExecutorManager executor = ExecutorManager.getInstance();
					schedule = executor.scheduleGeneral(this, 5000);
				}
			}
		}
		
		finally
		{
			unlock();
		}
	}
}

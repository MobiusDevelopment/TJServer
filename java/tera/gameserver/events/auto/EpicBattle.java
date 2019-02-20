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
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.IconType;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.LinkType;
import tera.gameserver.model.npc.interaction.links.NpcLink;
import tera.gameserver.model.npc.interaction.replyes.Reply;
import tera.gameserver.model.npc.playable.EventEpicBattleNpc;
import tera.gameserver.model.npc.spawn.NpcSpawn;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.AppledEffect;
import tera.gameserver.network.serverpackets.CancelEffect;
import tera.gameserver.tables.ConfigAITable;
import tera.gameserver.tables.NpcTable;
import tera.gameserver.tables.WorldZoneTable;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.random.Random;
import rlib.util.random.Randoms;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 * @created 11.04.2012
 */
public final class EpicBattle extends AbstractAutoEvent
{
	public static final String EVENT_NAME = "EpicBattle";
	public static final int TERRITORY_ID = 54;
	private static final Location[] playerPoints =
	{
		new Location(10970, 7848, 974, 0, 0),
		new Location(11148, 7978, 975, 0, 0),
		new Location(11282, 8079, 979, 0, 0),
		new Location(11464, 8206, 983, 0, 0),
		new Location(11647, 8337, 977, 0, 0),
		new Location(11838, 8466, 977, 0, 0),
		new Location(12106, 8658, 974, 0, 0),
	};
	
	private static final Location[] bootsPoints =
	{
		new Location(11818, 6736, 978, 0, 0),
		new Location(12000, 6858, 976, 0, 0),
		new Location(12179, 6996, 976, 0, 0),
		new Location(12390, 7156, 976, 0, 0),
		new Location(12614, 7336, 978, 0, 0),
		new Location(12925, 7576, 972, 0, 0),
	};
	
	private final NpcTemplate[] NPC_TYPES;
	private final ConfigAI[] NPC_CONFIG;
	private final Table<NpcTemplate, Array<NpcSpawn>> spawnPool;
	private final Reply REPLY_REGISTER = (npc, player, link) -> registerPlayer(player);
	private final Reply REPLY_UNREGISTER = (npc, player, link) -> unregisterPlayer(player);
	private final Link LINK_REGISTER = new NpcLink("Reg. Epic Battle", LinkType.DIALOG, IconType.GREEN, REPLY_REGISTER);
	private final Link LINK_UNREGISTER = new NpcLink("Unreg. Epic Battle", LinkType.DIALOG, IconType.GREEN, REPLY_UNREGISTER);
	private final Array<Player> playerTeam;
	private final Array<EventEpicBattleNpc> bootsTeam;
	private final Array<NpcSpawn> currentSpawns;
	private final Random random;
	
	public EpicBattle()
	{
		playerTeam = Arrays.toArray(Player.class);
		bootsTeam = Arrays.toArray(EventEpicBattleNpc.class);
		currentSpawns = Arrays.toArray(NpcSpawn.class);
		random = Randoms.newRealRandom();
		spawnPool = Tables.newObjectTable();
		final NpcTable npcTable = NpcTable.getInstance();
		NPC_TYPES = new NpcTemplate[]
		{
			npcTable.getTemplate(20001, 300),
			npcTable.getTemplate(20002, 300),
			npcTable.getTemplate(20003, 300),
			npcTable.getTemplate(20004, 300),
			npcTable.getTemplate(20005, 300),
			npcTable.getTemplate(20006, 300),
		};
		final ConfigAITable configTable = ConfigAITable.getInstance();
		NPC_CONFIG = new ConfigAI[]
		{
			configTable.getConfig("PlayableWarrior"),
			configTable.getConfig("PlayableSorcerer"),
			configTable.getConfig("PlayablePriest"),
			configTable.getConfig("PlayableSlayer"),
			configTable.getConfig("PlayableLancer"),
			configTable.getConfig("PlayableArcher"),
		};
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
		
		final NpcSpawn[] array = currentSpawns.array();
		
		for (int i = 0, length = currentSpawns.size(); i < length; i++)
		{
			final NpcSpawn spawn = array[i];
			spawn.stop();
			final NpcTemplate template = spawn.getTemplate();
			final Array<NpcSpawn> pool = spawnPool.get(template);
			pool.add(spawn);
		}
		
		currentSpawns.clear();
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
		return Config.EVENT_EB_MAX_LEVEL;
	}
	
	/**
	 * Method getMinLevel.
	 * @return int
	 */
	@Override
	protected int getMinLevel()
	{
		return Config.EVENT_EB_MIN_LEVEL;
	}
	
	/**
	 * Method getMonsterTeam.
	 * @return Array<EventEpicBattleNpc>
	 */
	public Array<EventEpicBattleNpc> getMonsterTeam()
	{
		return bootsTeam;
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
	 * Method getPlayerTeam.
	 * @return Array<Player>
	 */
	public Array<Player> getPlayerTeam()
	{
		return playerTeam;
	}
	
	/**
	 * Method getRegisterTime.
	 * @return int
	 */
	@Override
	protected int getRegisterTime()
	{
		return Config.EVENT_EB_REGISTER_TIME;
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
		return EventType.TEAM_VS_MONSTERS;
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
	protected void onDelete(Player player)
	{
		lock();
		
		try
		{
			switch (getState())
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
					
					playerTeam.fastRemove(player);
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
	 * @param killer Character
	 * @param killed Character
	 * @see tera.gameserver.model.listeners.DieListener#onDie(Character, Character)
	 */
	@Override
	public void onDie(Character killer, Character killed)
	{
		if (!isCheckDieState() || !(killed.isPlayer() || (killed.getClass() == EventEpicBattleNpc.class)))
		{
			return;
		}
		
		lock();
		
		try
		{
			if (killed.isPlayer() && playerTeam.contains(killed))
			{
				playerTeam.fastRemove(killed);
				updateResult();
			}
			else if (bootsTeam.contains(killed))
			{
				bootsTeam.fastRemove(killed);
				updateResult();
			}
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
			player.teleToLocation(zoneTable.getDefaultRespawn(player));
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
			player.sendMessage("You left the event area.");
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
		
		int winner = -1;
		
		if (playerTeam.isEmpty() && !bootsTeam.isEmpty())
		{
			World.sendAnnounce("The winning team of the elite!");
		}
		else if (!playerTeam.isEmpty() && bootsTeam.isEmpty())
		{
			World.sendAnnounce("The winning team players!");
			winner = 1;
		}
		else
		{
			World.sendAnnounce("Draw...");
		}
		
		if (winner == 1)
		{
			final Table<IntKey, EventPlayer> players = getPlayers();
			
			for (EventPlayer eventPlayer : players)
			{
				final Player player = eventPlayer.getPlayer();
				
				if (player.getFractionId() != winner)
				{
					continue;
				}
				
				final int reward = (int) Math.max(Math.sqrt(players.size()) * Math.sqrt(player.getLevel()), 1) * 2;
				synchronized (player)
				{
					player.setVar(EventConstant.VAR_NANE_HERO_POINT, player.getVar(EventConstant.VAR_NANE_HERO_POINT, 0) + reward);
				}
				player.sendMessage("You received " + reward + " points of glory.");
			}
		}
		
		setState(EventState.FINISHING);
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleGeneral(this, 5000);
	}
	
	@Override
	protected void prepareStartState()
	{
		final Array<Player> prepare = getPrepare();
		final Table<IntKey, EventPlayer> players = getPlayers();
		Player[] array = prepare.array();
		
		for (int i = 0, length = prepare.size(); i < length; i++)
		{
			if (i >= Config.EVENT_EB_MAX_PLAYERS)
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
			player.setFractionId(1);
			playerTeam.add(player);
			eventPlayer.saveLoc();
			eventPlayer.saveState();
		}
		
		prepare.clear();
		
		if (players.size() < Config.EVENT_EB_MIN_PLAYERS)
		{
			World.sendAnnounce("Insufficient number of participants.");
			setState(EventState.FINISHING);
			
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
		array = playerTeam.array();
		Location loc = EventUtils.takeLocation();
		
		for (int i = 0, length = playerTeam.size(); i < length; i++)
		{
			final Player player = array[i];
			final Location point = playerPoints[random.nextInt(0, playerPoints.length - 1)];
			loc = Coords.randomCoords(loc, point.getX(), point.getY(), point.getZ(), 10, 100);
			loc.setContinentId(0);
			player.teleToLocation(loc);
			lockMove(player);
			player.setStuned(true);
			player.broadcastPacket(AppledEffect.getInstance(player, player, 701100, 60000));
			player.updateInfo();
		}
		
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
		
		final Player[] array = playerTeam.array();
		
		for (int i = 0, length = playerTeam.size(); i < length; i++)
		{
			final Player player = array[i];
			unlockMove(player);
			player.setStamina(player.getMaxStamina());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			player.setStuned(false);
			player.broadcastPacket(CancelEffect.getInstance(player, 701100));
			player.updateInfo();
		}
		
		for (int i = 0, length = NPC_TYPES.length; i < length; i++)
		{
			final NpcTemplate template = NPC_TYPES[i];
			final ConfigAI config = NPC_CONFIG[i];
			Array<NpcSpawn> pool = spawnPool.get(template);
			
			if (pool == null)
			{
				pool = Arrays.toArray(NpcSpawn.class);
				spawnPool.put(template, pool);
			}
			
			NpcSpawn spawn = pool.pop();
			
			if (spawn == null)
			{
				spawn = new NpcSpawn(template, new Location(), config, NpcAIClass.EPIC_BATTLE_EVENT);
			}
			
			final Location point = bootsPoints[random.nextInt(0, bootsPoints.length - 1)];
			final Location loc = spawn.getLocation();
			loc.setXYZHC(point.getX(), point.getY(), point.getZ(), -1, 0);
			spawn.setMaxRadius(100);
			spawn.setMinRadius(60);
			spawn.setRespawnTime(1800);
			currentSpawns.add(spawn);
			spawn.start();
			bootsTeam.add((EventEpicBattleNpc) spawn.getSpawned());
		}
		
		World.sendAnnounce("TO BATTLE!!!");
		setState(EventState.PREPARE_END);
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, Config.EVENT_EB_BATTLE_TIME * 60 * 1000);
		updateResult();
	}
	
	/**
	 * Method stop.
	 * @return boolean
	 * @see tera.gameserver.events.Event#stop()
	 */
	@Override
	public synchronized boolean stop()
	{
		if (super.stop())
		{
			playerTeam.clear();
			bootsTeam.clear();
			return true;
		}
		
		return false;
	}
	
	private void updateResult()
	{
		lock();
		
		try
		{
			final Array<TObject> objects = getEventTerritory().getObjects();
			final Player[] array = playerTeam.array();
			
			for (int i = 0, length = playerTeam.size(); i < length; i++)
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
					
					playerTeam.fastRemove(player);
				}
			}
			
			if (playerTeam.isEmpty() || bootsTeam.isEmpty())
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

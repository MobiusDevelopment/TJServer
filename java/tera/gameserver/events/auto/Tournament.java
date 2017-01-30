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
import tera.gameserver.events.EventTeam;
import tera.gameserver.events.EventType;
import tera.gameserver.events.EventUtils;
import tera.gameserver.manager.EventManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MessageType;
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
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.random.Random;
import rlib.util.random.Randoms;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public class Tournament extends AbstractAutoEvent
{
	private static final Location CENTER = new Location(11995, 7736, 974, 0, 0);
	public static final String EVENT_NAME = "Tournament";
	public static final int TERRITORY_ID = 54;
	private static final Location[] firstPoints =
	{
		new Location(11670, 8222, 977, 0, 0),
		new Location(11600, 8179, 977, 0, 0),
		new Location(11748, 8274, 977, 0, 0),
		new Location(11522, 8128, 977, 0, 0),
		new Location(11829, 8327, 977, 0, 0),
	};
	private static final Location[] secondPoints =
	{
		new Location(12356, 7232, 975, 0, 0),
		new Location(12423, 7275, 975, 0, 0),
		new Location(12285, 7184, 975, 0, 0),
		new Location(12501, 7327, 975, 0, 0),
		new Location(12201, 7130, 975, 0, 0),
	};
	private final Reply REPLY_REGISTER = (npc, player, link) -> registerPlayer(player);
	private final Reply REPLY_UNREGISTER = (npc, player, link) -> unregisterPlayer(player);
	private final Link LINK_REGISTER = new NpcLink("Reg. Tournament", LinkType.DIALOG, IconType.GREEN, REPLY_REGISTER);
	private final Link LINK_UNREGISTER = new NpcLink("Unreg. Tournament", LinkType.DIALOG, IconType.GREEN, REPLY_UNREGISTER);
	private final Table<IntKey, EventTeam> playerTeams;
	private final Array<EventTeam> teams;
	private final Array<EventTeam> teamSet;
	private final Array<Location> points;
	private final Random random;
	private EventTeam first;
	private EventTeam second;
	private int teamSize;
	private int level;
	
	public Tournament()
	{
		teams = Arrays.toArray(EventTeam.class);
		teamSet = Arrays.toArray(EventTeam.class);
		points = Arrays.toArray(Location.class);
		playerTeams = Tables.newIntegerTable();
		random = Randoms.newRealRandom();
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
		final Spawn[] guards = EventUtils.guards;
		
		for (Spawn guard : guards)
		{
			guard.stop();
		}
		
		if (teams.size() != 1)
		{
			World.sendAnnounce("Draw...");
		}
		else if (teams.size() == 1)
		{
			final Table<IntKey, EventPlayer> players = getPlayers();
			final EventTeam team = teams.first();
			World.sendAnnounce("The winning team is \"" + team.getName() + "\".");
			final EventPlayer[] eventPlayers = team.getPlayers();
			
			for (int i = 0, length = team.size(); i < length; i++)
			{
				final EventPlayer eventPlayer = eventPlayers[i];
				
				if (eventPlayer == null)
				{
					continue;
				}
				
				final Player player = eventPlayer.getPlayer();
				
				if (player == null)
				{
					continue;
				}
				
				final int reward = (int) Math.max(Math.sqrt(players.size()) * Math.sqrt(player.getLevel()), 1) * 2;
				synchronized (player)
				{
					player.setVar(EventConstant.VAR_NANE_HERO_POINT, player.getVar(EventConstant.VAR_NANE_HERO_POINT, 0) + reward);
				}
				player.sendMessage("You got " + reward + " points of glory.");
			}
		}
		
		stopLock();
		ressurectPlayers();
		
		for (EventPlayer eventPlayer : getPlayers())
		{
			final Player player = eventPlayer.getPlayer();
			player.setFractionId(0);
			player.setResurrected(true);
			player.setEvent(false);
			eventPlayer.restoreState();
			player.updateInfo();
			eventPlayer.restoreLoc();
		}
		
		for (EventTeam team : playerTeams)
		{
			if (!teamSet.contains(team))
			{
				teamSet.add(team);
			}
		}
		
		final EventTeam[] arrayTeams = teamSet.array();
		
		for (int i = 0, length = teamSet.size(); i < length; i++)
		{
			arrayTeams[i].fold();
		}
		
		final Location[] arrayLoc = points.array();
		
		for (int i = 0, length = points.size(); i < length; i++)
		{
			EventUtils.putLocation(arrayLoc[i]);
		}
		
		points.clear();
		playerTeams.clear();
		teams.clear();
		teamSet.clear();
		setFirst(null);
		setSecond(null);
		stop();
		final EventManager eventManager = EventManager.getInstance();
		eventManager.finish(this);
	}
	
	/**
	 * Method getFirst.
	 * @return EventTeam
	 */
	protected final EventTeam getFirst()
	{
		return first;
	}
	
	/**
	 * Method getMaxLevel.
	 * @return int
	 */
	@Override
	protected int getMaxLevel()
	{
		return Config.EVENT_TMT_MAX_LEVEL;
	}
	
	/**
	 * Method getMinLevel.
	 * @return int
	 */
	@Override
	protected int getMinLevel()
	{
		return Config.EVENT_TMT_MIN_LEVEL;
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
	 * Method getPlayerTeams.
	 * @return Table<IntKey,EventTeam>
	 */
	public Table<IntKey, EventTeam> getPlayerTeams()
	{
		return playerTeams;
	}
	
	/**
	 * Method getRegisterTime.
	 * @return int
	 */
	@Override
	protected int getRegisterTime()
	{
		return Config.EVENT_TMT_REGISTER_TIME;
	}
	
	/**
	 * Method getSecond.
	 * @return EventTeam
	 */
	protected final EventTeam getSecond()
	{
		return second;
	}
	
	/**
	 * Method getTeams.
	 * @return Array<EventTeam>
	 */
	public Array<EventTeam> getTeams()
	{
		return teams;
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
		return null;
	}
	
	/**
	 * Method isCheckDieState.
	 * @return boolean
	 */
	@Override
	protected boolean isCheckDieState()
	{
		return state == EventState.PREPARE_END;
	}
	
	/**
	 * Method isCheckTerritoryState.
	 * @return boolean
	 */
	@Override
	protected boolean isCheckTerritoryState()
	{
		return (state == EventState.PREPARE_END) || (state == EventState.RUNNING);
	}
	
	/**
	 * Method movePlayersToPoints.
	 * @param players EventPlayer[]
	 * @param count int
	 * @param fractionId int
	 * @param points Location[]
	 */
	protected void movePlayersToPoints(EventPlayer[] players, int count, int fractionId, Location[] points)
	{
		for (int i = 0; i < count; i++)
		{
			final Player player = players[i].getPlayer();
			player.setFractionId(fractionId);
			player.teleToLocation(points[i]);
			lockMove(player);
			player.setStuned(true);
			player.broadcastPacket(AppledEffect.getInstance(player, player, EventUtils.SLEEP_ID, 30000));
			player.updateInfo();
		}
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
			final Table<IntKey, EventPlayer> players = getPlayers();
			final Table<IntKey, EventTeam> playerTeams = getPlayerTeams();
			final EventTeam team = playerTeams.get(player.getObjectId());
			
			if (team == null)
			{
				return;
			}
			
			team.removePlayer(players.get(player.getObjectId()));
		}
		
		finally
		{
			unlock();
		}
		updateState();
	}
	
	/**
	 * Method onDie.
	 * @param killed Player
	 * @param killer Character
	 */
	@Override
	protected void onDie(Player killed, Character killer)
	{
		updateState();
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
			player.sendMessage("You are out of the event-zone.");
		}
		
		updateState();
	}
	
	@Override
	protected void prepareBattleState()
	{
		final Array<EventTeam> teams = getTeams();
		
		if (teams.size() < 2)
		{
			setState(EventState.FINISHING);
			
			if (schedule != null)
			{
				schedule.cancel(false);
			}
			
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.execute(this);
			return;
		}
		
		EventTeam first = null;
		EventTeam second = null;
		final EventTeam[] array = teams.array();
		
		for (int i = 0, length = teams.size(); i < length; i++)
		{
			final EventTeam team = array[i];
			
			if (team.getLevel() == level)
			{
				first = team;
				break;
			}
		}
		
		if (first == null)
		{
			setState(EventState.FINISHING);
			
			if (schedule != null)
			{
				schedule.cancel(false);
			}
			
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.execute(this);
			return;
		}
		
		for (int i = teams.size() - 1; i > 0; i--)
		{
			final EventTeam team = array[i];
			
			if ((team != first) && (team.getLevel() == level))
			{
				second = team;
				break;
			}
		}
		
		if (second == null)
		{
			first.increaseLevel();
			level++;
			
			if (schedule != null)
			{
				schedule.cancel(false);
			}
			
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.execute(this);
			return;
		}
		
		ressurectPlayers();
		returnPlayers(getFirst());
		returnPlayers(getSecond());
		stopLock();
		setFirst(first);
		setSecond(second);
		movePlayersToPoints(first.getPlayers(), first.size(), 1, firstPoints);
		movePlayersToPoints(second.getPlayers(), second.size(), 2, secondPoints);
		startLock();
		World.sendAnnounce("Stage " + (level + 1) + ": fight in 1 minute \"" + first.getName() + "\" Vs \"" + second.getName() + "\"!");
		setState(EventState.RUNNING);
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, 30000);
	}
	
	@Override
	protected void prepareEndState()
	{
		final EventTeam first = getFirst();
		final EventTeam second = getSecond();
		
		if ((first == null) || (second == null))
		{
			setState(EventState.FINISHING);
			
			if (schedule != null)
			{
				schedule.cancel(false);
			}
			
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.execute(this);
			return;
		}
		
		if ((first.isDead() && second.isDead()) || (!first.isDead() && !second.isDead()))
		{
			World.sendAnnounce("Draw...");
			teams.fastRemove(second);
			teams.fastRemove(first);
		}
		else
		{
			EventTeam winner = null;
			EventTeam loser = null;
			
			if (first.isDead() && !second.isDead())
			{
				winner = second;
				loser = first;
			}
			else
			{
				winner = first;
				loser = second;
			}
			
			World.sendAnnounce("The winning team is \"" + winner.getName() + "\"!!!");
			teams.fastRemove(loser);
			winner.increaseLevel();
		}
		
		setState(EventState.PREPARE_BATLE);
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, 5000);
	}
	
	@Override
	protected void prepareStartState()
	{
		final Spawn[] guards = EventUtils.guards;
		
		for (Spawn guard : guards)
		{
			guard.start();
		}
		
		final Array<Player> prepare = getPrepare();
		final Table<IntKey, EventPlayer> players = getPlayers();
		final Array<Player> activePlayers = getActivePlayers();
		
		if (prepare.size() < ((teamSize * Config.EVENT_TMT_MIN_TEAMS) - Math.max(0, teamSize - 1)))
		{
			World.sendAnnounce("Insufficient number of participants.");
			setState(EventState.FINISHING);
			
			if (schedule != null)
			{
				schedule.cancel(false);
			}
			
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.execute(this);
			return;
		}
		
		EventTeam team = null;
		
		while (!prepare.isEmpty())
		{
			final Player player = random.chance(50) ? prepare.poll() : prepare.pop();
			
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
			
			if (team == null)
			{
				team = EventTeam.newInstance();
			}
			
			team.addPlayer(eventPlayer);
			playerTeams.put(player.getObjectId(), team);
			activePlayers.add(player);
			eventPlayer.saveLoc();
			eventPlayer.saveState();
			
			if (team.size() == teamSize)
			{
				teams.add(team);
				team = null;
			}
		}
		
		prepare.clear();
		
		if (teams.size() < Config.EVENT_TMT_MIN_TEAMS)
		{
			setState(EventState.FINISHING);
			
			if (schedule != null)
			{
				schedule.cancel(false);
			}
			
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.execute(this);
			return;
		}
		
		for (int i = 0, length = players.size(); i < length; i++)
		{
			final Location loc = EventUtils.takeLocation();
			loc.setContinentId(0);
			points.add(loc);
		}
		
		final Location[] locs = Coords.getCircularPoints(points.array(), CENTER.getX(), CENTER.getY(), CENTER.getZ(), players.size(), 440);
		final Player[] array = activePlayers.array();
		
		for (int i = 0, length = activePlayers.size(); i < length; i++)
		{
			final Player player = array[i];
			player.teleToLocation(locs[i]);
			lockMove(player);
			player.setStuned(true);
			player.setInvul(true);
			player.broadcastPacket(AppledEffect.getInstance(player, player, EventUtils.SLEEP_ID, 30000));
			player.updateInfo();
			team = playerTeams.get(player.getObjectId());
		}
		
		World.sendAnnounce("The event will involve " + players.size() + " players and " + teams.size() + " teams.");
		World.sendAnnounce("After 30 seconds, the battle will start.");
		clearTerritory();
		setState(EventState.PREPARE_BATLE);
		
		if (schedule != null)
		{
			schedule.cancel(false);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, 30000);
	}
	
	protected void ressurectPlayers()
	{
		final Array<Player> players = getActivePlayers();
		final Player[] array = players.array();
		
		for (int i = 0, length = players.size(); i < length; i++)
		{
			final Player player = array[i];
			
			if (player.isDead())
			{
				player.setStamina(player.getMaxStamina());
				player.setCurrentHp(player.getMaxHp());
				player.setCurrentMp(player.getMaxMp());
			}
		}
	}
	
	/**
	 * Method returnPlayers.
	 * @param players EventPlayer[]
	 * @param count int
	 */
	protected void returnPlayers(EventPlayer[] players, int count)
	{
		final Array<Player> activePlayers = getActivePlayers();
		
		for (int i = 0; i < count; i++)
		{
			final Player player = players[i].getPlayer();
			final int index = activePlayers.indexOf(player);
			
			if (index < 0)
			{
				continue;
			}
			
			player.setFractionId(0);
			player.teleToLocation(points.get(index));
		}
	}
	
	/**
	 * Method returnPlayers.
	 * @param team EventTeam
	 */
	protected void returnPlayers(EventTeam team)
	{
		if (team == null)
		{
			return;
		}
		
		returnPlayers(team.getPlayers(), team.size());
	}
	
	@Override
	protected void runningState()
	{
		unlockPlayers(first.getPlayers(), second.size());
		unlockPlayers(second.getPlayers(), second.size());
		World.sendAnnounce("TO BATTLE!!!");
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, Config.EVENT_TVT_BATTLE_TIME * 60000);
		setState(EventState.PREPARE_END);
	}
	
	/**
	 * Method setFirst.
	 * @param var EventTeam
	 */
	protected final void setFirst(EventTeam var)
	{
		first = var;
	}
	
	/**
	 * Method setSecond.
	 * @param var EventTeam
	 */
	protected final void setSecond(EventTeam var)
	{
		second = var;
	}
	
	/**
	 * Method start.
	 * @return boolean
	 * @see tera.gameserver.events.Event#start()
	 */
	@Override
	public boolean start()
	{
		if (super.start())
		{
			teamSize = random.nextInt(Config.EVENT_TMT_MIN_TEAM_SIZE, Config.EVENT_TMT_MAX_TEAM_SIZE);
			level = 0;
		}
		
		return false;
	}
	
	protected void startLock()
	{
		final Array<Player> activePlayers = getActivePlayers();
		final Player[] array = activePlayers.array();
		final EventTeam first = getFirst();
		final EventTeam second = getSecond();
		
		for (int i = 0, length = activePlayers.size(); i < length; i++)
		{
			final Player player = array[i];
			final EventTeam team = playerTeams.get(player.getObjectId());
			
			if ((team == first) || (team == second))
			{
				continue;
			}
			
			lockMove(player);
			player.setStuned(true);
			player.setInvul(true);
			player.broadcastPacket(AppledEffect.getInstance(player, player, EventUtils.SLEEP_ID, (Config.EVENT_TMT_BATTLE_TIME + 1) * 60000));
			player.updateInfo();
		}
	}
	
	protected void stopLock()
	{
		final Array<Player> activePlayers = getActivePlayers();
		final Player[] array = activePlayers.array();
		
		for (int i = 0, length = activePlayers.size(); i < length; i++)
		{
			final Player player = array[i];
			unlockMove(player);
			player.setStuned(false);
			player.setInvul(false);
			player.broadcastPacket(CancelEffect.getInstance(player, EventUtils.SLEEP_ID));
			player.updateInfo();
		}
	}
	
	/**
	 * Method unlockPlayers.
	 * @param players EventPlayer[]
	 * @param count int
	 */
	protected void unlockPlayers(EventPlayer[] players, int count)
	{
		for (int i = 0; i < count; i++)
		{
			final Player player = players[i].getPlayer();
			unlockMove(player);
			player.setStamina(player.getMaxStamina());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			player.setStuned(false);
			player.setInvul(false);
			player.broadcastPacket(CancelEffect.getInstance(player, EventUtils.SLEEP_ID));
			player.updateInfo();
		}
	}
	
	private void updateState()
	{
		lock();
		
		try
		{
			final EventTeam first = getFirst();
			final EventTeam second = getSecond();
			
			if ((first == null) || first.isDead() || (second == null) || second.isDead())
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
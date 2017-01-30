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
package tera.gameserver.events.global.regionwars;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.GeoManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildRank;
import tera.gameserver.model.SayType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.listeners.DieListener;
import tera.gameserver.model.listeners.PlayerSelectListener;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.NpcType;
import tera.gameserver.model.npc.RegionWarBarrier;
import tera.gameserver.model.npc.RegionWarControl;
import tera.gameserver.model.npc.interaction.IconType;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.LinkType;
import tera.gameserver.model.npc.interaction.links.ControlLink;
import tera.gameserver.model.npc.interaction.links.NpcLink;
import tera.gameserver.model.npc.interaction.replyes.Reply;
import tera.gameserver.model.npc.spawn.RegionWarSpawn;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.territory.RegionTerritory;
import tera.gameserver.network.serverpackets.CharSay;
import tera.gameserver.network.serverpackets.ServerPacket;
import tera.gameserver.tables.WorldZoneTable;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.geom.Coords;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Rnd;
import rlib.util.SafeTask;
import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.FuncValue;
import rlib.util.table.Table;
import rlib.util.table.Tables;
import rlib.util.wraps.Wrap;
import rlib.util.wraps.Wraps;

/**
 * @author Ronn
 */
public class Region extends SafeTask implements DieListener, PlayerSelectListener
{
	private static final Logger log = Loggers.getLogger(Region.class);
	private final Reply REPLY_REGISTER = (npc, player, link) -> register(npc, player);
	private final Reply REPLY_UNREGISTER = (npc, player, link) -> unregister(npc, player);
	private final Reply REPLY_STATUS = (npc, player, link) -> status(npc, player);
	private final Reply REPLY_CONTROL = (npc, player, link) ->
	{
		if (link.test(npc, player))
		{
			teleportTo(player, link);
		}
	};
	private final FuncValue<Wrap> WRAP_FOLD_FUNC = value -> value.fold();
	private final Runnable REWARD_TASK = new SafeTask()
	{
		@Override
		protected void runImpl()
		{
			final LocalObjects local = LocalObjects.get();
			final Array<Player> players = local.getNextPlayerList();
			final Spawn[] control = getControl();
			
			for (Spawn spawn : control)
			{
				final RegionWarControl npc = (RegionWarControl) ((RegionWarSpawn) spawn).getSpawned();
				final Guild guild = npc.getGuildOwner();
				
				if (guild == null)
				{
					continue;
				}
				
				World.getAround(Player.class, players, npc, 400);
				final Player[] array = players.array();
				
				for (int g = 0, size = players.size(); g < size; g++)
				{
					final Player player = array[g];
					
					if (player.getGuild() == guild)
					{
						synchronized (player)
						{
							player.setVar(RegionWars.BATTLE_POINT, player.getVar(RegionWars.BATTLE_POINT, 0) + 1);
						}
						player.sendMessage("You got a battle point.");
					}
				}
				
				players.clear();
			}
		}
	};
	private final Link LINK_REGISTER = new NpcLink("Register", LinkType.DIALOG, IconType.GREEN, REPLY_REGISTER);
	private final Link LINK_UNREGISTER = new NpcLink("Unregister", LinkType.DIALOG, IconType.GREEN, REPLY_UNREGISTER);
	private final Link LINK_STATUS = new NpcLink("Status", LinkType.DIALOG, IconType.GREEN, REPLY_STATUS);
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
	private final Date date = new Date();
	private final Array<Guild> registerGuilds;
	private final Array<Guild> guildKeys;
	private final Array<Spawn> monsterSpawns;
	private final RegionWars event;
	private final RegionTerritory territory;
	private final Table<Guild, Wrap> controlTable;
	private Guild owner;
	private Func[] positive;
	private Func[] negative;
	private Spawn[] defense;
	private Spawn[] barriers;
	private Spawn[] control;
	private Spawn[] manager;
	private Spawn[] shops;
	private ControlLink[] controlLinks;
	private String nextBattleDate;
	private volatile RegionState state;
	private volatile ScheduledFuture<Region> schedule;
	private volatile ScheduledFuture<Runnable> rewardSchedule;
	private long startTime;
	private long interval;
	private long nextBattle;
	private long battleTime;
	private int tax;
	
	public Region(RegionWars evt, RegionTerritory ter)
	{
		state = RegionState.WAIT_WAR;
		event = evt;
		territory = ter;
		territory.setRegion(this);
		registerGuilds = Arrays.toConcurrentArray(Guild.class);
		guildKeys = Arrays.toArray(Guild.class);
		controlTable = Tables.newObjectTable();
		monsterSpawns = Arrays.toArray(Spawn.class);
	}
	
	private void addFuncsTo(Func[] funcs, Player player)
	{
		if (funcs.length < 1)
		{
			return;
		}
		
		for (Func func : funcs)
		{
			func.addFuncTo(player);
		}
	}
	
	public void addFuncsTo(Player player)
	{
		final Guild owner = getOwner();
		
		if (owner == null)
		{
			return;
		}
		
		if (player.getGuild() != owner)
		{
			addFuncsTo(getNegative(), player);
		}
		else
		{
			addFuncsTo(getPositive(), player);
		}
	}
	
	public void addLinks(Array<Link> links, Npc npc, Player player)
	{
		if (!Arrays.contains(getManager(), npc.getSpawn()))
		{
			return;
		}
		
		final GuildRank rank = player.getGuildRank();
		final Guild guild = player.getGuild();
		
		if ((guild != null) && (rank != null))
		{
			if ((getState() == RegionState.PREPARE_START_WAR) && (guild != getOwner()))
			{
				if (rank.isGuildMaster())
				{
					links.add(registerGuilds.contains(guild) ? LINK_UNREGISTER : LINK_REGISTER);
				}
			}
			else if (getState() == RegionState.PREPARE_END_WAR)
			{
				final ControlLink[] controlLinks = getControlLinks();
				
				for (ControlLink controlLink : controlLinks)
				{
					if (controlLink.test(npc, player))
					{
						links.add(controlLink);
					}
				}
			}
		}
		
		links.add(LINK_STATUS);
	}
	
	public void addRegisterGuild(Guild guild)
	{
		if (guild == getOwner())
		{
			return;
		}
		
		getRegisterGuilds().add(guild);
		getEvent().addRegisterGuild(guild);
	}
	
	private void clearRegisterGuilds()
	{
		final Array<Guild> registerGuilds = getRegisterGuilds();
		final RegionWars event = getEvent();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		for (Guild guild : registerGuilds)
		{
			event.removeRegisterGuild(guild);
			dbManager.removeRegionRegisterGuild(this, guild);
		}
		
		registerGuilds.clear();
	}
	
	public Spawn[] getBarriers()
	{
		return barriers;
	}
	
	public long getBattleTime()
	{
		return battleTime;
	}
	
	public Spawn[] getControl()
	{
		return control;
	}
	
	public ControlLink[] getControlLinks()
	{
		return controlLinks;
	}
	
	public Table<Guild, Wrap> getControlTable()
	{
		return controlTable;
	}
	
	public Spawn[] getDefense()
	{
		return defense;
	}
	
	public RegionWars getEvent()
	{
		return event;
	}
	
	public Array<Guild> getGuildKeys()
	{
		return guildKeys;
	}
	
	public int getId()
	{
		return territory.getId();
	}
	
	public long getInterval()
	{
		return interval;
	}
	
	public Spawn[] getManager()
	{
		return manager;
	}
	
	public Array<Spawn> getMonsterSpawns()
	{
		return monsterSpawns;
	}
	
	public String getName()
	{
		return territory.getName();
	}
	
	public Func[] getNegative()
	{
		return negative;
	}
	
	public Guild getOwner()
	{
		return owner;
	}
	
	public int getOwnerId()
	{
		return owner == null ? 0 : owner.getId();
	}
	
	public Func[] getPositive()
	{
		return positive;
	}
	
	public Array<Guild> getRegisterGuilds()
	{
		return registerGuilds;
	}
	
	public Spawn[] getShops()
	{
		return shops;
	}
	
	public long getStartTime()
	{
		return startTime;
	}
	
	public RegionState getState()
	{
		return state;
	}
	
	public int getTax()
	{
		return tax;
	}
	
	public RegionTerritory getTerritory()
	{
		return territory;
	}
	
	public boolean hasOwner()
	{
		return owner != null;
	}
	
	public boolean isRegister(Guild guild)
	{
		return (guild != null) && (registerGuilds.contains(guild) || (guild == owner));
	}
	
	@Override
	public void onDie(Character killer, Character killed)
	{
		if (getState() != RegionState.PREPARE_END_WAR)
		{
			return;
		}
		
		final Class<? extends Character> cs = killed.getClass();
		
		if (cs == RegionWarControl.class)
		{
			final Npc npc = killed.getNpc();
			final RegionWarSpawn spawn = (RegionWarSpawn) npc.getSpawn();
			
			if (!Arrays.contains(getControl(), spawn))
			{
				return;
			}
			
			final Guild guild = killer.getGuild();
			spawn.setOwner(guild);
			final String announce = spawn.getChatLoc() + "<FONT FACE=\"$ChatFont\"> captured " + (guild == null ? " NPC" : " guild \"" + guild + "\"") + "!</FONT>";
			sendAnnounce(announce);
		}
		else if (cs == RegionWarBarrier.class)
		{
			final Npc npc = killed.getNpc();
			final RegionWarSpawn spawn = (RegionWarSpawn) npc.getSpawn();
			
			if (!Arrays.contains(getBarriers(), spawn))
			{
				return;
			}
			
			final Guild guild = killer.getGuild();
			final String announce = spawn.getChatLoc() + "<FONT FACE=\"$ChatFont\"> demolished " + (guild == null ? " NPC" : " guild \"" + guild + "\"") + "!</FONT>";
			sendAnnounce(announce);
		}
	}
	
	@Override
	public void onSelect(Player player)
	{
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			return;
		}
		
		final Array<Guild> registerGuilds = getRegisterGuilds();
		
		if ((guild != getOwner()) && !registerGuilds.contains(guild))
		{
			return;
		}
		
		switch (getState())
		{
			case PREPARE_START_WAR:
			{
				player.sendPacket(CharSay.getInstance(Strings.EMPTY, "Battle for \"" + getName() + "\" will begin at " + nextBattleDate, SayType.NOTICE_CHAT, 0, 0), true);
				break;
			}
			
			case PREPARE_END_WAR:
			{
				if (schedule == null)
				{
					return;
				}
				
				final long endTime = schedule.getDelay(TimeUnit.MINUTES);
				player.sendPacket(CharSay.getInstance(Strings.EMPTY, "Before the end of the battle for \"" + getName() + "\" left " + endTime + " m.", SayType.NOTICE_CHAT, 0, 0), true);
				player.sendPacket(CharSay.getInstance(Strings.EMPTY, "Checkpoints:", SayType.NOTICE_CHAT, 0, 0), true);
				final Spawn[] control = getControl();
				
				for (Spawn element : control)
				{
					final RegionWarSpawn spawn = (RegionWarSpawn) element;
					final Guild owner = spawn.getOwner();
					player.sendPacket(CharSay.getInstance(Strings.EMPTY, spawn.getChatLoc() + "<FONT FACE=\"$ChatFont\" > [" + (owner == null ? "NPC" : owner.getName()) + "]</FONT>", SayType.NOTICE_CHAT, 0, 0), true);
				}
				
				break;
			}
			
			default:
				return;
		}
	}
	
	public void prepare()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.loadRegion(this);
		dbManager.loadRegionRegister(this);
		final Spawn[] manager = getManager();
		
		for (Spawn spawn : manager)
		{
			spawn.start();
		}
		
		final Spawn[] control = getControl();
		
		for (Spawn element : control)
		{
			final RegionWarSpawn spawn = (RegionWarSpawn) element;
			final ControlLink controlLink = new ControlLink(spawn.getName(), spawn, REPLY_CONTROL);
			controlLinks = Arrays.addToArray(controlLinks, controlLink, ControlLink.class);
		}
		
		final Spawn[] shops = getShops();
		
		for (Spawn shop : shops)
		{
			final RegionWarSpawn spawn = (RegionWarSpawn) shop;
			spawn.setRegion(this);
			spawn.start();
		}
		
		final int respawn = (int) ((getBattleTime() / 1000) * 2);
		
		for (Spawn spawn : getDefense())
		{
			final RegionWarSpawn npcSpawn = (RegionWarSpawn) spawn;
			npcSpawn.setRespawnTime(respawn);
			npcSpawn.setRegion(this);
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		nextBattle = startTime;
		
		if (getState() == RegionState.WAIT_WAR)
		{
			final long currentTime = System.currentTimeMillis();
			
			for (int i = 0; nextBattle < currentTime; i++)
			{
				nextBattle = startTime + (interval * i);
			}
			
			nextBattleDate = timeFormat(nextBattle);
			log.info("next battle \"" + getName() + "\" in " + nextBattleDate + ".");
			final long diff = nextBattle - currentTime;
			setState(RegionState.PREPARE_START_WAR);
			schedule = executor.scheduleGeneral(this, diff);
		}
		else if (getState() == RegionState.WAR)
		{
			final long currentTime = System.currentTimeMillis();
			
			for (int i = 0; nextBattle < currentTime; i++)
			{
				nextBattle = startTime + (interval * i);
			}
			
			nextBattle -= interval;
			nextBattleDate = timeFormat(nextBattle);
			setState(RegionState.PREPARE_START_WAR);
			schedule = executor.scheduleGeneral(this, 100);
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.addPlayerSelectListener(this);
	}
	
	private void prepareEndWar()
	{
		if (rewardSchedule != null)
		{
			rewardSchedule.cancel(false);
			rewardSchedule = null;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.removeDieListener(this);
		
		for (Spawn spawn : getBarriers())
		{
			spawn.stop();
		}
		
		for (Spawn spawn : getDefense())
		{
			spawn.stop();
		}
		
		final Array<Spawn> monsterSpawns = getMonsterSpawns();
		
		for (Spawn spawn : monsterSpawns)
		{
			spawn.start();
		}
		
		monsterSpawns.clear();
		final Table<Guild, Wrap> controlTable = getControlTable();
		controlTable.clear();
		final Spawn[] control = getControl();
		int npc = 0;
		
		for (Spawn spawn : control)
		{
			final RegionWarSpawn regionSpawn = (RegionWarSpawn) spawn;
			final Guild guild = regionSpawn.getOwner();
			
			if (guild == null)
			{
				npc++;
			}
			else
			{
				Wrap wrap = controlTable.get(guild);
				
				if (wrap == null)
				{
					wrap = Wraps.newIntegerWrap(0, true);
					controlTable.put(guild, wrap);
				}
				
				wrap.setInt(wrap.getInt() + 1);
			}
			
			spawn.stop();
		}
		
		final Array<Guild> guildKeys = getGuildKeys();
		guildKeys.clear();
		controlTable.keyArray(guildKeys);
		Guild top = null;
		int guild = 0;
		
		for (Guild key : guildKeys)
		{
			final Wrap wrap = controlTable.get(key);
			final int count = wrap.getInt();
			
			if ((count > npc) && (count > guild))
			{
				top = key;
				guild = count;
			}
		}
		
		if (top != null)
		{
			int check = 0;
			
			for (Wrap wrap : controlTable)
			{
				if (wrap.getInt() == guild)
				{
					check++;
				}
			}
			
			if (check > 1)
			{
				npc = 0;
				top = null;
			}
		}
		
		if ((top == null) && (npc == 0))
		{
			sendAnnounce("The battle for \"" + getName() + "\" was a draw, the owner remains the same.");
		}
		else if ((top == null) && (npc > 0))
		{
			removeOwner();
			sendAnnounce("The battle for \"" + getName() + "\" won \"NPC\".");
		}
		else if (top != null)
		{
			final Guild currentOwner = getOwner();
			removeOwner();
			
			if (currentOwner == top)
			{
				sendAnnounce("Guild \"" + top.getName() + "\" defended the region \"" + getName() + "\".");
			}
			else
			{
				sendAnnounce("The battle for \"" + getName() + "\" was won by the guild \"" + top.getName() + "\".");
			}
			
			setOwner(top);
		}
		
		setState(RegionState.WAIT_WAR);
		updateState();
		updateOwner();
		returnPlayers();
		controlTable.apply(WRAP_FOLD_FUNC);
		controlTable.clear();
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, 5000);
		clearRegisterGuilds();
	}
	
	private void prepareStartWar()
	{
		controlTable.clear();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.addDieListener(this);
		final Spawn[] control = getControl();
		sendAnnounce("The battle for \"" + getName() + "\"!");
		sendAnnounce("Checkpoints:");
		final Guild owner = getOwner();
		final RegionWars event = getEvent();
		
		if ((owner != null) && event.isRegisterGuild(owner))
		{
			removeOwner();
		}
		
		final Region[] regions = event.getRegions();
		final Array<Guild> registerGuilds = getRegisterGuilds();
		synchronized (regions)
		{
			for (Region region : regions)
			{
				if ((region == this) || !registerGuilds.contains(region.getOwner()))
				{
					continue;
				}
				
				region.removeOwner();
			}
		}
		final LocalObjects local = LocalObjects.get();
		final Array<Npc> npcs = local.getNextNpcList();
		final Array<Spawn> monsterSpawns = getMonsterSpawns();
		
		for (Spawn spawn : control)
		{
			final RegionWarSpawn regionSpawn = (RegionWarSpawn) spawn;
			regionSpawn.setRegion(this);
			regionSpawn.setOwner(owner);
			final Location loc = spawn.getLocation();
			World.getAround(Npc.class, npcs, loc.getContinentId(), loc.getX(), loc.getY(), loc.getZ(), 0, 0, 3000);
			
			for (Npc npc : npcs)
			{
				if ((npc.getNpcType() == NpcType.RAID_BOSS) || npc.isMinion())
				{
					continue;
				}
				
				final Spawn target = npc.getSpawn();
				target.stop();
				monsterSpawns.add(target);
			}
			
			spawn.start();
			sendAnnounce(regionSpawn.getChatLoc() + "<FONT FACE=\"$ChatFont\" > [" + (owner == null ? "NPC" : owner.getName()) + "]</FONT>");
		}
		
		for (Spawn spawn : getBarriers())
		{
			spawn.start();
		}
		
		if (getOwner() == null)
		{
			for (Spawn spawn : getDefense())
			{
				spawn.start();
			}
		}
		
		setState(RegionState.WAR);
		final RegionTerritory territory = getTerritory();
		synchronized (territory)
		{
			final Array<TObject> objects = territory.getObjects();
			
			for (TObject object : objects)
			{
				if (!object.isPlayer())
				{
					continue;
				}
				
				removeFuncsTo(object.getPlayer());
			}
		}
		updateState();
		returnPlayers();
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, 100);
		rewardSchedule = executor.scheduleAiAtFixedRate(REWARD_TASK, RegionWars.REWARD_INTERVAL, RegionWars.REWARD_INTERVAL);
	}
	
	private synchronized void register(Npc npc, Player player)
	{
		if (!Arrays.contains(getManager(), npc.getSpawn()))
		{
			return;
		}
		
		final GuildRank guildRank = player.getGuildRank();
		
		if (guildRank == null)
		{
			player.sendMessage("You are not in a guild.");
			return;
		}
		
		if (!guildRank.isGuildMaster())
		{
			player.sendMessage("You are not the master of the guild.");
			return;
		}
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			return;
		}
		
		if (guild == getOwner())
		{
			player.sendMessage("And so you are the owner of the region.");
			return;
		}
		
		final RegionWars event = getEvent();
		
		if (event.isRegisterGuild(guild))
		{
			player.sendMessage("You are already registered to another battle.");
			return;
		}
		
		final Array<Guild> registerGuilds = getRegisterGuilds();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		if (!dbManager.insertRegionGuildRegister(this, guild))
		{
			player.sendMessage("There was an error, please contact technical support.");
		}
		else
		{
			registerGuilds.add(guild);
			event.addRegisterGuild(guild);
			
			if (event.isOwnerGuild(guild))
			{
				player.sendMessage("You have successfully signed up for battle, ownership of your region will be canceled at the beginning of the battle.");
			}
			else
			{
				player.sendMessage("You have successfully signed up for battle.");
			}
		}
	}
	
	private void removeFuncsTo(Func[] funcs, Player player)
	{
		if (funcs.length < 1)
		{
			return;
		}
		
		for (Func func : funcs)
		{
			func.removeFuncTo(player);
		}
	}
	
	public void removeFuncsTo(Player player)
	{
		removeFuncsTo(getNegative(), player);
		removeFuncsTo(getPositive(), player);
	}
	
	public void removeOwner()
	{
		final Guild owner = getOwner();
		
		if (owner == null)
		{
			return;
		}
		
		final RegionWars event = getEvent();
		event.removeOwnerGuild(owner);
		getRegisterGuilds().fastRemove(owner);
		setOwner(null);
	}
	
	private void returnPlayers()
	{
		final WorldZoneTable zoneTable = WorldZoneTable.getInstance();
		final RegionTerritory territory = getTerritory();
		synchronized (territory)
		{
			final Array<TObject> objects = territory.getObjects();
			
			for (TObject object : objects)
			{
				if (!object.isPlayer())
				{
					continue;
				}
				
				final Player player = object.getPlayer();
				final Location loc = zoneTable.getRespawn(player);
				player.teleToLocation(loc);
			}
		}
	}
	
	@Override
	protected synchronized void runImpl()
	{
		switch (getState())
		{
			case WAIT_WAR:
				startWaitWar();
				break;
			
			case PREPARE_START_WAR:
				prepareStartWar();
				break;
			
			case WAR:
				startWar();
				break;
			
			case PREPARE_END_WAR:
				prepareEndWar();
		}
	}
	
	public void sendAnnounce(String message)
	{
		sendPacket(CharSay.getInstance(Strings.EMPTY, message, SayType.NOTICE_CHAT, 0, 0));
	}
	
	public void sendPacket(ServerPacket packet)
	{
		final Guild owner = getOwner();
		packet.increaseSends();
		
		if (owner != null)
		{
			owner.sendPacket(null, packet);
		}
		
		final Array<Guild> registerGuilds = getRegisterGuilds();
		registerGuilds.readLock();
		
		try
		{
			final Guild[] array = registerGuilds.array();
			
			for (int i = 0, length = registerGuilds.size(); i < length; i++)
			{
				array[i].sendPacket(null, packet);
			}
		}
		
		finally
		{
			registerGuilds.readUnlock();
		}
		packet.complete();
	}
	
	public void setBarriers(Spawn[] spawn)
	{
		barriers = spawn;
	}
	
	public void setBattleTime(long var)
	{
		battleTime = var;
	}
	
	public void setControl(Spawn[] spawn)
	{
		control = spawn;
	}
	
	public void setDefense(Spawn[] spawn)
	{
		defense = spawn;
	}
	
	public void setInterval(long var)
	{
		interval = var;
	}
	
	public void setManager(Spawn[] var)
	{
		manager = var;
	}
	
	public void setNegative(Func[] func)
	{
		negative = func;
	}
	
	public void setOwner(Guild guild)
	{
		owner = guild;
		
		if (owner != null)
		{
			event.addOwnerGuild(owner);
		}
	}
	
	public void setPositive(Func[] func)
	{
		positive = func;
	}
	
	public void setShops(Spawn[] spawn)
	{
		shops = spawn;
	}
	
	public void setStartTime(long var)
	{
		startTime = var;
	}
	
	public void setState(RegionState var)
	{
		state = var;
	}
	
	public void setTax(int var)
	{
		tax = var;
	}
	
	private void startWaitWar()
	{
		final ExecutorManager executor = ExecutorManager.getInstance();
		final long currentTime = System.currentTimeMillis();
		
		for (int i = 0; nextBattle < currentTime; i++)
		{
			nextBattle = startTime + (interval * i);
		}
		
		nextBattleDate = timeFormat(nextBattle);
		log.info("next battle \"" + getName() + "\" in " + nextBattleDate + ".");
		final long diff = nextBattle - currentTime;
		setState(RegionState.PREPARE_START_WAR);
		schedule = executor.scheduleGeneral(this, diff);
		returnPlayers();
	}
	
	private void startWar()
	{
		setState(RegionState.PREPARE_END_WAR);
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, battleTime);
	}
	
	@SuppressWarnings("incomplete-switch")
	private synchronized void status(Npc npc, Player player)
	{
		switch (getState())
		{
			case PREPARE_START_WAR:
			{
				final StringBuilder builder = new StringBuilder("\nStatus \"");
				builder.append(getName());
				builder.append("\":\n");
				builder.append("Track. battle - ");
				builder.append(nextBattleDate);
				builder.append("\n");
				final Guild owner = getOwner();
				builder.append("Owner - \"").append(owner == null ? "NPC" : owner.getName()).append("\"\n");
				
				if (registerGuilds.isEmpty())
				{
					builder.append("Offensive guild absent.");
				}
				else
				{
					builder.append("Offensive guild:\n");
					final Guild[] array = registerGuilds.array();
					
					for (int i = 0, length = registerGuilds.size() - 1; i <= length; i++)
					{
						final Guild guild = array[i];
						builder.append("\"");
						builder.append(guild);
						builder.append("\"");
						
						if (i < length)
						{
							builder.append(";\n");
						}
						else
						{
							builder.append(".");
						}
					}
				}
				
				player.sendMessage(builder.toString());
				break;
			}
			
			case PREPARE_END_WAR:
			{
				if (schedule == null)
				{
					return;
				}
				
				final StringBuilder builder = new StringBuilder("\nStatus \"");
				builder.append(getName());
				builder.append("\":\n");
				builder.append("Before the battle - ");
				builder.append(schedule.getDelay(TimeUnit.MINUTES));
				builder.append(" m.\n");
				final Guild owner = getOwner();
				final Array<Guild> registerGuilds = getRegisterGuilds();
				builder.append("Defender - \"").append(owner == null ? "NPC" : owner.getName()).append("\"\n");
				
				if (registerGuilds.isEmpty())
				{
					builder.append("Offensive guild absent.");
				}
				else
				{
					builder.append("Offensive guild:\n");
					final Guild[] array = registerGuilds.array();
					
					for (int i = 0, length = registerGuilds.size() - 1; i <= length; i++)
					{
						final Guild guild = array[i];
						builder.append("\"");
						builder.append(guild);
						builder.append("\"");
						
						if (i < length)
						{
							builder.append(";\n");
						}
						else
						{
							builder.append(".");
						}
					}
				}
				
				player.sendMessage(builder.toString());
				break;
			}
		}
	}
	
	public void teleportTo(Player player, Link link)
	{
		final ControlLink controlLink = (ControlLink) link;
		final RegionWarSpawn spawn = controlLink.getSpawn();
		final Location location = spawn.getLocation();
		final int heading = Rnd.nextInt(0, 65000);
		final float newX = Coords.calcX(location.getX(), 100, heading);
		final float newY = Coords.calcY(location.getY(), 100, heading);
		final GeoManager geoManager = GeoManager.getInstance();
		final float newZ = geoManager.getHeight(location.getContinentId(), newX, newY, location.getZ());
		player.teleToLocation(location.getContinentId(), newX, newY, newZ);
	}
	
	private String timeFormat(long time)
	{
		date.setTime(time);
		return timeFormat.format(date);
	}
	
	@Override
	public String toString()
	{
		return "Region  nextBattleDate = " + nextBattleDate + ", state = " + state + ", nextBattle = " + nextBattle;
	}
	
	private synchronized void unregister(Npc npc, Player player)
	{
		if (!Arrays.contains(getManager(), npc.getSpawn()))
		{
			return;
		}
		
		final GuildRank guildRank = player.getGuildRank();
		
		if (guildRank == null)
		{
			player.sendMessage("You are not in a guild.");
			return;
		}
		
		if (!guildRank.isGuildMaster())
		{
			player.sendMessage("You are not the master of the guild.");
			return;
		}
		
		final Guild guild = player.getGuild();
		
		if ((guild == null) || (guild == getOwner()))
		{
			return;
		}
		
		final RegionWars event = getEvent();
		
		if (!event.isRegisterGuild(guild))
		{
			player.sendMessage("You are not logged in any battle.");
			return;
		}
		
		final Array<Guild> registerGuilds = getRegisterGuilds();
		
		if (!registerGuilds.contains(guild))
		{
			player.sendMessage("You have not been registered on this battle.");
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.removeRegionRegisterGuild(this, guild);
		event.removeRegisterGuild(guild);
		registerGuilds.fastRemove(guild);
		player.sendMessage("Your guild has been successfully registered with the battle.");
	}
	
	private void updateOwner()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updateRegionOwner(this);
	}
	
	private void updateState()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updateRegionState(this);
	}
}
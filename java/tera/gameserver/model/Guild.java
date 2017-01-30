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
package tera.gameserver.model;

import tera.Config;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.GuildManager;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.GuildBank;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.CharSay;
import tera.gameserver.network.serverpackets.ServerPacket;
import tera.util.Identified;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Nameable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class Guild implements Nameable, Identified
{
	private static final Logger log = Loggers.getLogger(Guild.class);
	
	private final Table<IntKey, GuildRank> ranks;
	
	private final Array<GuildMember> members;
	
	private final Array<Player> online;
	
	private final Array<GuildLog> logs;
	
	private final Bank bank;
	
	private final String name;
	
	private String title;
	
	private String message;
	
	private final GuildIcon icon;
	
	private GuildMember guildLeader;
	
	private final int id;
	
	private final int level;
	
	public Guild(String name, String title, String message, int id, int level, GuildIcon icon)
	{
		this.name = name;
		this.title = title;
		this.message = message;
		this.id = id;
		this.level = level;
		this.icon = icon;
		members = Arrays.toConcurrentArray(GuildMember.class);
		online = Arrays.toConcurrentArray(Player.class);
		logs = Arrays.toConcurrentArray(GuildLog.class);
		ranks = Tables.newIntegerTable();
		bank = GuildBank.newInstance(this);
	}
	
	public void addMember(GuildMember member)
	{
		members.add(member);
		
		if (member.getRankId() == GuildRank.GUILD_MASTER)
		{
			guildLeader = member;
		}
	}
	
	public void addOnline(Player player)
	{
		online.add(player);
	}
	
	public void addRank(GuildRank rank)
	{
		final Table<IntKey, GuildRank> ranks = getRanks();
		
		if (ranks.containsKey(rank.getIndex()))
		{
			log.warning("found duplicate " + rank + " for guild " + name);
			return;
		}
		
		ranks.put(rank.getIndex(), rank);
	}
	
	public synchronized void changeMemberNote(Player player, String newNote)
	{
		final GuildMember member = getMember(player.getObjectId());
		
		if (member != null)
		{
			final String old = player.getGuildNote();
			player.setGuildNote(newNote);
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			
			if (!dbManager.updatePlayerGuildNote(player))
			{
				player.setGuildNote(old);
			}
			else
			{
				member.setNote(newNote);
				player.updateGuild();
			}
		}
	}
	
	public synchronized void changeRank(Player player, int index, String name, GuildRankLaw law)
	{
		if ((law == null) || (index == GuildRank.GUILD_MEMBER) || (index == GuildRank.GUILD_MASTER))
		{
			return;
		}
		
		final GuildRank rank = ranks.get(index);
		
		if (rank != null)
		{
			rank.setLaw(law);
			rank.setName(name);
			rank.prepare();
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			dbManager.updateGuildRank(this, rank);
			player.updateGuild();
			player.sendMessage("Your rank has changed, reload the guild window.");
		}
	}
	
	public synchronized void createRank(Player player, String name)
	{
		final Table<IntKey, GuildRank> ranks = getRanks();
		
		if ((ranks.size() > 10) || !Config.checkName(name))
		{
			return;
		}
		
		for (GuildRank rank : ranks)
		{
			if (name.equalsIgnoreCase(rank.getName()))
			{
				return;
			}
		}
		
		int index = 4;
		
		while (ranks.containsKey(index))
		{
			index++;
		}
		
		if (index > 128)
		{
			return;
		}
		
		final GuildRank rank = GuildRank.newInstance(name, GuildRankLaw.MEMBER, index);
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.createGuildRank(this, rank);
		ranks.put(index, rank);
		player.updateGuild();
		player.sendMessage("Your rank has changed, reload the guild window.");
	}
	
	public void enterInGame(Player player)
	{
		final Array<GuildMember> members = getMembers();
		final int index = members.indexOf(player);
		
		if (index < 0)
		{
			return;
		}
		
		final GuildMember target = members.get(index);
		
		if (target != null)
		{
			target.setOnline(true);
		}
		
		online.add(player);
	}
	
	public synchronized void exclude(Player excluder, String name)
	{
		final GuildMember member = getMember(name);
		
		if (member == null)
		{
			return;
		}
		
		final GuildRank rank = member.getRank();
		
		if (rank.isGuildMaster())
		{
			return;
		}
		
		member.setRank(null);
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updatePlayerGuild(null, member);
		members.fastRemove(member);
		final Player player = getPlayer(member.getObjectId());
		member.fold();
		
		if (player != null)
		{
			player.setGuild(null);
			player.setGuildRank(null);
			player.updateOtherInfo();
			online.fastRemove(player);
			player.updateGuild();
		}
		
		excluder.updateGuild();
	}
	
	public void exitOutGame(Player player)
	{
		final GuildMember target = getMember(player.getObjectId());
		
		if (target != null)
		{
			target.setOnline(false);
			target.setLastOnline((int) (System.currentTimeMillis() / 1000));
		}
		
		online.fastRemove(player);
	}
	
	public final Bank getBank()
	{
		return bank;
	}
	
	public final GuildIcon getIcon()
	{
		return icon;
	}
	
	public int getId()
	{
		return id;
	}
	
	public GuildMember getLeader()
	{
		return guildLeader;
	}
	
	public final int getLevel()
	{
		return level;
	}
	
	public Array<GuildLog> getLogs()
	{
		return logs;
	}
	
	public GuildMember getMember(int objectId)
	{
		final Array<GuildMember> members = getMembers();
		members.readLock();
		
		try
		{
			final GuildMember[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final GuildMember member = array[i];
				
				if ((member != null) && (member.getObjectId() == objectId))
				{
					return member;
				}
			}
			
			return null;
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public GuildMember getMember(String name)
	{
		final Array<GuildMember> members = getMembers();
		members.readLock();
		
		try
		{
			final GuildMember[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final GuildMember member = array[i];
				
				if ((member != null) && name.equals(member.getName()))
				{
					return member;
				}
			}
			
			return null;
		}
		
		finally
		{
			members.readUnlock();
		}
	}
	
	public final Array<GuildMember> getMembers()
	{
		return members;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public int getObjectId()
	{
		return id;
	}
	
	public Array<Player> getOnline()
	{
		return online;
	}
	
	public Player getPlayer(int objectId)
	{
		final Array<Player> online = getOnline();
		online.readLock();
		
		try
		{
			final Player[] array = online.array();
			
			for (int i = 0, length = online.size(); i < length; i++)
			{
				final Player player = array[i];
				
				if ((player != null) && (player.getObjectId() == objectId))
				{
					return player;
				}
			}
			
			return null;
		}
		
		finally
		{
			online.readUnlock();
		}
	}
	
	public Player getPlayer(String name)
	{
		final Array<Player> online = getOnline();
		online.readLock();
		
		try
		{
			final Player[] array = online.array();
			
			for (int i = 0, length = online.size(); i < length; i++)
			{
				final Player player = array[i];
				
				if ((player != null) && name.equals(player.getName()))
				{
					return player;
				}
			}
			
			return null;
		}
		
		finally
		{
			online.readUnlock();
		}
	}
	
	public GuildRank getRank(int index)
	{
		final Table<IntKey, GuildRank> ranks = getRanks();
		GuildRank rank = ranks.get(index);
		
		if (rank == null)
		{
			rank = ranks.get(GuildRank.GUILD_MEMBER);
		}
		
		if (rank == null)
		{
			log.warning("found incorrect rank table for guild " + name);
		}
		
		return rank;
	}
	
	public Table<IntKey, GuildRank> getRanks()
	{
		return ranks;
	}
	
	public final String getTitle()
	{
		return title;
	}
	
	public synchronized void joinMember(Player player)
	{
		final GuildMember member = GuildMember.newInstance();
		player.setGuild(this);
		player.setGuildRank(getRank(GuildRank.GUILD_MEMBER));
		member.setClassId(player.getClassId());
		member.setLevel(player.getLevel());
		member.setName(player.getName());
		member.setNote(player.getGuildNote());
		member.setObjectId(player.getObjectId());
		member.setOnline(true);
		member.setRaceId(player.getRaceId());
		member.setRank(player.getGuildRank());
		member.setSex(player.getSexId());
		player.updateOtherInfo();
		online.add(player);
		addMember(member);
		player.updateGuild();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updatePlayerGuild(player);
	}
	
	public synchronized void leaveMember(Player player)
	{
		if (player.getGuildRankId() == GuildRank.GUILD_MASTER)
		{
			return;
		}
		
		final int objectId = player.getObjectId();
		final GuildMember member = getMember(objectId);
		
		if (member == null)
		{
			return;
		}
		
		members.fastRemove(member);
		online.fastRemove(player);
		member.fold();
		player.setGuild(null);
		player.setGuildRank(null);
		player.updateOtherInfo();
		player.sendMessage("You left the guild.");
		player.updateGuild();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updatePlayerGuild(player);
	}
	
	public synchronized void makeGuildMaster(Player player, String name)
	{
		final GuildRank current = player.getGuildRank();
		
		if (!current.isGuildMaster())
		{
			return;
		}
		
		final GuildMember playerMember = getMember(player.getObjectId());
		final GuildMember targetMember = getMember(name);
		
		if ((playerMember == null) || (targetMember == null))
		{
			return;
		}
		
		final GuildRank def = getRank(GuildRank.GUILD_MEMBER);
		final GuildRank master = getRank(GuildRank.GUILD_MASTER);
		
		if ((def == null) || (master == null))
		{
			return;
		}
		
		playerMember.setRank(def);
		targetMember.setRank(master);
		setGuildLeader(targetMember);
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updatePlayerGuild(this, playerMember);
		dbManager.updatePlayerGuild(this, targetMember);
		player.setGuildRank(def);
		player.updateGuild();
		final Player target = getPlayer(targetMember.getObjectId());
		
		if (target != null)
		{
			target.setGuildRank(master);
			target.updateGuild();
		}
	}
	
	public synchronized void removeRank(Player player, int rankId)
	{
		final GuildRank rank = getRank(rankId);
		final GuildRank def = getRank(GuildRank.GUILD_MEMBER);
		
		if ((rank == null) || (rank.getIndex() == GuildRank.GUILD_MASTER) || (rank.getIndex() == GuildRank.GUILD_MEMBER))
		{
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.removeGuildRank(this, rank);
		dbManager.removeGuildRankForPlayer(this, def, rank);
		ranks.remove(rankId);
		final Array<GuildMember> members = getMembers();
		members.readLock();
		
		try
		{
			final GuildMember[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final GuildMember member = array[i];
				
				if (member.getRank() == rank)
				{
					member.setRank(def);
				}
			}
		}
		
		finally
		{
			members.readUnlock();
		}
		final Array<Player> online = getOnline();
		online.readLock();
		
		try
		{
			final Player[] array = online.array();
			
			for (int i = 0, length = online.size(); i < length; i++)
			{
				final Player member = array[i];
				
				if (member.getGuildRank() == rank)
				{
					member.setGuildRank(def);
					member.updateGuild();
				}
			}
		}
		
		finally
		{
			online.readUnlock();
		}
	}
	
	public void sendMessage(Player player, String message)
	{
		sendPacket(player, CharSay.getInstance(player.getName(), message, SayType.GUILD_CHAT, player.getObjectId(), player.getSubId()));
	}
	
	public void sendPacket(Player player, ServerPacket packet)
	{
		final Array<Player> online = getOnline();
		online.readLock();
		
		try
		{
			if (online.isEmpty())
			{
				return;
			}
			
			final Player[] array = online.array();
			packet.increaseSends(online.size());
			
			for (int i = 0, length = online.size(); i < length; i++)
			{
				array[i].sendPacket(packet, false);
			}
		}
		
		finally
		{
			online.readUnlock();
		}
	}
	
	public void setGuildLeader(GuildMember guildLeader)
	{
		this.guildLeader = guildLeader;
	}
	
	public synchronized void setMessage(String message)
	{
		this.message = message;
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updateGuildMessage(this);
	}
	
	public synchronized void setTitle(String title)
	{
		this.title = title;
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updateGuildTitle(this);
	}
	
	public final int size()
	{
		return members.size();
	}
	
	public void updateIcon()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.updateGuildIcon(this);
		final GuildManager guildManager = GuildManager.getInstance();
		guildManager.putIcon(icon);
	}
	
	public void updateMember(Player player)
	{
		final int objectId = player.getObjectId();
		GuildMember target = null;
		final Array<GuildMember> members = getMembers();
		members.readLock();
		
		try
		{
			final GuildMember[] array = members.array();
			
			for (int i = 0, length = members.size(); i < length; i++)
			{
				final GuildMember member = array[i];
				
				if (member.getObjectId() == objectId)
				{
					target = member;
					break;
				}
			}
		}
		
		finally
		{
			members.readUnlock();
		}
		
		if (target != null)
		{
			target.setLevel(player.getLevel());
			target.setNote(player.getTitle());
			target.setName(player.getName());
			target.setZoneId(player.getZoneId());
		}
	}
	
	public synchronized void updateRank(Player player, int objectId, int rankId)
	{
		final GuildRank rank = getRank(rankId);
		
		if ((rank == null) || rank.isGuildMaster())
		{
			return;
		}
		
		final GuildMember member = getMember(objectId);
		
		if (member != null)
		{
			if (member.getRankId() == GuildRank.GUILD_MASTER)
			{
				return;
			}
			
			member.setRank(rank);
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			dbManager.updatePlayerGuild(this, member);
		}
		
		final Player online = getPlayer(objectId);
		
		if (online != null)
		{
			online.setGuildRank(rank);
			online.updateGuild();
		}
		
		player.updateGuild();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}

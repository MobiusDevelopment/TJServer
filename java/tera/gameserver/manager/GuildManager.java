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
package tera.gameserver.manager;

import java.util.Iterator;

import tera.gameserver.IdFactory;
import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildIcon;
import tera.gameserver.model.GuildMember;
import tera.gameserver.model.GuildRank;
import tera.gameserver.model.GuildRankLaw;
import tera.gameserver.model.playable.Player;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Strings;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class GuildManager
{
	private static final Logger log = Loggers.getLogger(GuildManager.class);
	
	private static GuildManager instance;
	
	/**
	 * Method getInstance.
	 * @return GuildManager
	 */
	public static GuildManager getInstance()
	{
		if (instance == null)
		{
			instance = new GuildManager();
		}
		
		return instance;
	}
	
	private final Table<IntKey, Guild> guilds;
	
	private final Table<String, GuildIcon> icons;
	
	private GuildManager()
	{
		guilds = Tables.newConcurrentIntegerTable();
		icons = Tables.newConcurrentObjectTable();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.restoreGuilds(guilds);
		int remove = 0;
		
		for (Iterator<Guild> iterator = guilds.iterator(); iterator.hasNext();)
		{
			final Guild guild = iterator.next();
			dbManager.restoreGuildRanks(guild);
			dbManager.restoreGuildBankItems(guild);
			dbManager.restoreGuildMembers(guild);
			putIcon(guild.getIcon());
			
			if ((guild.size() > 1) && (guild.getLeader() != null))
			{
				continue;
			}
			
			dbManager.removeGuild(guild);
			dbManager.removeGuildMembers(guild);
			iterator.remove();
			remove++;
		}
		
		if (remove > 0)
		{
			log.info("remove " + remove + " guilds.");
		}
		
		log.info("loaded " + guilds.size() + " guilds.");
	}
	
	/**
	 * Method createNewGuild.
	 * @param name String
	 * @param leader Player
	 * @return Guild
	 */
	public synchronized Guild createNewGuild(String name, Player leader)
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		if (!dbManager.checkGuildName(name))
		{
			return null;
		}
		
		final IdFactory idFactory = IdFactory.getInstance();
		final Guild guild = new Guild(name, Strings.EMPTY, Strings.EMPTY, idFactory.getNextGuildId(), 1, new GuildIcon(Strings.EMPTY, new byte[0]));
		GuildRank rank = GuildRank.newInstance("GuildMaster", GuildRankLaw.GUILD_MASTER, GuildRank.GUILD_MASTER);
		
		if (!dbManager.createGuildRank(guild, rank))
		{
			return null;
		}
		
		guild.addRank(rank);
		rank = GuildRank.newInstance("Member", GuildRankLaw.MEMBER, GuildRank.GUILD_MEMBER);
		
		if (!dbManager.createGuildRank(guild, rank))
		{
			return null;
		}
		
		guild.addRank(rank);
		
		if (!dbManager.insertGuild(guild))
		{
			return null;
		}
		
		leader.setGuild(guild);
		leader.setGuildRank(guild.getRank(GuildRank.GUILD_MASTER));
		final GuildMember member = GuildMember.newInstance();
		member.setClassId(leader.getClassId());
		member.setLevel(leader.getLevel());
		member.setName(leader.getName());
		member.setObjectId(leader.getObjectId());
		member.setOnline(true);
		member.setRaceId(leader.getRaceId());
		member.setRank(guild.getRank(leader.getGuildRankId()));
		member.setSex(leader.getSexId());
		guild.addMember(member);
		guild.addOnline(leader);
		dbManager.updatePlayerGuild(leader);
		guilds.put(guild.getId(), guild);
		return guild;
	}
	
	/**
	 * Method getGuild.
	 * @param id int
	 * @return Guild
	 */
	public Guild getGuild(int id)
	{
		return guilds.get(id);
	}
	
	/**
	 * Method getIcon.
	 * @param name String
	 * @return GuildIcon
	 */
	public GuildIcon getIcon(String name)
	{
		return icons.get(name);
	}
	
	/**
	 * Method putIcon.
	 * @param icon GuildIcon
	 */
	public void putIcon(GuildIcon icon)
	{
		if (icon.hasIcon())
		{
			icons.put(icon.getName(), icon);
		}
	}
}

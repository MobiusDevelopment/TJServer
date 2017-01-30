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

import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildIcon;
import tera.gameserver.model.GuildRank;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.GuildLoadIcon;

/**
 * @author Ronn
 */
public final class LoadGuildIcon extends AbstractDialog
{
	public static LoadGuildIcon newInstance(Npc npc, Player player)
	{
		final LoadGuildIcon dialog = (LoadGuildIcon) DialogType.GUILD_LOAD_ICON.newInstance();
		dialog.player = player;
		dialog.npc = npc;
		return dialog;
	}
	
	private byte[] icon;
	
	@Override
	public synchronized boolean apply()
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return false;
		}
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			log.warning(this, new Exception("not found guild"));
			return false;
		}
		
		final GuildIcon iconInfo = guild.getIcon();
		iconInfo.setIcon(guild, icon);
		guild.updateIcon();
		player.sendMessage("You have downloaded the emblem of the guild, relog.");
		return true;
	}
	
	@Override
	public void finalyze()
	{
		icon = null;
		super.finalyze();
	}
	
	@Override
	public DialogType getType()
	{
		return DialogType.GUILD_LOAD_ICON;
	}
	
	@Override
	public synchronized boolean init()
	{
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
		
		final Guild guild = player.getGuild();
		
		if (guild == null)
		{
			player.sendMessage(MessageType.YOU_NOT_IN_GUILD);
			return false;
		}
		
		final GuildRank rank = player.getGuildRank();
		
		if (!rank.isGuildMaster())
		{
			player.sendMessage(MessageType.YOU_ARE_NOT_THE_GUILD_MASTER);
			return false;
		}
		
		player.sendPacket(GuildLoadIcon.getInstance(), true);
		return true;
	}
	
	public void setIcon(byte[] icon)
	{
		this.icon = icon;
	}
}
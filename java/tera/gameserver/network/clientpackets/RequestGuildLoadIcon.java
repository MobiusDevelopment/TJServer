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
package tera.gameserver.network.clientpackets;

import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.npc.interaction.dialogs.DialogType;
import tera.gameserver.model.npc.interaction.dialogs.LoadGuildIcon;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class RequestGuildLoadIcon extends ClientPacket
{
	
	private Player player;
	
	private byte[] icon;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	@Override
	protected void readImpl()
	{
		player = owner.getOwner();
		readShort();
		final int size = readShort();
		
		if (size > buffer.remaining())
		{
			log.warning(this, "incorrect load guild icon, size " + size + ", remaining " + buffer.remaining());
			return;
		}
		
		icon = new byte[size];
		buffer.get(icon);
	}
	
	@Override
	protected void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		if ((icon == null) || (icon.length < 5))
		{
			player.sendMessage("Download was unsuccessful.");
			return;
		}
		
		final Dialog dialog = player.getLastDialog();
		
		if ((dialog == null) || (dialog.getType() != DialogType.GUILD_LOAD_ICON))
		{
			return;
		}
		
		final LoadGuildIcon load = (LoadGuildIcon) dialog;
		load.setIcon(icon);
		load.apply();
	}
}

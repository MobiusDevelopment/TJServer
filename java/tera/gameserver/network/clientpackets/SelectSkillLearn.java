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

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.npc.interaction.dialogs.DialogType;
import tera.gameserver.model.npc.interaction.dialogs.SkillShopDialog;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public class SelectSkillLearn extends ClientPacket
{
	private Player player;
	
	private int skillId;
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.ReadeablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	protected void readImpl()
	{
		player = owner.getOwner();
		readInt();
		skillId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Npc npc = player.getLastNpc();
		
		if ((npc == null) || !npc.isInRange(player, 200))
		{
			return;
		}
		
		final Dialog dialog = player.getLastDialog();
		
		if (dialog == null)
		{
			return;
		}
		
		if (dialog.getType() != DialogType.SKILL_SHOP)
		{
			dialog.close();
			return;
		}
		
		final SkillShopDialog shop = (SkillShopDialog) dialog;
		
		if (shop.studySkill(skillId))
		{
			shop.apply();
		}
	}
}

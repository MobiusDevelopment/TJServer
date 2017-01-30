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
package tera.gameserver.model.drop;

import tera.Config;
import tera.gameserver.model.Character;
import tera.gameserver.model.TObject;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;

/**
 * @author Ronn
 */
public final class NpcDrop extends AbstractDrop
{
	private final int templateType;
	
	/**
	 * Constructor for NpcDrop.
	 * @param templateId int
	 * @param templateType int
	 * @param groups DropGroup[]
	 */
	public NpcDrop(int templateId, int templateType, DropGroup[] groups)
	{
		super(templateId, groups);
		this.templateType = templateType;
	}
	
	/**
	 * Method checkCondition.
	 * @param creator TObject
	 * @param owner Character
	 * @return boolean
	 */
	@Override
	protected boolean checkCondition(TObject creator, Character owner)
	{
		if (!creator.isNpc() || !owner.isPlayer())
		{
			return false;
		}
		
		final Npc npc = creator.getNpc();
		final Player player = owner.getPlayer();
		return Math.abs(npc.getLevel() - player.getLevel()) <= Config.WORLD_MAX_DIFF_LEVEL_ON_DROP;
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 * @see tera.gameserver.model.drop.Drop#getTemplateType()
	 */
	@Override
	public int getTemplateType()
	{
		return templateType;
	}
}
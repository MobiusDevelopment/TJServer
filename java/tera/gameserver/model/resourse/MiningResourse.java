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
package tera.gameserver.model.resourse;

import tera.Config;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.network.serverpackets.ResourseIncreaseLevel;
import tera.gameserver.templates.ResourseTemplate;

/**
 * @author Ronn
 */
public class MiningResourse extends ResourseInstance
{
	/**
	 * Constructor for MiningResourse.
	 * @param objectId int
	 * @param template ResourseTemplate
	 */
	public MiningResourse(int objectId, ResourseTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method getChanceFor.
	 * @param player Player
	 * @return int
	 */
	@Override
	public int getChanceFor(Player player)
	{
		final Formulas formulas = Formulas.getInstance();
		return formulas.getChanceCollect(getTemplate().getReq(), player.getMiningLevel());
	}
	
	/**
	 * Method increaseReq.
	 * @param player Player
	 */
	@Override
	public void increaseReq(Player player)
	{
		if (player.getMiningLevel() >= Config.WORLD_MAX_COLLECT_LEVEL)
		{
			return;
		}
		
		player.setMiningLevel(player.getMiningLevel() + 1);
		player.sendPacket(ResourseIncreaseLevel.getInstance(getTemplate().getType(), player.getMiningLevel()), true);
		player.updateInfo();
	}
}
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

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public class DuelPlayer
{
	private final Array<ReuseSkill> reuses;
	private int hp;
	private int mp;
	private int stamina;
	
	public DuelPlayer()
	{
		reuses = Arrays.toArray(ReuseSkill.class);
	}
	
	/**
	 * Method getReuses.
	 * @return Array<ReuseSkill>
	 */
	public Array<ReuseSkill> getReuses()
	{
		return reuses;
	}
	
	/**
	 * Method restore.
	 * @param player Player
	 */
	public void restore(Player player)
	{
		player.setStamina(stamina);
		player.setCurrentHp(hp);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyHpChanged(player);
		player.setCurrentMp(mp);
		eventManager.notifyMpChanged(player);
		final Table<IntKey, ReuseSkill> reuseTable = player.getReuseSkills();
		final Array<ReuseSkill> reuses = getReuses();
		final long current = System.currentTimeMillis();
		
		for (ReuseSkill reuse : reuseTable)
		{
			if (!reuse.isItemReuse() && (reuse.getEndTime() > current) && !reuses.contains(reuse))
			{
				player.enableSkill(player.getSkill(reuse.getSkillId()));
			}
		}
		
		reuses.clear();
	}
	
	/**
	 * Method save.
	 * @param player Player
	 */
	public void save(Player player)
	{
		hp = player.getCurrentHp();
		mp = player.getCurrentMp();
		stamina = player.getStamina();
		final Table<IntKey, ReuseSkill> reuseTable = player.getReuseSkills();
		final Array<ReuseSkill> reuses = getReuses();
		reuses.clear();
		final long current = System.currentTimeMillis();
		
		for (ReuseSkill reuse : reuseTable)
		{
			if (!reuse.isItemReuse() && (reuse.getEndTime() > current))
			{
				reuses.add(reuse);
			}
		}
	}
}
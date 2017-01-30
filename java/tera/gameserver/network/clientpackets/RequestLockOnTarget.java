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

import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;

/**
 * @author Ronn
 */
public class RequestLockOnTarget extends ClientPacket
{
	private Player player;
	private int targetId;
	private int targetSubId;
	private int skillId;
	
	@Override
	protected void readImpl()
	{
		player = owner.getOwner();
		targetId = readInt();
		targetSubId = readInt();
		skillId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final Skill lockOnSkill = player.getLockOnSkill();
		final Skill skill = player.getSkill(skillId);
		
		if ((skill == null) || (lockOnSkill != skill))
		{
			return;
		}
		
		final Character target = World.getAroundById(Character.class, player, targetId, targetSubId);
		
		if (target == null)
		{
			return;
		}
		
		player.addLockOnTarget(target, skill);
	}
}
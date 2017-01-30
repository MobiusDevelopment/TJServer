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

import tera.Config;
import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;

/**
 * @author Ronn
 */
public class RequestUseRushSkill extends ClientPacket
{
	
	private Player player;
	
	private int skillId;
	
	private int heading;
	
	private int targetId;
	
	private int targetSubId;
	
	private float targetX;
	private float targetY;
	private float targetZ;
	
	private float startX;
	private float startY;
	private float startZ;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
		targetId = 0;
		targetSubId = 0;
	}
	
	/**
	 * Method getPlayer.
	 * @return Player
	 */
	public final Player getPlayer()
	{
		return player;
	}
	
	@Override
	protected void readImpl()
	{
		player = owner.getOwner();
		readInt();
		skillId = readInt();
		startX = readFloat();
		startY = readFloat();
		startZ = readFloat();
		heading = readShort();
		targetX = readFloat();
		targetY = readFloat();
		targetZ = readFloat();
		readInt();
		targetId = readInt();
		targetSubId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			return;
		}
		
		final Skill skill = player.getSkill(skillId);
		
		if (skill == null)
		{
			player.sendMessage("You do not own this skill.");
			return;
		}
		
		Character target = null;
		
		if (player.getSquareDistance(startX, startY) > Config.WORLD_MAX_SKILL_DESYNC)
		{
			startX = player.getX();
			startY = player.getY();
		}
		
		if ((targetId > 0) && (targetSubId == Config.SERVER_PLAYER_SUB_ID))
		{
			target = World.getAroundById(Character.class, player, targetId, targetSubId);
		}
		
		player.setTarget(target);
		player.getAI().startCast(startX, startY, startZ, skill, 0, heading, targetX, targetY, targetZ);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "UseRunningSkill skillId = " + skillId + ", heading = " + heading + ", targetX = " + targetX + ", targetY = " + targetY + ", targetZ = " + targetZ + ", startX = " + startX + ", startY = " + startY + ", startZ = " + startZ;
	}
}

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

import rlib.geom.Angles;

public class RequestUseShortSkill extends ClientPacket
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
	
	@Override
	public void finalyze()
	{
		setPlayer(null);
		setTargetId(0);
	}
	
	public int getHeading()
	{
		return heading;
	}
	
	public final Player getPlayer()
	{
		return player;
	}
	
	public float getStartX()
	{
		return startX;
	}
	
	public float getStartY()
	{
		return startY;
	}
	
	public float getStartZ()
	{
		return startZ;
	}
	
	public int getTargetId()
	{
		return targetId;
	}
	
	public int getTargetSubId()
	{
		return targetSubId;
	}
	
	public float getTargetX()
	{
		return targetX;
	}
	
	public float getTargetY()
	{
		return targetY;
	}
	
	public float getTargetZ()
	{
		return targetZ;
	}
	
	@Override
	public void readImpl()
	{
		if (buffer.remaining() < 40)
		{
			return;
		}
		
		setPlayer(getOwner().getOwner());
		setSkillId(readInt());
		setHeading(readShort());
		setStartX(readFloat());
		setStartY(readFloat());
		setStartZ(readFloat());
		setTargetX(readFloat());
		setTargetY(readFloat());
		setTargetZ(readFloat());
		readShort();
		readByte();
		setTargetId(readInt());
		setTargetSubId(readInt());
	}
	
	@Override
	public void runImpl()
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
		
		float startX = getStartX();
		float startY = getStartY();
		final float startZ = getStartZ();
		Character target = null;
		
		if (player.getSquareDistance(startX, startY) > Config.WORLD_MAX_SKILL_DESYNC)
		{
			startX = player.getX();
			startY = player.getY();
		}
		
		final int targetId = getTargetId();
		final int targetSubId = getTargetSubId();
		
		if (targetId > 0)
		{
			target = World.getAroundById(Character.class, player, targetId, targetSubId);
		}
		
		player.setTarget(target);
		float targetX = getTargetX();
		float targetY = getTargetY();
		float targetZ = getTargetZ();
		int heading = getHeading();
		
		if ((target != null) && skill.isCorrectableTarget() && (targetSubId == Config.SERVER_PLAYER_SUB_ID))
		{
			targetX = target.getX();
			targetY = target.getY();
			targetZ = target.getZ() + (target.getGeomHeight() * 0.5F);
			heading = Angles.calcHeading(startX, startY, targetX, targetY);
		}
		
		player.getAI().startCast(startX, startY, startZ, skill, 0, heading, targetX, targetY, targetZ);
	}
	
	public void setHeading(int heading)
	{
		this.heading = heading;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public void setSkillId(int skillId)
	{
		this.skillId = skillId;
	}
	
	public void setStartX(float startX)
	{
		this.startX = startX;
	}
	
	public void setStartY(float startY)
	{
		this.startY = startY;
	}
	
	public void setStartZ(float startZ)
	{
		this.startZ = startZ;
	}
	
	public void setTargetId(int targetId)
	{
		this.targetId = targetId;
	}
	
	public void setTargetSubId(int targetSubId)
	{
		this.targetSubId = targetSubId;
	}
	
	public void setTargetX(float targetX)
	{
		this.targetX = targetX;
	}
	
	public void setTargetY(float targetY)
	{
		this.targetY = targetY;
	}
	
	public void setTargetZ(float targetZ)
	{
		this.targetZ = targetZ;
	}
}
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
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;

public class RequestUseDefenseSkill extends ClientPacket
{
	public static final int DEFENSE_START = 1;
	public static final int DEFENSE_END = 0;
	private Player player;
	private int skillId;
	private int state;
	private int heading;
	private float startX;
	private float startY;
	private float startZ;
	
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	public final Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	@Override
	public void readImpl()
	{
		if (buffer.remaining() < 19)
		{
			return;
		}
		
		setPlayer(getOwner().getOwner());
		setSkillId(readInt());
		setState(readByte());
		setStartX(readFloat());
		setStartY(readFloat());
		setStartZ(readFloat());
		setHeading(readShort());
	}
	
	@Override
	public void runImpl()
	{
		final Player player = getPlayer();
		
		if (player == null)
		{
			return;
		}
		
		final Skill skill = player.getSkill(getSkillId());
		
		if (skill == null)
		{
			player.sendMessage("You do not own this skill.");
			return;
		}
		
		float startX = getStartX();
		float startY = getStartY();
		final float startZ = getStartZ();
		
		if (player.getSquareDistance(startX, startY) > Config.WORLD_MAX_SKILL_DESYNC)
		{
			startX = player.getX();
			startY = player.getY();
		}
		
		player.getAI().startCast(startX, startY, startZ, skill, getState(), getHeading(), player.getX(), player.getY(), player.getZ());
	}
	
	@Override
	public String toString()
	{
		return "RequestUseDefenseSkill state = " + state;
	}
	
	public int getSkillId()
	{
		return skillId;
	}
	
	public int getHeading()
	{
		return heading;
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
	
	public int getState()
	{
		return state;
	}
	
	public void setHeading(int heading)
	{
		this.heading = heading;
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
	
	public void setState(int state)
	{
		this.state = state;
	}
}
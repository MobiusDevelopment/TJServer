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
package tera.gameserver.model.skillengine.classes;

import tera.gameserver.model.Character;
import tera.gameserver.network.serverpackets.MoveSkill;
import tera.gameserver.network.serverpackets.SkillStart;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class Cyclone extends ChargeDam
{
	
	private int state;
	
	/**
	 * Constructor for Cyclone.
	 * @param template SkillTemplate
	 */
	public Cyclone(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method getCastCount.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Skill#getCastCount()
	 */
	@Override
	public int getCastCount()
	{
		return chargeLevel + 1;
	}
	
	/**
	 * Method getDiff.
	 * @return int
	 */
	protected int getDiff()
	{
		return Math.max(1, super.getCastCount() - chargeLevel);
	}
	
	/**
	 * Method getHitTime.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Skill#getHitTime()
	 */
	@Override
	public int getHitTime()
	{
		return getDelay() + (getInterval() * (getCastCount() + 2)) + super.getHitTime();
	}
	
	/**
	 * Method getIconId.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Skill#getIconId()
	 */
	@Override
	public int getIconId()
	{
		return super.getIconId();
	}
	
	/**
	 * Method getMoveDistance.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Skill#getMoveDistance()
	 */
	@Override
	public int getMoveDistance()
	{
		return super.getMoveDistance() / getDiff();
	}
	
	/**
	 * Method getMoveTime.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Skill#getMoveTime()
	 */
	@Override
	public int getMoveTime()
	{
		return Math.max(getHitTime() - super.getMoveTime(), 1);
	}
	
	/**
	 * Method getPower.
	 * @return int
	 * @see tera.gameserver.model.skillengine.Skill#getPower()
	 */
	@Override
	public int getPower()
	{
		return super.getPower() / getDiff();
	}
	
	/**
	 * Method startSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#startSkill(Character, float, float, float)
	 */
	@Override
	public void startSkill(Character attacker, float targetX, float targetY, float targetZ)
	{
		castId = attacker.getCastId();
		chargeLevel = attacker.getChargeLevel();
		state = (template.getStartState() + template.getEndState()) - chargeLevel;
		attacker.broadcastPacket(SkillStart.getInstance(attacker, getIconId(), castId, state));
		
		if (isRush())
		{
			final Character target = attacker.getTarget();
			
			if (target != null)
			{
				attacker.broadcastPacket(MoveSkill.getInstance(attacker, target));
			}
			else
			{
				attacker.broadcastPacket(MoveSkill.getInstance(attacker, targetX, targetY, targetZ));
			}
		}
	}
	
	/**
	 * Method useSkill.
	 * @param character Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#useSkill(Character, float, float, float)
	 */
	@Override
	public void useSkill(Character character, float targetX, float targetY, float targetZ)
	{
		state++;
		super.useSkill(character, targetX, targetY, targetZ);
		character.broadcastPacket(SkillStart.getInstance(character, template.getIconId(), castId, state));
	}
}

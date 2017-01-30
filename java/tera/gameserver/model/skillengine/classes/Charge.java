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

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.RequestSkillStart;
import tera.gameserver.network.serverpackets.SkillEnd;
import tera.gameserver.network.serverpackets.SkillStart;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class Charge extends AbstractSkill
{
	
	private int charge;
	
	/**
	 * Constructor for Charge.
	 * @param template SkillTemplate
	 */
	public Charge(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method endSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param force boolean
	 * @see tera.gameserver.model.skillengine.Skill#endSkill(Character, float, float, float, boolean)
	 */
	@Override
	public void endSkill(Character attacker, float targetX, float targetY, float targetZ, boolean force)
	{
		template.removeCastFuncs(attacker);
		
		if (force || attacker.isAttackBlocking() || attacker.isOwerturned())
		{
			attacker.broadcastPacket(SkillEnd.getInstance(attacker, castId, template.getId()));
			attacker.setChargeSkill(null);
			return;
		}
		
		attacker.setCastId(castId);
		attacker.setChargeLevel(getCharge());
		attacker.sendPacket(RequestSkillStart.getInstance(template.getId() + template.getOffsetId()), true);
	}
	
	/**
	 * Method isWaitable.
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Skill#isWaitable()
	 */
	@Override
	public boolean isWaitable()
	{
		return false;
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
		super.startSkill(attacker, targetX, targetY, targetZ);
		setCharge(template.getStartState());
		attacker.setChargeSkill(this);
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
		setCharge(getCharge() + 1);
		final Skill skill = character.getSkill(template.getId() + getCharge());
		
		if ((skill == null) || !skill.checkCondition(character, targetX, targetY, targetZ))
		{
			character.abortCast(false);
			return;
		}
		
		character.broadcastPacket(SkillStart.getInstance(character, template.getId(), castId, getCharge()));
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		
		if (skill.getMpConsume() > 0)
		{
			final int resultMp = character.getCurrentMp() - skill.getMpConsume();
			character.setCurrentMp(resultMp);
			eventManager.notifyMpChanged(character);
		}
		
		if (skill.getHpConsume() > 0)
		{
			final int resultHp = character.getCurrentHp() - skill.getHpConsume();
			character.setCurrentHp(resultHp);
			eventManager.notifyHpChanged(character);
		}
	}
	
	/**
	 * Method getCharge.
	 * @return int
	 */
	public int getCharge()
	{
		return charge;
	}
	
	/**
	 * Method setCharge.
	 * @param charge int
	 */
	public void setCharge(int charge)
	{
		this.charge = charge;
	}
}
